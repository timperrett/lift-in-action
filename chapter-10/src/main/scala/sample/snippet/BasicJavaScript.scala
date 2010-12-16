package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.js.JsCmds.{Alert,Script,SetHtml} 

class BasicJavascript {
  def one(xhtml: NodeSeq): NodeSeq = 
    Script(Alert("Important Alert Goes Here!"))
  
  def two(xhtml: NodeSeq): NodeSeq = Script(
    SetHtml("replaceme",Text("I have been replaced!")) &
    Alert("Text Replaced")
  ) 
}