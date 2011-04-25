package example.travel
package snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S,StatefulSnippet,SHtml}
import net.liftweb.http.js.JsCmds.{Noop}
import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
import example.travel.model.{Auction,Bid,Customer}
import example.travel.lib.AuctionInstanceHelpers
import example.travel.comet.{AuctionServer,NewBid,CurrentAuction}

class Details extends StatefulSnippet with AuctionInstanceHelpers with Loggable {
  
  override def dispatch = {
    case "show" => show
    case "bid" => bid
  }
  
  lazy val auction: Box[Auction] = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
  
  def bid = {
    var amount: Box[String] = Empty
    def submit = boxToNotice("Your bid was accepted!", "Unable to place bid at this time."){
      for {
        a <- auction
        b <- a.barter(amount)
        c <- amount
        d <- tryo(c.toDouble)
      } yield AuctionServer ! NewBid(a.id.is, d, S.session.map(_.uniqueId))
    }
    "type=text" #> SHtml.text(amount.openOr(""), s => amount = Box.!!(s)) &
    "type=submit" #> SHtml.ajaxSubmit("Place Bid", submit _) andThen SHtml.makeFormsAjax
  }
  
  def show = {
    S.session.map(_.findComet("AuctionUpdater")).openOr(Nil).foreach(_ ! CurrentAuction(auction))
    auction.map { 
      single(_) &
      "#current_amount" #> <span>{leadingBid.toString}</span> & 
      "#next_amount" #> <span>{minimumBid.toString}</span>
    } openOr("*" #> "That auction does not exist.")
  }
}
