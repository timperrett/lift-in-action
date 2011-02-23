package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.S
import net.liftweb.util.Helpers._
// import net.liftweb.http.DispatchSnippet

class ExampleOne {
  def render = {
    "p *" replaceWith "sample!"
  }
}
