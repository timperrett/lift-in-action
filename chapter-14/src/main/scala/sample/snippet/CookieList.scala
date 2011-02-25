package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S,DispatchSnippet}

class CookieList extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = 
    S.receivedCookies.flatMap(c => Text(c.name) ++ <br />)
}