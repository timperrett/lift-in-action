package example.travel {
package lib {
  
  import example.travel.model.Auction
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import net.liftweb.textile.TextileParser
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.mapper.{By}
  import net.liftweb.http.{S,RequestVar}
  import net.liftweb.http.js.JsCmd
  import net.liftweb.http.js.JsCmds.Noop
  
  
  object CurrentAuction extends RequestVar[Box[Auction]](
    Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
  )
  
  trait AuctionRequestHelpers {
    
    /** 
     * Accessor for the CurrentAuction request state; if it is empty, attempt to assign it.
     */
    def auction: Box[Auction] = CurrentAuction.is
    
    /**
     * Has the auction in the auction in the current request expired?
     */
    def hasExpired_? : Boolean = auction.map(_.expired_?).openOr(true)
    
    /**
     * Obtain the currently leading bid for the auction in the request scope
     */
    def leadingBid = auction.map(_.currentAmount).openOr(0D)
  }
  
  /** 
   * common helpers for getting auctions and displaying them
   */
  trait AuctionHelpers extends Loggable {
    
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