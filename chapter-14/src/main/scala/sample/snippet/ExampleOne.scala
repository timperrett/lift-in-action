package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
// import net.liftweb.http.DispatchSnippet

class ExampleOne {
  def render = {
    "p *" replaceWith "sample!"
  }
    
}
