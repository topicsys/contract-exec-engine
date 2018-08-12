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

package systems.topic.lee.lib

import hobby.chenai.nakam.lang.J2S.{NonFlat$, NonNull}
import org.objectweb.asm.{ClassVisitor, Opcodes}
import systems.topic.feint.asm._

/**
  * 针对 JDK 的类不可以调用`ClassLoader`的`defineClass()`，需要通过伪装的方式更改。
  *
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
class FeintJdkAdapter(cv: ClassVisitor) extends ClassVisitor(Opcodes.ASM6, cv) {
  override def visit(version: Int, access: Int, name: String, signature: String, superName: String, interfaces: Array[String]): Unit = {
    println(s"--->>> visit(access:$access, name:${name.feint}, sign:${if (signature.isNull) signature else signature.feint}," +
      s" super:${superName.feint}, interfaces:${interfaces.map(_.feint).mkString$})")

    super.visit(version, access, name.feint, if (signature.isNull) signature else signature.feint, superName.feint, interfaces.map(_.feint))
  }
}
