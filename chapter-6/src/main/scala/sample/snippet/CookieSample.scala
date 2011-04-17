package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Full,Empty,Box}
import net.liftweb.util.Helpers._
import net.liftweb.http.{DispatchSnippet,S,SHtml}
import net.liftweb.http.provider.HTTPCookie

// listing 6.13
class CookieSample extends DispatchSnippet {
  override def dispatch = {
    case "add" => add 
    case "delete" => delete 
    case _ => display _
  }
  
  private val cookieName = "liftinaction.sample"
  
  private def action(does: String, using: () => Any) = 
    "*" #>  SHtml.submit(does, () => {using(); S.redirectTo(S.uri)})
  
  def delete = action("Delete Cookie", 
    () => S.deleteCookie(cookieName))
  
  def add = action("Create Cookie", () => S.addCookie(
      HTTPCookie(cookieName,"I love cookies")))
  
  def display(xhtml: NodeSeq) = S.findCookie(cookieName).map { cookie => 
      Text("Cookie found!: %s".format(cookie))
    } openOr Text("No cookie set.")
}