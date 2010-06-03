package example {
package travel {
package comet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Empty,Failure,Box}
  import net.liftweb.actor.LiftActor 
  import net.liftweb.http.{CometActor,CometListenee,ListenerManager,S}
  import net.liftweb.util.Helpers._
  import net.liftweb.util.ActorPing
  import net.liftweb.http.js.JsCmds._
  import example.travel.model.{Auction,Customer}
  import example.travel.lib.AuctionRequestHelpers
  
  // messages
  case object CountdownTick
  case class ListenTo(actor: CometActor, auctions: List[Long])
  case class CurrentAuction(auction: Box[Auction])
  
  // case class AuctionInfoFor(id: Long)
  case class NewBid(auction: Long, amount: Double)
  
  class AuctionUpdater extends CometActor with AuctionRequestHelpers {
    // element ids
    private lazy val countdownId = uniqueId+"_countdown"
    private lazy val nextAmountId = "next_amount"
    private lazy val currentAmountId = "current_amount"
    // internal helps
    private val server = AuctionServer
    
    // state
    override def auction = _auction
    private def auctionId = auction.map(_.id.is).openOr(0L)
    private var _auction: Box[Auction] = Empty
    
    /**
     * xhtml content
     */
    def countdown = 
      if(hasExpired_?) Text("This auction has ended.") else Text(timeNow.toString)
    
    def notifyOtherAuctionUpdate {
      partialUpdate(Run("$.growl('TEST', 'EXAMPLE')"))
    }
    
    def notifyThisAuctionUpdate {
      partialUpdate(SetHtml(currentAmountId, Text(leadingBid.toString)))
      partialUpdate(SetHtml(nextAmountId, Text("NEXT")))
    }
    
    /**
     * comet message handlers
     */
    override def lowPriority = {
      case CountdownTick => {
        partialUpdate(SetHtml(countdownId, countdown))
        // if its not expired, continue the countdown
        if(!hasExpired_?) ActorPing.schedule(this, CountdownTick, 1 seconds)
      }
      case CurrentAuction(a) => _auction = a
    }
    
    override def highPriority = {
      case NewBid(auctionId,amount) => 
        if(auctionId == auctionId)
          notifyThisAuctionUpdate
        else 
          notifyOtherAuctionUpdate
    }
    
    override def render = {
      // listen for bids on this current auction (and auctions the user is bidding on)
      auction.map(a => {
        server ! ListenTo(this,(a.id.is :: Customer.currentUser.map(_.participatingIn).openOr(Nil)).removeDuplicates)
      })
      
      // need at least one ping after the intial render
      ActorPing.schedule(this, CountdownTick, 2 seconds)
      // do the render
      bind("live",defaultXml,
        "countdown" -> <span id={countdownId}>Calculating...</span>
      )
    }
    
    // private def activeUserAuctions {
    //   if(Customer.loggedIn_?){
    //     _activeUserAuctions = Customer.currentUser.participatingIn
    //   }
    // } 
  }
  
}}}