package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.S
import net.liftweb.util.Helpers._

class AttributeExample {
  def thing(xhtml: NodeSeq): NodeSeq = {
    val stuff = S.attr("extraStuff", _.toBoolean) openOr false
    val count = S.attr("fictionalCount", _.toInt) openOr 0
    Text("extraStuff: %s fictionalCount: %s".format(stuff,count))
  }
}
