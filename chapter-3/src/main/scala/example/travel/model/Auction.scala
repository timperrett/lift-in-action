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
      outboundOn,inboundOn,flyingFrom,startingAmount,isClosed)
    
    override def dbAddTable = Full(populate _)
    private def populate {
      val airports = List("Bristol", "London Heathrow", "Paris", "New York")
      for(l <- 'A' to 'Z')
        Auction.create
          .name("Trip %s".format(l))
          .description("""utpat vel aliquam eget, auctor ac nisl. Curabitur laoreet urna consectetur utpat vel aliquam eget, auctor ac nisl. Curabitur laoreet urna consectetur lectus faucibus ultricies. Maecenas nec lectus et dui sodales ultricies. Fusce eu pulvinar ipsum. In varius euismod lectus. Suspendisse potenti. Integer velit nisl, iaculis in aliquet non""")
          .flyingFrom(airports.apply(scala.util.Random.nextInt(3)))
          .isClosed(false)
          .startingAmount(1.0D)
          .save
    }
    
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
  object isClosed extends MappedBoolean(this){
    override def defaultValue = false
  }
  object startingAmount extends MappedDouble(this)
  
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
