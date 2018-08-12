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

package systems.topic

import hobby.chenai.nakam.lang.J2S.char2String
import systems.topic.lee.lib.ClazzUtil._

import scala.reflect.ClassTag

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
package object feint extends GetPackage {
  def isFeint[C](implicit tag: ClassTag[C]): Boolean = isFeint(tag.runtimeClass.getName)

  def isFeint(clazzName: String): Boolean = {
    require(clazzName.isName)
    clazzName.startsWith(pkgPre)
  }

  def deFeint(clazzName: String): String = if (isFeint(clazzName)) clazzName.substring(pkgPre.length) else clazzName

  implicit class ImpFeint(clazzName: String) {
    @inline def isFeint: Boolean = feint.isFeint(clazzName)

    @inline def deFeint: String = feint.deFeint(clazzName)
  }

  object asm {
    def feint(name: String, check: Boolean = true): String = {
      if (check) require(name.isAsmName)
      if (name.startsWith(pkgPrefixJdk) && !name.isExceptedAsm) pkgPrefixFeint + name else name
    }

    def feintSignature(signature: String, check: Boolean = true): String = {
      if (check) require(signature.isAsmSignature)
      val array: Array[String] = signature.split(tpePrefixClazz)
      // 这种`split`方式无法通过`isAsmName`检查（有空字符串，还有其它符号`$regex4Sig`），但简便高效。
      array.map(feint(_, check = false)).mkString(tpePrefixClazz)
    }.ensuring(s => s.startsWith(tpePrefixClazz) || s.startsWith(sigPrefixClazz))

    def isFeint(namePath: String): Boolean = {
      require(namePath.isAsmName)
      namePath.startsWith(pkgPrefixFeint)
    }

    def deFeint(namePath: String): String = if (isFeint(namePath)) namePath.substring(pkgPrefixFeint.length) else namePath

    implicit class ImpFeint(clazzName: String) {
      @inline def feint: String = if (clazzName.isAsmSignature) asm.feintSignature(clazzName, check = false) else asm.feint(clazzName)

      @inline def isFeint: Boolean = asm.isFeint(clazzName)

      @inline def deFeint: String = asm.deFeint(clazzName)
    }
  }
}
