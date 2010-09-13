package example {
package travel {
package comet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Empty,Failure,Box}
  import net.liftweb.http.CometActor
  import net.liftweb.util.Helpers._
  import net.liftweb.util.ActorPing
  import net.liftweb.http.S
  import net.liftweb.http.js.JsCmds._
  import example.travel.model.{Auction,Customer}
  import example.travel.lib.AuctionInstanceHelpers
  
  // messages
  case object CountdownTick
  case class ListenTo(actor: CometActor, auctions: List[Long])
  case class CurrentAuction(auction: Box[Auction])
  
  // case class AuctionInfoFor(id: Long)
  case class NewBid(auction: Long, amount: Double, fromsession: Box[String])
  
  class AuctionUpdater extends CometActor with AuctionInstanceHelpers {
    // element ids
    private lazy val countdownId = "time_remaining"
    private lazy val nextAmountId = "next_amount"
    private lazy val currentAmountId = "current_amount"
    private lazy val winningCustomerId = "winning_customer"
    private lazy val amountId = "amount"
    // helpers
    private val server = AuctionServer
    private var _auction: Box[Auction] = Empty
    protected def auction = _auction
    private def auctionId = auction.map(_.id.is).openOr(0L)
    
    /**
     * xhtml content
     */
    def countdown = 
      if(hasExpired_?) Text("This auction has ended.") 
      else Text(TimeSpan.format((auction.map(_.ends_at.is.getTime).openOr(now.getTime) - now.getTime) / 1000L * 1000L))
    
    def notifyOtherAuctionUpdate {
      warning("You have been outbid on an auction you are participating in")
    }
    
    def notifyThisAuctionUpdate {
      partialUpdate {
        SetHtml(currentAmountId, Text(leadingBid.toString)) & 
        SetHtml(nextAmountId, Text(minimumBid.toString)) & 
        SetHtml(winningCustomerId, winningCustomer) &
        SetValueAndFocus(amountId,"")
      }
    }
    
    def registerListeners {
      auction.map(a => 
        server ! ListenTo(this,(a.id.is :: Customer.currentUser.map(_.participatingIn).openOr(Nil)).distinct))
    }
    
    /**
     * comet message handlers
     */
    override def lowPriority = {
      case CountdownTick => {
        partialUpdate(SetHtml(countdownId, countdown))
        if(!hasExpired_?) ActorPing.schedule(this, CountdownTick, 5 seconds)
      }
      case CurrentAuction(a) => 
        _auction = a
        registerListeners
    }
    
    override def highPriority = {
      case NewBid(auctionId,amount,fromsession) => 
        notifyThisAuctionUpdate
        if((S.session.map(_.uniqueId) equals fromsession) == false)
          notifyOtherAuctionUpdate
    }
    
    override def render = {
      // listen for bids on this current auction (and auctions the user is bidding on)
      registerListeners
      // need at least one ping after the intial render
      ActorPing.schedule(this, CountdownTick, 2 seconds)
      NodeSeq.Empty
    }
    
  }
  
}}}