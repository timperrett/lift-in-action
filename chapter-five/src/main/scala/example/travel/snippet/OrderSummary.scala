package example {
package travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import example.travel.model.Customer
  
  class OrderSummary {
    private val order = Customer.currentUser.flatMap(_.order)
    private val totalValue = order.map(_.totalValue).openOr(0D)
    private val reference = order.map(_.reference.is).openOr("n/a")
    
    def value(xhtml: NodeSeq): NodeSeq = Text(totalValue.toString)
    
    def shipping(xhtml: NodeSeq): NodeSeq = Customer.currentUser.flatMap(_.order.map(order =>
      bind("s",xhtml,
        "address_one" -> order.shippingAddressOne.is,
        "address_two" -> order.shippingAddressTwo.is,
        "city" -> order.shippingAddressCity.is,
        "postcode" -> order.shippingAddressPostalCode.is
      ))).openOr(NodeSeq.Empty)
    
    def paynow(xhtml: NodeSeq): NodeSeq =
      <form name="_xclick" action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
        <input type="hidden" name="cmd" value="_xclick" />
        <input type="hidden" name="business" value="me@mybusiness.com" />
        <input type="hidden" name="currency_code" value="GBP" />
        <input type="hidden" name="item_name" value={"Order " + reference} />
        <input type="hidden" name="amount" value={totalValue.toString} />
        <input type="image" src="images/x-click-but23.gif" name="submit" alt="" />
      </form>
    
    
  }
  
}}}