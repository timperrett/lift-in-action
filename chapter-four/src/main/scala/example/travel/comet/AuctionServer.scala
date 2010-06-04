package example {
package travel {
package comet {
  
  import scala.xml.{NodeSeq,Text}
  import scala.collection.immutable.Map
  import net.liftweb.common.{Full,Empty,Failure,Box,Loggable}
  import net.liftweb.actor.LiftActor 
  import net.liftweb.http.{CometActor,CometListenee,ListenerManager,S}
  import net.liftweb.util.Helpers._
  import example.travel.model.Auction
  import example.travel.lib.AuctionHelpers
  
  object AuctionServer extends LiftActor with Loggable {
    // load auction info from database on demand
    
    private var cometActorAuctions: Map[CometActor,List[Long]] = Map()
    private def auctionCometActors: Map[Long, List[CometActor]] = 
      cometActorAuctions.foldLeft[Map[Long, List[CometActor]]](Map.empty withDefaultValue Nil){
        case (prev, (k, vs)) => vs.foldLeft(prev)((prev, v) => prev + (v -> (k::prev(v))))
      }
    
    
    override def messageHandler = {
      case ListenTo(actor,auctions) => 
        println("Actor ("+actor.hashCode+") listening to: "+ auctions.map(_.toString).mkString(","))
        cometActorAuctions = cometActorAuctions + actor -> auctions
      case msg@NewBid(auction,amount,session) => 
        println("New bid recived for auction " + auction.toString + ", " + amount.toString)
        auctionCometActors(auction).foreach(_ ! msg)
    }
    
    // override def lowPriority {
    //   // case ChatServerMsg(user, msg) if msg.length > 0 =>
    //   //   chats ::= ChatLine(user, toHtml(msg), timeNow)
    //   //   chats = chats.take(50)
    //   //   updateListeners()
    //   case msg => println(msg)
    // }
    
    //def createUpdate = AuctionValueChange(10.0)
    
  }
  
}}}