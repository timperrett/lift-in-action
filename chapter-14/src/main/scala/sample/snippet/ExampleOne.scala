package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.DispatchSnippet

class ExampleOne extends DispatchSnippet {
  def dispatch = {
    case "render" => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = 
    NodeSeq.Empty
}