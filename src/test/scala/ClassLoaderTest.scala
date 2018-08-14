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
        ClassLoader.boot.loadClass(classOf[Integer].getName)
        ClassLoader.boot.loadClass(classOf[List[_]].getName)
        val dsl: Dsl = ClassLoader.boot.loadClass(classOf[Dsl].getName).newInstance().asInstanceOf[Dsl]

        print("dsl.abcd(gas = Int.MaxValue) --->")
        dsl.abcd(new GasCounter(Int.MaxValue))
        println(" [DONE].")
        print("dsl.abcd(gas = 5000) --->")
        dsl.abcd(new GasCounter(5000))
        println(" [DONE].")
        print("dsl.abcd(gas = 3763) --->")
        dsl.abcd(new GasCounter(3763))
        println(" [DONE].")
        print("dsl.abcd(gas = 3762) --->")
        dsl.abcd(new GasCounter(3762))
        println(" [DONE].")
      } catch {
        case e: Throwable =>
          println(" " + e.getMessage)
          println()
          log.e(e)
      }
      assert(true)
    }
  }
}
