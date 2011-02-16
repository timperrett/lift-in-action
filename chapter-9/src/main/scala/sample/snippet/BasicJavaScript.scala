package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JsCmds.{Alert,Script,Run,SetHtml,JsIf} 
import net.liftweb.http.js.JE.{JsNotEq,Num}

class BasicJavascript {
  def one(xhtml: NodeSeq): NodeSeq = 
    Script(Alert("1: Important Alert Goes Here!"))
  
  def two(xhtml: NodeSeq): NodeSeq = Script(
    SetHtml("replaceme",Text("I have been replaced!")) &
    Alert("2: Text Replaced")
  )
  
  def three(xhtml: NodeSeq): NodeSeq = 
    Script(Run(
      JsIf(JsNotEq(Num(1), Num(2)), Alert("3: 1 does not equal 2!")).toJsCmd
    ))
  
}