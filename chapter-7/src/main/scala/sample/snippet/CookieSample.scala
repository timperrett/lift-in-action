package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Full,Empty,Box}
import net.liftweb.util.Helpers._
import net.liftweb.http.{DispatchSnippet,S,SHtml}
import net.liftweb.http.provider.HTTPCookie

// listing 7.15
class CookieSample extends DispatchSnippet {
  override def dispatch = {
    case "add" => add _
    case "delete" => delete _
    case _ => display _
  }
  
  private val cookieName = "liftinaction.sample"
  
  private def action(does: String, using: () => Any, xhtml: NodeSeq) = 
    bind("c",xhtml, 
    "button" -> SHtml.submit(does, 
      () => {using(); S.redirectTo(S.uri)}))
  
  def delete(xhtml: NodeSeq): NodeSeq = 
    action("Delete Cookie", () => S.deleteCookie(cookieName), xhtml)
  
  def add(xhtml: NodeSeq): NodeSeq = 
    action("Create Cookie", () => S.addCookie(
      HTTPCookie(cookieName,"I love cookies")), xhtml)
  
  def display(xhtml: NodeSeq): NodeSeq = 
    S.findCookie(cookieName).map { cookie => 
      Text("Cookie found!: %s".format(cookie))
    } openOr Text("No cookie set.")
  
}