package example {
package travel {
package comet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Empty,Failure,Box}
  import net.liftweb.actor.LiftActor 
  import net.liftweb.http.{CometActor,CometListenee,ListenerManager,S}
  import net.liftweb.util.Helpers._
  import net.liftweb.util.ActorPing
  import net.liftweb.http.js.JsCmds.{SetHtml}
  import example.travel.model.Auction
  import example.travel.lib.AuctionHelpers
  
  // messages
  case object CountdownTick
  case class AuctionInfoFor(id: Long)
  case class AuctionValueChange(amount: Double)
  
  class AuctionUpdater extends CometActor with CometListenee with AuctionHelpers {
    private lazy val countdownId = uniqueId+"_countdown"
    private lazy val nextAmountId = "next_amount"
    private lazy val currentAmountId = "current_amount"
    private var auction: Box[Auction] = Empty
    
    override def lowPriority = {
      case CountdownTick =>
        partialUpdate(SetHtml(countdownId, Text(timeNow.toString)))
        ActorPing.schedule(this, CountdownTick, 1 seconds)
    }
    
    override def highPriority = {
      case AuctionInfoFor(id) => // get from database
      case AuctionValueChange(amount) => // update the front end with JsCmds
    }
    
    override def render = {
      auction = currentAuction
      bind("live",defaultXml,
        "countdown" -> <span id={countdownId}>Calculating...</span>
      )
    }
    
    def registerWith = AuctionServer
    
    ActorPing.schedule(this, CountdownTick, 2 seconds)
  }
  
}}}