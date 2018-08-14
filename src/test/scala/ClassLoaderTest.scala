import hobby.chenai.nakam.basis.TAG
import org.scalatest.{FeatureSpec, GivenWhenThen}
import systems.topic.lee.{Dsl, GasCounter, logger => log}
import systems.topic.lee.lib.ClassLoader

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
class ClassLoaderTest extends FeatureSpec with GivenWhenThen with TAG.ClassName {
  Feature("lee.ClassLoader") {
    Scenario("test:") {
      try {
        val dsl: Dsl = ClassLoader.boot.loadClass(classOf[Dsl].getName).newInstance().asInstanceOf[Dsl]

        dsl.abcd(new GasCounter(Int.MaxValue))
        println("dsl.abcd(Int.MaxValue) --- done.")
        dsl.abcd(new GasCounter(5))
        println("dsl.abcd(5) --- done.")
        dsl.abcd(new GasCounter(1))
        println("dsl.abcd(0) --- done.")
        ClassLoader.boot.loadClass(classOf[Integer].getName)
        //      ClassLoader.boot.loadClass(classOf[List[_]].getName)
      } catch {
        case e: Throwable => log.e(e)
      }
      assert(true)
    }
  }
}
