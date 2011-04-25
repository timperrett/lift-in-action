package example.travel.model 

import net.liftweb.common.{Full,Box,Empty,Failure}
import net.liftweb.sitemap.Loc._
import scala.xml.NodeSeq
import net.liftweb.mapper._

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
  object name extends MappedString(this, 150)
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
  def bids = Bid.findAll(By(Bid.auction, this.id), OrderBy(Bid.id, Descending))
}
