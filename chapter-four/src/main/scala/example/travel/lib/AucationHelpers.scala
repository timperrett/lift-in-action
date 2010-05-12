package example.travel {
package lib {
  
  import example.travel.model.Auction
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import net.liftweb.textile.TextileParser
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.mapper.{By}
  import net.liftweb.http.{S}
  import net.liftweb.http.js.JsCmd
  import net.liftweb.http.js.JsCmds.Noop
  
  
  /** 
   * common helpers for getting auctions and displaying them
   */
  trait AuctionHelpers extends Loggable {
    
    protected def many(auctions: List[Auction], xhtml: NodeSeq): NodeSeq = 
      auctions.flatMap(a => single(a,xhtml))
    
    def boxToNotice[T](sucsess: String, warning: String)(f: => Box[T]){
      f match {
        case Full(value) => 
          S.notice(sucsess)
        case Failure(msg,_,_) => 
          S.error(msg)
        case _ => 
          S.warning(warning)
      }
    }
    
    def currentAuction = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
    
    protected def single(auction: Auction, xhtml: NodeSeq): NodeSeq =
      bind("a", xhtml,
        "name" -> auction.name,
        "description" -> TextileParser.toHtml(auction.description),
        "travel_dates" -> auction.travelDates,
        "link" -%> <a href={"/auction/" +
          auction.id.toString+"-"+auction.permanent_link}>details >></a>
      )
    
  }
  
}}