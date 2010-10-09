package sample.snippet 

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.DispatchSnippet

// listing 7.7
class InstanceExample {
  def howdy(xhtml: NodeSeq) = Text("Hello world")
}

object SingletonExample extends DispatchSnippet {
  def dispatch = {
    case _ => howdy _
  }
  def howdy(xhtml: NodeSeq) = Text("Hello world")
}  
