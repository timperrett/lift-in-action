package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,SessionVar}
import net.liftweb.http.js.JsCmds.{Alert}
import net.liftweb.http.js.jquery.JqJsCmds.FadeIn

class BasicAjax {
  
  object ExampleVar extends SessionVar[String]("Replace Me")
  
  def one(xhtml: NodeSeq): NodeSeq = 
    SHtml.a(() => Alert("You clicked me!"), Text("Go on, click me"))
    
  def two(xhtml: NodeSeq): NodeSeq = 
    SHtml.ajaxEditable(
      Text(ExampleVar.is), 
      SHtml.text(ExampleVar.is, ExampleVar(_)), 
      () => FadeIn("example_two_notice"))
    
}