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
import org.objectweb.asm.{ClassReader, ClassWriter}
import sbt.io.IO
import systems.topic.lee.Dsl
import systems.topic.lee.lib.ClassLoader._
import systems.topic.lee.lib.ClazzUtil._

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * 能够从 JDK 父类（如`java.lang.Object`等）完全隔离。
  *
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
class ClassLoader(parent: java.lang.ClassLoader) extends java.lang.ClassLoader(
  if (parent.isInstanceOf[ClassLoader] || !inited) parent else boot) {
  private[ClassLoader] def this() = this(boot)

  // jar 文件的加载是这么来的：sun.net.www.protocol.jar.JarURLConnection.JarURLInputStream.JarURLInputStream

  @throws[ClassNotFoundException]
  override def findClass(name: String) = {
    println(s">>>findClass($name):${int.incrementAndGet}")
    val clazz = inflated.computeIfAbsent(name, (_: String) => {
      loadClazzByStream(name) { in =>
        try {
          val data = IO.readBytes(in)

          val cr = new ClassReader(data)
          val cw = new ClassWriter(0)
          val cjpa = new FeintJdkAdapter(cw)
          cr.accept(cjpa, 0)
          val bytes = cw.toByteArray

          defineClass(null, bytes, 0, bytes.length)
        } catch {
          case e: Throwable =>
            println(s"\n[error]findClass---($name):${int.incrementAndGet}::${e.getMessage}\n")
            throw e
        } finally {
          in.close()
        }
      }
    })
    //    if (name.contains("Object")) println(s"$name == ${classOf[Object].getName}" +
    //      s"->cl:${clazz.getClassLoader},${classOf[Object].getClassLoader}," +
    //      s"\n ${clazz == classOf[Object]}\n")

    println(s">>>findClass($clazz)<<<\n")
    clazz
  }

  @throws[ClassNotFoundException]
  override def loadClass(name: String) = {
    println(s"loadClass($name):${int.incrementAndGet}")
    if (name.isExcepted) super.loadClass(name)
    else findClass(name)
  }

  @throws[ClassNotFoundException]
  override def loadClass(name: String, resolve: Boolean) = {
    println(s"loadClass($name, $resolve):${int.incrementAndGet}")
    super.loadClass(name, resolve)
  }

  // override def findResource(name: String) = super.findResource(name)

  // override def findResources(name: String) = super.findResources(name)
}

object ClassLoader {
  @volatile private[ClassLoader] var inited: Boolean = _

  lazy val boot = {
    inited = false
    val cl = new ClassLoader(sys)
    inited = true
    cl
  }

  def sys = java.lang.ClassLoader.getSystemClassLoader

  private[ClassLoader] lazy val inflated = new ConcurrentHashMap[String, Class[_]]

  def loadClazzByStream[A, T](f: InputStream => A)(implicit tag: ClassTag[T]): A = {
    // val input = tag.runtimeClass.getResourceAsStream(tag.runtimeClass.getSimpleName + `.clas`)
    loadClazzByStream(tag.runtimeClass.getName)(f)
  }

  def loadClazzByStream[A](name: String)(f: InputStream => A): A = {
    import systems.topic.feint.deFeint
    import systems.topic.feint.asm.ImpFeint
    val dft = if (name.isPath) name.toAsmName.deFeint else if (name.isAsmName) name.deFeint else deFeint(name)
    println(s"[loadClazzByStream]name:$name, real:${dft.toPath}")
    val input = classOf[Dsl].getResourceAsStream(dft.toPath)
    try f(input) finally input.close()
  }

  implicit def clazz2Tag[A](clazz: Class[A]): ClassTag[A] = ClassTag(clazz)

  // TODO: 4 test，待删除。
  val int = new AtomicInteger(0)
}
