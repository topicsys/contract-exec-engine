import scala.collection.{immutable, mutable}

object SuanfaTest extends App {
  // 给字符串去重复
  val s = "ABCDABCDABCの"

  //////////////////////////////////////////////////////////
  // 第一种方法：
  val chars = s.toCharArray
  val bools = new Array[Boolean](chars.length)
  for ((c, i) <- chars.zipWithIndex) {
    var j = i + 1
    while (j < chars.length) {
      if (chars(j) == c) bools(j) = true
      j += 1
    }
  }
  var seq: List[Char] = Nil
  for ((b, i) <- bools.zipWithIndex) {
    if (!b) seq ::= chars(i)
  }
  val dest = new String(seq.reverse.toArray)
  println(dest)

  //////////////////////////////////////////////////////////
  // 第二种方法：
  val builder = StringBuilder.newBuilder
  for (i <- s.indices) {
    val c = s.charAt(i)
    val k = s.indexOf(c)
    if (i == k || k == s.lastIndexOf(c)) {
      builder.append(c)
    }
  }
  println(builder.toString)

  //////////////////////////////////////////////////////////
  // 注意a认识b，但b不一定也认识a。求分成两组，要求互相认识，且两组人数量差不多。
  // 题目分析：求互相认识的两组人，就是给出的人必然是互相认识的了。只是有少数人a认识b，但b不认识a。

  val person = immutable.Map.newBuilder[Int, Set[Int]]
  person += (1 -> Set(2, 3, 4))
  person += (2 -> Set(1))
  person += (3 -> Set(1, 2, 5))
  person += (4 -> Set(1))
  person += (5 -> Set(1, 2))

  grouping(person.result)

  /**
    * @param person 某人认识的人
    */
  def grouping(person: Map[Int, Set[Int]]) = {
    // 认识某人k的人
    def search(k: Int) = (for ((key, set) <- person if set(k)) yield key).toSet

    // 互相认识的人的集合，但是有重复，如：1 -> Set(3), 3 -> Set(1)
    val aba = for {
      (key, set) <- person
      ba = search(key) & set
      if ba.nonEmpty
      kv = key -> ba
    } yield kv

    val map = mutable.Map.empty[Int, Set[Int]]
    aba.foreach { case (k, v) =>
      map += k -> v
    }
    // 去重复
    aba.foreach { case (k, v) =>
      v.foreach { elem =>
        if (elem != k && aba.contains(elem) && aba(elem).size == 1)
          map -= elem
      }
    }

    val num: Int = person.size / 2

    map
  }
}
