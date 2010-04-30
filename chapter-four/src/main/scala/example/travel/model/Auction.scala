package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.sitemap.Loc._
  import scala.xml.NodeSeq
  import net.liftweb.mapper._
  
  object Auction 
    extends Auction 
    with LongKeyedMetaMapper[Auction]
    with CRUDify[Long,Auction]{
      override def dbTableName = "auctions"
      override def fieldOrder = List(name,description,ends_at,
        outbound_on,inbound_on,flying_from,permanent_link,is_closed)
      
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
    object ends_at extends MappedDateTime(this)
    object outbound_on extends MappedDateTime(this)
    object inbound_on extends MappedDateTime(this)
    object flying_from extends MappedString(this, 100)
    object permanent_link extends MappedString(this, 150)
    object is_closed extends MappedBoolean(this)
    
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
    
    def highestBid: Box[Bid] = bids match {
      case list if list.length > 0 => Full(list.head)
      case _ => Empty
    }
  }
  
  
}}
