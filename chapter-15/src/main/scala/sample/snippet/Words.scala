package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.{DispatchSnippet,ContainerVar,SHtml}
import net.liftweb.util.Helpers._

class Words extends DispatchSnippet {
  object WordHolder extends ContainerVar[String]("n/a")
  
  def dispatch = {
    case "update" => update 
    case "show" => show _
  }
  
  def update = 
    "type=text" #> SHtml.text(WordHolder.is, WordHolder(_)) &
    "type=submit" #> SHtml.submit("Update >>", () => println("Submitted!"))
  
  def show(xhtml: NodeSeq): NodeSeq = Text(WordHolder.is)
}
