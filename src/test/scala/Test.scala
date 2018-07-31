import hobby.wei.c.reflow.Reflow
import hobby.wei.c.reflow.implicits._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FeatureSpec, GivenWhenThen}

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 31/07/2018
  */
class Test extends /*AsyncFeatureSpec*/ FeatureSpec with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll {
  override protected def beforeAll(): Unit = {
  }

  //  示例：
  //  Feature("异步执行代码") {
  //    Scenario("也可以这样写") {
  //      Given("一段代码")
  //      lazy val someCodes: () => String = () => {
  //        // do something ...
  //        Thread.sleep(10)
  //        outputStr
  //      }
  //      When("提交")
  //      val future = Reflow.submit(someCodes())(SHORT)
  //      Then("代码被异步执行")
  //      info("输出：" + future.get)
  //      assertResult(outputStr)(future.get)
  //    }
  //  }

  Feature("test") {
    Scenario("test:") {
      Reflow.submit(println("----->>> Test."))(TRANSIENT)
      assert(true)
    }
  }
}
