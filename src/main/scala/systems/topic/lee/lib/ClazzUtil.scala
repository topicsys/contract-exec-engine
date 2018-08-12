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

import hobby.chenai.nakam.lang.J2S.char2String

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 12/08/2018
  */
object ClazzUtil {
  //org.objectweb.asm.Type.BOOLEAN_TYPE.getDescriptor
  implicit class Name$Path(np: String) {
    @inline def isName: Boolean = !np.contains(/) && !np.contains(`.clas`) && (np.count(_ == *) >= 1)

    @inline def isPath: Boolean = np.startsWith(/) && (np.charAt(1) != /) && np.endsWith(`.clas`) &&
      (np.count(_ == *) == 1 && np.indexOf(*) == np.length - `.clas`.length) && (np.count(_ == /) > 1)

    @inline def isAsmName: Boolean = !np.startsWith(/) && !np.contains(*) && !np.contains(`.clas`) && (np.count(_ == /) >= 1)

    @inline def isAsmType: Boolean = np.startsWith(tpePrefixClazz) && isAsmName && np.endsWith(tpeSuffixClazz)

    @inline def isAsmSignature: Boolean = (np.startsWith(tpePrefixClazz) || np.startsWith(sigPrefixClazz)) &&
      np.split(regex4Sig).filterNot(_.isEmpty).map(_ + tpeSuffixClazz).forall(_.isAsmType)

    @inline def toPath: String = {
      var path = np
      if (path.contains(/)) {
        if (!path.startsWith(/)) path = / + path
        if (!path.endsWith(`.clas`)) {
          require(!path.contains(*))
          path = path.concat(`.clas`)
        }
      } else {
        require(path.contains(*))
        require(!path.endsWith(`.clas`))
        path = / + path.replace(*, /).concat(`.clas`)
      }
      path
    }

    @inline def toName: String = {
      val name = np.replace(`.clas`, "").replaceAll(s"\\${/}+", *)
      if (name.startsWith(*)) name.substring(1) else name
    }

    @inline def toAsmName: String = {
      val name = np.replace(`.clas`, "").replaceAll(s"\\${*}+", /)
      if (name.startsWith(/)) name.substring(1) else name
    }

    @inline def isExcepted: Boolean = {
      require(np.isName)
      implantExceptedList.exists(np.startsWith)
    }

    @inline def isExceptedAsm: Boolean = {
      require(np.isAsmName)
      asmImplantExcepted.exists(np.startsWith) // 为了兼容`asm.feintSignature()`的`signature.split(tpePrefixClazz)`
    }
  }

  trait GetPackage {
    lazy val pkgPre = pkg + *
    lazy val pkg = {
      val name = this.getClass.getName
      name.substring(0, name.lastIndexOf(*))
    }
  }

  val / = '/'
  val * = '.'
  val `.clas` = * + "class"

  val pkgPrefixJdk = "java" + /
  val pkgPrefixFeint = systems.topic.feint.pkgPre.replace(*, /)
  val tpePrefixClazz = 'L'
  val tpeSuffixClazz = ';'
  val sigPrefixClazz = '<'
  // 正则表达式需要兼容以下几种Class文件`签名(Signature)`：
  // <T:Ljava/lang/Object;>Ljava/lang/Object;
  // Ljava/lang/Number;Ljava/lang/Comparable<Ljava/lang/Integer;>;
  // <A:Ljava/lang/Object;>Ljava/lang/Object;Lscala/collection/mutable/Traversable<TA;>;Lscala/collection/Iterable<TA;>;
  val regex4Sig = "<\\w+(:|;)[>;]*|[;<>]+"

  lazy val implantExceptedList = Seq(classOf[Object], classOf[Throwable], classOf[Exception],
    classOf[InterruptedException]).map(_.getName) :+ systems.topic.lee.pkg
  lazy val asmImplantExcepted = implantExceptedList.map(_.toAsmName)
}
