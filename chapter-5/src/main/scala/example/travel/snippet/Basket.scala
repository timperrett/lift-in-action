package example.travel
package snippet

import scala.xml.NodeSeq
import net.liftweb.http.DispatchSnippet
import net.liftweb.util.Helpers._
import example.travel.model.Customer
import example.travel.lib.AuctionHelpers

class Basket extends AuctionHelpers {
  private lazy val items = 
    Customer.currentUser.flatMap(_.order.map(_.order_auctions.all)).openOr(Nil)
  
  // def items =
  //   ""
  
  def items(xhtml: NodeSeq): NodeSeq = items.flatMap(oa => 
    bind("a", single(oa.auction.obj,xhtml))
  )
  
  def chooser(xhtml: NodeSeq): NodeSeq = {
    val template = if(items.isEmpty) "empty" else "full"
    bind("b",chooseTemplate("basket",template,xhtml))
  }
}
