package example.travel
package snippet 

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.paypal.snippet.BuyNowSnippet
import example.travel.model.Customer

class OrderSummary extends BuyNowSnippet {
  override def dispatch = {
    case "paynow" => buynow _
    case "value" => value _
    case "shipping" => shipping _
  }
  
  val order = Customer.currentUser.flatMap(_.order)
  val amount = order.map(_.totalValue).openOr(0D)
  val reference = order.map(_.reference.is.toString).openOr("n/a")
  
  override val values = Map(
    "business" -> "seller_1278962623_biz@getintheloop.eu",
    "item_number" -> reference,
    "item_name" -> ("Auction Order: " + reference))
  
  def value(xhtml: NodeSeq): NodeSeq = Text(amount.toString)
  
  def shipping(xhtml: NodeSeq): NodeSeq = order.map(o =>
    bind("s",xhtml,
      "address_one" -> o.shippingAddressOne.is,
      "address_two" -> o.shippingAddressTwo.is,
      "city" -> o.shippingAddressCity.is,
      "postcode" -> o.shippingAddressPostalCode.is
    )).openOr(NodeSeq.Empty)
  
}
