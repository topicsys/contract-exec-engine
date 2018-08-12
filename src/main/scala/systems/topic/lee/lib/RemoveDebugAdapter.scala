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

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM6

/**
  * 本类重写的方法都是`debug`用途，不影响运行，可以删除。
  *
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 06/08/2018
  */
class RemoveDebugAdapter(cv: ClassVisitor) extends ClassVisitor(ASM6, cv) {
  override def visitSource(source: String, debug: String): Unit = {}

  override def visitOuterClass(owner: String, name: String, desc: String): Unit = {}

  override def visitInnerClass(name: String, outerName: String, innerName: String, access: Int): Unit = {}
}
