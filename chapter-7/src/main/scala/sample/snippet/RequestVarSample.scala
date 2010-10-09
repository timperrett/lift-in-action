package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,SHtml,DispatchSnippet}

object sample extends RequestVar[Box[String]](Empty)

object RequestVarSample extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = bind("f",xhtml,
    "value" -> SHtml.text(sample.is.openOr(""), v => sample(Box.!!(v))),
    "submit" -> SHtml.submit("Submit", () => println(sample.is))
  )
}