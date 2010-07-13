package example {
package travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.paypal.snippet.BuyNowSnippet
  import net.liftweb.util.Helpers._
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
    
    def shipping(xhtml: NodeSeq): NodeSeq = Customer.currentUser.flatMap(_.order.map(order =>
      bind("s",xhtml,
        "address_one" -> order.shippingAddressOne.is,
        "address_two" -> order.shippingAddressTwo.is,
        "city" -> order.shippingAddressCity.is,
        "postcode" -> order.shippingAddressPostalCode.is
      ))).openOr(NodeSeq.Empty)
    
  }
  
}}}