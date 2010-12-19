package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.util.ValueCell
import net.liftweb.http.{SHtml,WiringUI}
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.jquery.JqWiringSupport

class SimpleWiring {
  
  object CarManufacturers extends Enumeration {
    val Ferrari, Bugatti, Lamborghini, Pagani = Value
  }
  
  val radios = SHtml.ajaxRadio(CarManufacturers.values.toList.sortBy(_.id).map(_.id), 
    Empty, (v:Int) => { selection.set(Full(v)); Noop })
  
  private val selection = ValueCell[Box[Int]](Empty)
  private val chosen = selection.lift(_.map(i => 
    "I love %s super cars".format(CarManufacturers(i).toString)
  ).openOr("You dont like super cars"))
  
  def manufacturers = {
    ".line *" #> radios.map(ci => 
      ci.xhtml ++ Text(CarManufacturers(ci.key).toString))
  }
  
  def favorite(xhtml: NodeSeq): NodeSeq = 
    WiringUI.asText(xhtml, chosen, JqWiringSupport.fade)
  
}