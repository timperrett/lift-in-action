package example {
package travel {
package comet {
  
  import scala.collection.immutable.Map
  import net.liftweb.actor.LiftActor 
  import net.liftweb.http.CometActor
  
  object AuctionServer extends LiftActor {
    /** 
     * Internal state about who's listening for what and what is listening for who!
     */
    private var cometActorAuctions: Map[CometActor,List[Long]] = Map()
    private def auctionCometActors: Map[Long, List[CometActor]] = 
      cometActorAuctions.foldLeft[Map[Long, List[CometActor]]](Map.empty withDefaultValue Nil){
        case (prev, (k, vs)) => vs.foldLeft(prev)((prev, v) => prev + (v -> (k::prev(v))))
      }
    
    /**
     * Handle the messages themselves
     */ 
    override def messageHandler = {
      case ListenTo(actor,auctions) => 
        println("Actor ("+actor.hashCode+") listening to: "+ auctions.map(_.toString).mkString(","))
        cometActorAuctions = cometActorAuctions + (actor -> auctions) 
      case msg@NewBid(auction,amount,session) => 
        println("New bid recived for auction " + auction.toString + ", " + amount.toString)
        auctionCometActors(auction).foreach(_ ! msg)
    }
    
  }
  
}}}