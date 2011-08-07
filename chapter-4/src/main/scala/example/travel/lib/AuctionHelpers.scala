package example.travel
package lib

import example.travel.model.Auction
import scala.xml.{NodeSeq,Text}

import net.liftweb._,
  common.{Full,Box,Empty,Failure,Loggable},
  util.Helpers._,
  util.CssSel,
  http.{S,SessionVar},
  http.js.JsCmd,
  http.js.JsCmds.Noop,
  mapper.{By},
  textile.TextileParser

trait AuctionInstanceHelpers extends AuctionHelpers {
  
  /** 
   * Accessor for the CurrentAuction request state; if it is empty, attempt to assign it.
   */
  protected def auction: Box[Auction]
  
  /**
   * Has the auction in the auction in the current request expired?
   */
  protected def hasExpired_? : Boolean = hasExpired_?(auction)
  
  /**
   * Obtain the currently leading bid for the auction in the request scope
   */
  protected def leadingBid: Double = leadingBid(auction)
  
  /**
   * The minimum bid based on any incomming bids etc
   */
  protected def minimumBid: Double = minimumBid(auction)
  
  /**
   * Winning user name
   */
  protected def winningCustomer: NodeSeq = winningCustomer(auction)
}

/** 
 * common helpers for getting auctions and displaying them
 */
trait AuctionHelpers extends Loggable {
  
  protected def many(auctions: List[Auction]) = auctions.map(a => single(a))
  
  /** 
   * In reality i'd build these types of functions as a type class,
   * but that's really too much complexity to be introducted at this level
   */
  protected def single(auction: Auction): CssSel = 
    ".name *" #> auction.name &
    ".desc" #> TextileParser.toHtml(auction.description) & 
    "#winning_customer *" #> winningCustomer(auction) &
    "#travel_dates" #> auction.travelDates & 
    "a [href]" #> "/auction/%s".format(auction.id.toString) 
  
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
  
  /**
   * Has the auction in the auction in the current request expired?
   */
  protected def hasExpired_?(a: Box[Auction]) : Boolean = a.map(_.expired_?).openOr(true)

  /**
   * Obtain the currently leading bid for the auction in the request scope
   */
  protected def leadingBid(a: Box[Auction]): Double = a.flatMap(_.currentAmount).openOr(0D)

  /**
   * The minimum bid based on any incomming bids etc
   */
  protected def minimumBid(a: Box[Auction]): Double = a.flatMap(_.nextAmount).openOr(0D)

  /**
   * Winning user name
   */
  protected def winningCustomer(a: Box[Auction]): NodeSeq = 
    Text(a.flatMap(_.winningCustomer.map(_.shortName)).openOr("Unknown"))
  
  protected def winningCustomer(a: Auction): NodeSeq = 
    winningCustomer(Full(a))
  
}

