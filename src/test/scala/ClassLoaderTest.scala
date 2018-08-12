import org.scalatest.{FeatureSpec, GivenWhenThen}
import systems.topic.lee.{Dsl, GasCounter}
import systems.topic.lee.lib.ClassLoader

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
class ClassLoaderTest extends FeatureSpec with GivenWhenThen {
  Feature("lee.ClassLoader") {
    Scenario("test:") {
      val dsl: Dsl = ClassLoader.boot.loadClass(classOf[Dsl].getName).newInstance().asInstanceOf[Dsl]
      //      ClassLoader.boot.loadClass(classOf[Integer].getName)

      dsl.abcd(new GasCounter(Int.MaxValue))
      assert(true)
    }
  }
}
