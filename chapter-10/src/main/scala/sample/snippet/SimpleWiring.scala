package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.util.ValueCell
import net.liftweb.http.{SHtml,WiringUI}
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.jquery.JqWiringSupport

class FormulaWiring {
  
  private val productValue = ValueCell[Box[Double]](Empty)
  private val taxValue = productValue.lift(_.map(x => (x/100d*17.5d)).openOr(0d))
  private val totalValue = productValue.lift(_.map(x => (x+taxValue.currentValue._1)).openOr(0d))
  
  def product = {
    "#value" #> SHtml.ajaxText(
      productValue.map(_.toString).openOr(""), 
      v => { asDouble(v).pass(productValue.set(_)); Noop })
  }
  
  def tax(xhtml: NodeSeq): NodeSeq = 
    WiringUI.asText(xhtml, taxValue.lift(_.toString), JqWiringSupport.fade)
  
  def total(xhtml: NodeSeq): NodeSeq = 
    WiringUI.asText(xhtml, totalValue.lift(_.toString), JqWiringSupport.fade)
  
}