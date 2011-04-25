package example.travel
package snippet

import scala.xml.NodeSeq
import net.liftweb.http.DispatchSnippet
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel
import example.travel.model.Customer
import example.travel.lib.AuctionHelpers

class Basket extends AuctionHelpers {
  private lazy val contents = 
    Customer.currentUser.flatMap(_.order.map(_.order_auctions.all)).openOr(Nil)
  
  def items = contents.map(x => single(x.auction.obj))
  
  def chooser(xhtml: NodeSeq): NodeSeq = {
    val template = if(contents.isEmpty) "empty" else "full"
    chooseTemplate("basket",template,xhtml)
  }
}
