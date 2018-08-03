import org.objectweb.asm._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen}
import systems.topic.lee.Dsl

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
    val stream = tag.runtimeClass.getResourceAsStream(tag.runtimeClass.getSimpleName + ".class")
    val cr = try new ClassReader(stream) finally stream.close()
    cr.accept(new ClassPrinter, 0)
  }

  Feature("ASM") {
    Scenario("test:") {
      // 两种方式取得 stream：
      // 1. classOf[Dsl].getResourceAsStream("/" + classOf[String].getName.replace('.', '/') + ".class")
      // 路径必须这样："Dsl.class" 或 "/systems/topic/cee/Dsl.class"

      // 2. ClassLoader.getSystemResourceAsStream(classOf[String/*JDK中的包*/].getName.replace('.', '/') + ".class")
      // 路径前面不能有："/"。

      println()
      println(ClassLoader.getSystemResourceAsStream(classOf[String /*JDK中的包*/ ].getName.replace('.', '/') + ".class"))

      printClazz[Bazhang]
      printClazz[Dsl]
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
  import Opcodes._

  override def visitInsn(opcode: Int): Unit = {
    val codeName = opcode match {
      case NOP => "NOP"
      case ACONST_NULL => "ACONST_NULL"
      case ICONST_M1 => "ICONST_M1"
      case ICONST_0 => "ICONST_0"
      case ICONST_1 => "ICONST_1"
      case ICONST_2 => "ICONST_2"
      case ICONST_3 => "ICONST_3"
      case ICONST_4 => "ICONST_4"
      case ICONST_5 => "ICONST_5"
      case LCONST_0 => "LCONST_0"
      case LCONST_1 => "LCONST_1"
      case FCONST_0 => "FCONST_0"
      case FCONST_1 => "FCONST_1"
      case FCONST_2 => "FCONST_2"
      case DCONST_0 => "DCONST_0"
      case DCONST_1 => "DCONST_1"
      case IALOAD => "IALOAD"
      case LALOAD => "LALOAD"
      case FALOAD => "FALOAD"
      case DALOAD => "DALOAD"
      case AALOAD => "AALOAD"
      case BALOAD => "BALOAD"
      case CALOAD => "CALOAD"
      case SALOAD => "SALOAD"
      case IASTORE => "IASTORE"
      case LASTORE => "LASTORE"
      case FASTORE => "FASTORE"
      case DASTORE => "DASTORE"
      case AASTORE => "AASTORE"
      case BASTORE => "BASTORE"
      case CASTORE => "CASTORE"
      case SASTORE => "SASTORE"
      case POP => "POP"
      case POP2 => "POP2"
      case DUP => "DUP"
      case DUP_X1 => "DUP_X1"
      case DUP_X2 => "DUP_X2"
      case DUP2 => "DUP2"
      case DUP2_X1 => "DUP2_X1"
      case DUP2_X2 => "DUP2_X2"
      case SWAP => "SWAP"
      case IADD => "IADD"
      case LADD => "LADD"
      case FADD => "FADD"
      case DADD => "DADD"
      case ISUB => "ISUB"
      case LSUB => "LSUB"
      case FSUB => "FSUB"
      case DSUB => "DSUB"
      case IMUL => "IMUL"
      case LMUL => "LMUL"
      case FMUL => "FMUL"
      case DMUL => "DMUL"
      case IDIV => "IDIV"
      case LDIV => "LDIV"
      case FDIV => "FDIV"
      case DDIV => "DDIV"
      case IREM => "IREM"
      case LREM => "LREM"
      case FREM => "FREM"
      case DREM => "DREM"
      case INEG => "INEG"
      case LNEG => "LNEG"
      case FNEG => "FNEG"
      case DNEG => "DNEG"
      case LSHL => "LSHL"
      case ISHR => "ISHR"
      case IUSHR => "IUSHR"
      case LUSHR => "LUSHR"
      case IAND => "IAND"
      case LAND => "LAND"
      case IOR => "IOR"
      case LOR => "LOR"
      case IXOR => "IXOR"
      case LXOR => "LXOR"
      case I2L => "I2L"
      case I2F => "I2F"
      case I2D => "I2D"
      case L2I => "L2I"
      case L2F => "L2F"
      case L2D => "L2D"
      case F2I => "F2I"
      case F2L => "F2L"
      case F2D => "F2D"
      case D2I => "D2I"
      case D2L => "D2L"
      case D2F => "D2F"
      case I2B => "I2B"
      case I2C => "I2C"
      case I2S => "I2S"
      case LCMP => "LCMP"
      case FCMPL => "FCMPL"
      case FCMPG => "FCMPG"
      case DCMPL => "DCMPL"
      case DCMPG => "DCMPG"
      case IRETURN => "IRETURN"
      case LRETURN => "LRETURN"
      case FRETURN => "FRETURN"
      case DRETURN => "DRETURN"
      case ARETURN => "ARETURN"
      case RETURN => "RETURN"
      case ARRAYLENGTH => "ARRAYLENGTH"
      case ATHROW => "ATHROW"
      case MONITORENTER => "MONITORENTER"
      case MONITOREXIT => "MONITOREXIT"
      case _ => "xxx"
    }
    println(s"[visitInsn]----->>>$codeName(:$opcode)")
    super.visitInsn(opcode)
  }
}

//静态内部类
class Bazhang(a: Int) {
  private def f(n: Int, s: String, arr: Array[Int]): Int = {
    (new Dsl).value
  }

  private def hi(a: Double, b: List[String]): List[Int] = {
    val label = new Label
    5 :: Nil
  }
}
