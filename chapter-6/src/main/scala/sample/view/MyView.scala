package sample.view

import scala.xml._
import net.liftweb.http.LiftView

class MyView extends LiftView { 
  override def dispatch = { 
    case "sample" => render _ 
  } 
  def render: NodeSeq = <h1>Test</h1>
}
