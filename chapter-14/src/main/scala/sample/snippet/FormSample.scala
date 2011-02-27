package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.http.{DispatchSnippet,SHtml}
import net.liftweb.util.Helpers._

class FormSample extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  
  var name = ""
  
  def render(xhtml: NodeSeq): NodeSeq = {
    (".text" #> SHtml.text(name, name = _) &
    "type=submit" #> SHtml.onSubmitUnit(() => println("Submitted " + name)))(xhtml)
  }
}
