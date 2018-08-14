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

package systems.topic.lee

import java.util.concurrent.atomic.AtomicLong
import hobby.wei.c.anno.proguard.Keep$
import systems.topic.lee.GasCounter.GasOutOfLimitException

import scala.annotation.meta.getter

/**
  * 虽然设计上，只用于单线程（不同线程需要独立创建实例），但用于多线程也是安全的。
  *
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 01/08/2018
  */
@Keep$
final class GasCounter(@(Keep$@getter) val limit: Long) {
  require(limit >= 0)
  private val counter = new AtomicLong(0)

  @Keep$
  @throws[GasOutOfLimitException]
  def ++ : Long = this + 1

  @Keep$
  @throws[GasOutOfLimitException]
  def +(i: Int): Long = {
    val count = counter.addAndGet(i)
    require(count > 0)
    if (count > limit) throw new GasOutOfLimitException(count, limit, s"[GAS out of LIMIT] count: $count, limit: $limit.")
    count
  }

  @Keep$
  def count: Long = counter.get

  @Keep$
  def isOutOfGas: Boolean = count > limit
}

object GasCounter {
  @Keep$
  class GasOutOfLimitException(val count: Long, val limit: Long, message: String) extends LogicExecEngineRuntimeException(message)
}
