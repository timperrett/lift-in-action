package example.travel {
package lib {
  
  import example.travel.model.Auction
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import net.liftweb.textile.TextileParser
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.mapper.{By}
  import net.liftweb.http.{S,SessionVar}
  import net.liftweb.http.js.JsCmd
  import net.liftweb.http.js.JsCmds.Noop
  
  trait AuctionActionHelpers {
    
    /** 
     * Accessor for the CurrentAuction request state; if it is empty, attempt to assign it.
     */
    protected def auction: Box[Auction]
    
    /**
     * Has the auction in the auction in the current request expired?
     */
    protected def hasExpired_? : Boolean = auction.map(_.expired_?).openOr(true)
    
    /**
     * Obtain the currently leading bid for the auction in the request scope
     */
    protected def leadingBid = auction.map(_.currentAmount).openOr(0D)
    
    /**
     * The minimum bid based on any incomming bids etc
     */
    protected def minimumBid = auction.map(_.nextAmount).openOr(0D)
    
    /**
     * Winning user name
     */
    protected def winningCustomer = 
      Text(auction.flatMap(_.winningCustomer.map(_.shortName)).openOr("Unknown"))
  }
  
  /** 
   * common helpers for getting auctions and displaying them
   */
  trait AuctionDisplayHelpers extends Loggable {
    
    protected def many(auctions: List[Auction], xhtml: NodeSeq): NodeSeq = 
      auctions.flatMap(a => single(a,xhtml))
    
    protected def boxToNotice[T](sucsess: String, warning: String)(f: => Box[T]){
      f match {
        case Full(value) => 
          S.notice(sucsess)
        case Failure(msg,_,_) => 
          S.error(msg)
        case _ => 
          S.warning(warning)
      }
    }
    
    protected def single(auction: Auction, xhtml: NodeSeq): NodeSeq =
      bind("a", xhtml,
        "name" -> auction.name,
        "description" -> TextileParser.toHtml(auction.description),
        "winning_customer" -> auction.winningCustomer.map(_.shortName).openOr("unknown"),
        "travel_dates" -> auction.travelDates,
        "link" -%> <a href={"/auction/" +
          auction.id.toString+"-"+auction.permanent_link}>details >></a>
      )
    
  }
  
}}