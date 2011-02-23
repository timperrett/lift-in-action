package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.S

class CookieList {
  def render(xhtml: NodeSeq): NodeSeq = 
    S.receivedCookies.flatMap(c => Text(c.name) ++ <br />)
}