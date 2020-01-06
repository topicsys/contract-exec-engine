/*
 * MIT License
 *
 * Copyright (c) 2018 topic.systems
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package systems.topic.lee.lib

import java.io.InputStream
import java.util
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap
import hobby.chenai.nakam.basis.TAG
import hobby.chenai.nakam.lang.J2S.NonNull
import hobby.wei.c.log.Logger._
import org.objectweb.asm.{ClassReader, ClassWriter}
import sbt.io.IO
import systems.topic.feint.ImpFeint
import systems.topic.lee.{logger => log}
import systems.topic.lee.lib.ClassLoader.{boot, count, feintMap, inflated, loadClazzAsStream}
import systems.topic.lee.lib.ClazzUtil.Name$Path

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
 * 能够从 JDK 父类（如`java.lang.Object`等）完全隔离。
 *
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 12/08/2018
 */
class ClassLoader(parent: java.lang.ClassLoader) extends AbsCLoaderJ(if (
  parent.isInstanceOf[ClassLoader] || !ClassLoader.inited) parent else boot) with TAG.ClassName {
  def this() = this(boot)

  @throws[ClassNotFoundException]
  override def findClass(name: String) = {
    val bytes = loadClazzAsStream(name, this) { in =>
      val data = IO.readBytes(in)
      if (name.isExcepted) {
        log.d("\n**********>>> findClass[excepted] | %s | %s.", name.s, count.incrementAndGet)
        data
      } else {
        log.d("\n**********>>> findClass | %s | %s.", name.s, count.incrementAndGet)

        // TODO: hack it here!
        //  不仅要改`name`，类实体里面的`import`等也要改。

        val cr = new ClassReader(data)
        val cw = new ClassWriter(0)
        cr.accept(new FeintJdkAdapter(cw, feintMap), 0)
        cw.toByteArray
      }
    }
    log.d("defineClass --->")
    // `defineClass()`的时候会发起`loadClass()`的递归调用。
    val clazz = defineClass(null, bytes, 0, bytes.length)
    log.d("findClass | %s.", clazz)
    clazz
  }

  @throws[ClassNotFoundException]
  override def loadClass(name0: String, resolve: Boolean) = {
    val name = name0.deFeint
    log.d("loadClass | name:%s, name.deFeint:%s | %s, %s.", name0.s, name.s, resolve, count.incrementAndGet)
    if (name.isExcepted) {
      val clazz = super.loadClass(name, resolve)
      log.d("loadClass.super | %s [DONE].\n\n", clazz)
      clazz
    } else {
      var loaded, prev: Class[_] = null
      getClassLoadingLock(name).synchronized {
        loaded = findLoadedClass(name)
        if (loaded.isNull) {
          val feint = feintMap.get(name)
          if (feint.nonNull) loaded = inflated.get(feint)
          if (loaded.isNull) {
            loaded = findClass(name)
            if (feint.nonNull) assert(feint == loaded.getName)
            feintMap.putIfAbsent(name, loaded.getName)
            // 不可以用`computeIfAbsent()`，递归会造成死锁。
            prev = inflated.putIfAbsent(loaded.getName, loaded)
            if (resolve) resolveClass(loaded)
          }
        } else log.d("findLoadedClass | %s.", loaded)
      }
      log.d("loadClass | loaded:%s, prev:%s, [DONE].\n\n", loaded, prev)
      loaded
    }
  }

  // override def findResource(name: String) = super.findResource(name)
  // override def findResources(name: String) = super.findResources(name)
}

object ClassLoader extends TAG.ClassName {
  @volatile private[ClassLoader] var inited: Boolean = _

  // jar 文件的加载是这么来的：sun.net.www.protocol.jar.JarURLConnection.JarURLInputStream.JarURLInputStream
  lazy val boot = {
    inited = false
    val cl = new ClassLoader(app)
    inited = true
    cl
  }

  // 实际上是`AppClassLoader`，见：`sun.misc.Launcher`。
  // `AppClassLoader.parent`为`ExtClassLoader`，而`ExtClassLoader.parent`
  // 虽然为`null`，但效果上为`BootstrapClassLoader`，见：`ClassLoader.loadClass(String name, boolean resolve)`。
  lazy val sys = java.lang.ClassLoader.getSystemClassLoader

  /* 输出结果为（Scala 库提供的）：
   URLClassLoader with NativeCopyLoader with RawResources(
    urls = List(/home/.../target/scala-2.12/logic-exec-engine_2.12-0.0.1-SNAPSHOT-tests.jar, ...), ...,
    parent = DualLoader(a = java.net.URLClassLoader@5b0abc94, b = java.net.URLClassLoader@2ddc8ecb)
  )*/
  // 但有个大问题：它们再往上的`parent`并没有指向`sys`(AppClassLoader)，而是为`null`(sbt.internal.inc.classpath.DualLoader/NullLoader)。
  lazy val app = getClass.getClassLoader

  private[ClassLoader] lazy val inflated = new util.HashMap[String, Class[_]]
  private[ClassLoader] lazy val feintMap = new ConcurrentHashMap[String, String]

  @throws[ClassNotFoundException]
  def loadClazzAsStream[A](name: String, loader: ClassLoader)(f: InputStream => A): A = {
    // 只有使用这种方式的时候不需要去掉开头的`/`: getClass.getResourceAsStream(path).
    val path = name.toPath.substring(1)
    log.d("loadClazzAsStream | name:%s, real:%s.", name.s, path.s)
    // 注意：以下两个`ClassLoader`的`parent`并没有接起来。详见上面的注释。
    var input = loader.getParent.getResourceAsStream(path)
    log.d("loadClazzAsStream | from[loader.getParent] stream:%s, res:%s.", input, boot.getResource(path))

    if (input.isNull) throw new ClassNotFoundException(path)
    try f(input) finally input.close()
  }

  implicit def clazz2Tag[A](clazz: Class[A]): ClassTag[A] = ClassTag(clazz)

  // TODO: 4 test，待删除。
  val count = new AtomicInteger(0)
}
