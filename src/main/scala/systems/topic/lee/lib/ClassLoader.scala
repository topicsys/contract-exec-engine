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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import hobby.chenai.nakam.basis.TAG
import hobby.chenai.nakam.lang.J2S.NonNull
import hobby.wei.c.log.Logger._
import org.objectweb.asm.{ClassReader, ClassWriter}
import sbt.io.IO
import systems.topic.lee.{logger => log}
import systems.topic.lee.lib.ClassLoader.{boot, inflated, count, loadClazzAsStream}
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
    log.d("\n**********>>> findClass | %s | %s.", name.s, count.incrementAndGet)
    loadClazzAsStream(name) { in =>
      try {
        val data = IO.readBytes(in)

        // TODO: hack it here!

        val cr = new ClassReader(data)
        val cw = new ClassWriter(0)
        cr.accept(new FeintJdkAdapter(cw), 0)
        val bytes = cw.toByteArray

        val clazz = defineClass(null, bytes, 0, bytes.length)
        log.d("findClass | %s.", clazz)
        clazz
      } catch {
        case e: Throwable => log.e(e)
          throw e
      } finally {
        in.close()
      }
    }
  }

  @throws[ClassNotFoundException]
  override def loadClass(name: String) = {
    log.d("loadClass | %s | %s.", name.s, count.incrementAndGet)
    if (name.isExcepted) {
      val clazz = super.loadClass(name)
      log.d("loadClass.super | %s [DONE].\n\n", clazz)
      clazz
    } else {
      var loaded, prev: Class[_] = null
      getClassLoadingLock(name).synchronized {
        // 不能在这里用`findLoadedClass()`，因为有可能会改类名使其不起作用。
        loaded = inflated.get(name) // findLoadedClass(name)
        if (loaded == null) {
          loaded = findClass(name)
          // 不可以用`computeIfAbsent()`，递归会造成死锁。
          prev = inflated.putIfAbsent(name, loaded)
        } else log.d("findLoadedClass | %s.", loaded)
      }
      log.d("loadClass | %s [DONE].\n\n", if (prev.nonNull) prev else loaded)
      if (prev.nonNull) prev else loaded
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

  private[ClassLoader] lazy val inflated = new ConcurrentHashMap[String, Class[_]]

  def loadClazzByStream[A, T](f: InputStream => A)(implicit tag: ClassTag[T]): A = {
    // val input = tag.runtimeClass.getResourceAsStream(tag.runtimeClass.getSimpleName + `.clas`)
    loadClazzAsStream(tag.runtimeClass.getName)(f)
  }

  def loadClazzAsStream[A](name: String)(f: InputStream => A): A = {
    import systems.topic.feint.deFeint
    import systems.topic.feint.asm.ImpFeint
    val dft = if (name.isPath) name.toAsmName.deFeint else if (name.isAsmName) name.deFeint else deFeint(name)
    val path = dft.toPath.substring(1) // 只有使用这种方式的时候不需要去掉开头的`/`: getClass.getResourceAsStream(path).
    log.d("loadClazzByStream | name:%s, real:%s.", name.s, path.s)
    // 注意：以下两个`ClassLoader`的`parent`并没有接起来。详见上面的注释。
    var input = boot.getResourceAsStream(path)
    log.d("loadClazzByStream | from loader[boot]stream:%s, res:%s.", input, boot.getResource(path))
    if (input.isNull) {
      input = sys.getResourceAsStream(path)
      log.d("loadClazzByStream | from loader[sys]stream:%s, res:%s.", input, sys.getResource(path))
    }
    try f(input) catch {
      case e: Throwable => log.e(e)
        throw e
    } finally input.close()
  }

  implicit def clazz2Tag[A](clazz: Class[A]): ClassTag[A] = ClassTag(clazz)

  // TODO: 4 test，待删除。
  val count = new AtomicInteger(0)
}
