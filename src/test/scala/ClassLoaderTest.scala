import java.lang
import java.util.concurrent.ConcurrentHashMap
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
        val oClaz = ClassLoader.boot.loadClass("java.lang.Object")
        val dsl: Dsl = ClassLoader.boot.loadClass("systems.topic.lee.Dsl").newInstance().asInstanceOf[Dsl]
        // TODO: 动态加载进来的 class 只能用反射去调用方法。
        val concHashMap = ClassLoader.boot.loadClass("java.util.concurrent.ConcurrentHashMap")
        val concHashMap1 = ClassLoader.boot.loadClass("java.util.concurrent.ConcurrentHashMap")//.newInstance()
        // val comparablIns = ClassLoader.boot.loadClass("java.lang.Comparable").newInstance()

        println(s"object.classloader: ${oClaz.getClassLoader}")
        println(s"dsl.classloader: ${dsl.getClass.getClassLoader}")
        println(s"ConcHashMap.getName: ${classOf[ConcurrentHashMap[_, _]].getName}")
        println(s"class(concHashMap).getName: ${concHashMap.getName}")
        println(s"ConcHashMap.classloader: ${classOf[ConcurrentHashMap[_, _]].getClassLoader}")
        println(s"class(concHashMap).classloader: ${concHashMap.getClassLoader}")
        println(s"class(concHashMap) eq ConcHashMap.class: ${concHashMap eq classOf[Comparable[_]]}")
        println(s"class(concHashMap) == ConcHashMap.class: ${concHashMap == classOf[Comparable[_]]}")
        println(s"class(concHashMap) == class(concHashMap1): ${concHashMap == concHashMap1}")

        println("++++++")
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
