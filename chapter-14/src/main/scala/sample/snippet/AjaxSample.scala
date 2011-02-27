package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.{DispatchSnippet,SHtml}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.Helpers._

class AjaxSample extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = 
    SHtml.ajaxButton("Hello", () => SetHtml("ajax_button",Text("Clicked"))) % ("id" -> "clickme")
}