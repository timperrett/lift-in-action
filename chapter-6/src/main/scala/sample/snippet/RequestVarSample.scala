package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,SHtml,DispatchSnippet}

object sample extends RequestVar[Box[String]](Empty)

object RequestVarSample extends DispatchSnippet {
  def dispatch = {
    case _ => render 
  }
  def render = {
    "type=text" #> SHtml.text(
      sample.is.openOr(""), 
      v => sample(Box.!!(v))) &
    "type=submit" #> SHtml.onSubmitUnit(() => println(sample.is))
  }
}