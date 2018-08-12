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

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 31/07/2018
  */
class Dsl {
  val value = 12345

  abcd(new GasCounter(100))

  @throws[InterruptedException]
  def abcd(counter: GasCounter): Boolean = {
    //    counter.++

    try
      Thread.sleep(1000)
    catch {
      case e: InterruptedException =>
        e.printStackTrace()
        throw e
    } finally {
      print("x")
    }

    xyz()

    if (counter.++ / 2 == 0)
      counter.isOutOfGas
    else false
  }

  @native def xyz(): Unit

  //  org.objectweb.asm.Opcodes.ALOAD
  //   scala.tools.asm.Opcodes.ALOAD
  //  org.apache.bcel.Const.ALOAD
}
