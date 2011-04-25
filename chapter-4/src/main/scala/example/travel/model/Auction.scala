package example.travel
package model

import net.liftweb.common.{Full,Box,Empty,Failure}
import net.liftweb.sitemap.Loc._
import scala.xml.NodeSeq
import net.liftweb.mapper._
import net.liftweb.util.Helpers.{tryo,now}

object Auction 
  extends Auction 
  with LongKeyedMetaMapper[Auction]
  with CRUDify[Long,Auction]{
    override def dbTableName = "auctions"
    override def fieldOrder = List(name,description,endsAt,
      outboundOn,inboundOn,flyingFrom,permanent_link,isClosed)
    
    // crudify
    override def pageWrapper(body: NodeSeq) = 
      <lift:surround with="admin" at="content">{body}</lift:surround>
    override def calcPrefix = List("admin",_dbTableNameLC)
    override def displayName = "Auction"
    override def showAllMenuLocParams = LocGroup("admin") :: Nil
    override def createMenuLocParams = LocGroup("admin") :: Nil
    override def viewMenuLocParams = LocGroup("admin") :: Nil
    override def editMenuLocParams = LocGroup("admin") :: Nil
    override def deleteMenuLocParams = LocGroup("admin") :: Nil
  }

class Auction extends LongKeyedMapper[Auction] with IdPK with CreatedUpdated {
  def getSingleton = Auction
  // fields
  object name extends MappedString(this, 150){
    override def validations = 
      valMinLen(3, "Description must be 3 characters") _ :: 
      valUnique("That link URL has already been taken") _ :: 
      super.validations
  }
  object description extends MappedText(this)
  object endsAt extends MappedDateTime(this)
  object outboundOn extends MappedDateTime(this)
  object inboundOn extends MappedDateTime(this)
  object flyingFrom extends MappedString(this, 100)
  object permanent_link extends MappedString(this, 150)
  object isClosed extends MappedBoolean(this)
  
  // relationships
  object supplier extends LongMappedMapper(this, Supplier){
    override def dbColumnName = "supplier_id"
    override def validSelectValues = 
      Full(Supplier.findMap(OrderBy(Supplier.name, Ascending)){
        case s: Supplier => Full(s.id.is -> s.name.is)
      })
  }
  
  // helper: get all the bids for this auction
  def bids = Bid.findAll(By(Bid.auction, this.id), OrderBy(Bid.amount, Descending))
  
  def barter(next: Box[String]): Box[Bid] = 
    for(ass <- next ?~! "Amount is not a number";
        amo <- tryo(BigDecimal(ass).doubleValue) ?~! "Amount is not a number";
        nxt <- nextAmount;
        vld <- tryo(amo).filter(_ >= nxt) ?~ "Your bid is lower than required!"
    ) yield {
      new Bid().auction(this).customer(Customer.currentUser).amount(vld).saveMe
    }
  
  def expired_? : Boolean = endsAt.is.getTime < now.getTime
  
  def winningCustomer: Box[Customer] = topBid.flatMap(_.customer.obj)
  
  private def topBid: Box[Bid] = bids match {
    case list if list.length > 0 => Full(list.head)
    case _ => Empty
  }
  def currentAmount: Box[Double] = topBid.map(_.amount.is)
  def nextAmount: Box[Double] = currentAmount.map(_ + 1D)
  
  def travelDates: String = (Box.!!(inboundOn.is), Box.!!(outboundOn.is)) match {
    case (Full(in), Full(out)) => out.toString + ", returning " + in.toString
    case (Empty,Full(out)) => out.toString + " (one way)"
    case _ => "Travel dates not specified, call the vendor."
  }
}
