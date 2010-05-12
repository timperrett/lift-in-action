package example {
package travel {
package comet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Empty,Failure,Box}
  import net.liftweb.actor.LiftActor 
  import net.liftweb.http.{CometActor,CometListenee,ListenerManager,S}
  import net.liftweb.util.Helpers._
  import example.travel.model.Auction
  import example.travel.lib.AuctionHelpers
  
  object AuctionServer extends LiftActor with ListenerManager {
    // load auction info from database on demand
    
    override def lowPriority = {
      // case ChatServerMsg(user, msg) if msg.length > 0 =>
      //   chats ::= ChatLine(user, toHtml(msg), timeNow)
      //   chats = chats.take(50)
      //   updateListeners()
      case msg => println(msg)
    }
    
    def createUpdate = AuctionValueChange(10.0)
    
  }
  
}}}