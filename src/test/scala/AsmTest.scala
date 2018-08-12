import org.objectweb.asm._
import org.objectweb.asm.util.{CheckClassAdapter, Printer, TraceClassVisitor}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen}
import systems.topic.lee.{Dsl, GasCounter}

import scala.reflect.ClassTag

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 31/07/2018
  */
class AsmTest extends /*AsyncFeatureSpec*/ FeatureSpec with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll {
  override protected def beforeAll(): Unit = {
  }

  def printClazz[T](implicit tag: ClassTag[T]): Unit = {
    println()
    println()
    println("[printClazz]===>>>" + tag.runtimeClass.getName)
    println()
    val stream = // classOf[Dsl].getResourceAsStream("/" + tag.runtimeClass.getName.replace('.', '/') + ".class")
    tag.runtimeClass.getResourceAsStream(tag.runtimeClass.getSimpleName + ".class")
    val cr = try new ClassReader(stream) finally stream.close()

    val cw = new ClassWriter(0)

    // Textifier 默认，用于输出解析的内容。
    // ASMifier 用于输出要填写的ASM代码内容。
    val cca = new CheckClassAdapter(cw)
    val tcv = new TraceClassVisitor(cca, /*new ASMifier,*/ System.console().writer())
    cr.accept(tcv, 0)

    cw.toByteArray
  }

  Feature("ASM") {
    Scenario("test:") {
      // 两种方式取得 stream：
      // 1. classOf[Dsl].getResourceAsStream("/" + classOf[String].getName.replace('.', '/') + ".class")
      // 路径必须这样："Dsl.class" 或 "/systems/topic/cee/Dsl.class"

      // 2. ClassLoader.getSystemResourceAsStream(classOf[String/*JDK中的包*/].getName.replace('.', '/') + ".class")
      // 路径前面不能有："/"。

      println()
      //      println(ClassLoader.getSystemResourceAsStream(classOf[Object /*JDK中的包*/ ].getName.replace('.', '/') + ".class"))

      //      printClazz[Bazhang]
      //      printClazz[String]
      printClazz[Object]
      //      printClazz[Dsl]
      // TODO: 必须重启，否则更改的类不会刷新。
      // System.exit(0)

      assert(true)
    }
  }
}

class ClassPrinter extends ClassVisitor(Opcodes.ASM6) {
  override def visit(version: Int, access: Int, name: String, signature: String, superName: String, interfaces: Array[String]): Unit = {
    super.visit(version, access, name, signature, superName, interfaces)
    //打印出父类name和本类name
    println("[visit]----->>>" + superName + " " + name)
  }

  override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
    //打印出方法名和类型签名
    println("[visitMethod]----->>>" + name + " " + desc)
    new MethodPrinter(super.visitMethod(access, name, desc, signature, exceptions))
  }
}

class MethodPrinter(mv: MethodVisitor) extends MethodVisitor(Opcodes.ASM6, mv) {
  override def visitCode(): Unit = {
    println("[visitCode]----->>><<<")
    super.visitCode()
  }

  override def visitInsn(opcode: Int): Unit = {
    println(s"[visitInsn]----->>>${Printer.OPCODES(opcode)}(:$opcode)")
    super.visitInsn(opcode)
  }
}

//静态内部类
class Bazhang(gc: GasCounter, a: Int) {
  private def f(n: Int, s: String, arr: Array[Int]): Int = {

    val o = new Object
    o.equals()
    o.hashCode()

    gc.++
    (new Dsl).value
  }

  private def hi(a: Double, b: List[String]): List[Int] = {
    gc.++
    val label = new Label
    gc.++
    5 :: Nil
  }
}
