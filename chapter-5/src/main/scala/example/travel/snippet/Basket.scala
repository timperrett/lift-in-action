package example.travel
package snippet

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import example.travel.model.Customer
import example.travel.lib.AuctionHelpers

class Basket extends AuctionHelpers {
  private lazy val contents = 
    Customer.currentUser.flatMap(_.order.map(_.order_auctions.all)).openOr(Nil)
  
  def items = "full *" #> contents.map(x => single(x.auction.obj)) andThen
    "%s ^*".format(if(contents.isEmpty) "empty" else "full") #> NodeSeq.Empty
}
