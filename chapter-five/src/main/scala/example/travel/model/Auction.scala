package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.sitemap.Loc._
  import scala.xml.NodeSeq
  import net.liftweb.mapper._
  import net.liftweb.util.Helpers.{tryo,now}
  import net.liftweb.machine.{ProtoStateMachine,MetaProtoStateMachine}
  
  class AuctionStates extends Enumeration {
    object Active extends Val(1,"Active")
    object Expired extends Val(2,"Expired")
  }
  
  case object AuctionExpires extends ATransition(,on,)
  
  object Auction 
    extends Auction 
    with LongKeyedMetaMapper[Auction]
    with CRUDify[Long,Auction]
    with MetaProtoStateMachine[Auction, AuctionStates] {
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
      
      // state machine methods
      def instantiate = new Auction
      val stateEnumeration = new AuctionStates
      def initialState = stateEnumeration.Active
      def globalTransitions = Nil
      def states = List(,new State(stateEnumeration.Active, List(AuctionExpires)))
    }

  class Auction extends LongKeyedMapper[Auction]
      with CreatedUpdated 
      with ProtoStateMachine[Auction, AuctionStates]
      with IdPK {
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
    def bids = Bid.findAll(By(Bid.auction, this.id), OrderBy(Bid.amount, Descending))
    
    def barter(next: Box[String]): Box[Bid] = 
      for(ass <- next ?~! "Amount is not a number";
          amo <- tryo(BigDecimal(ass).doubleValue) ?~! "Amount is not a number";
          nxt <- nextAmount;
          vld <- tryo(amo).filter(_ >= nxt) ?~ "Your bid is lower than required!"
      ) yield {
        new Bid().auction(this).customer(Customer.currentUser).amount(vld).saveMe
      }
    
    def expired_? : Boolean = ends_at.is.getTime < now.getTime
    
    def winningCustomer: Box[Customer] = topBid.flatMap(_.customer.obj)
    
    private def topBid: Box[Bid] = bids match {
      case list if list.length > 0 => Full(list.head)
      case _ => Empty
    }
    def currentAmount: Box[Double] = topBid.map(_.amount.is)
    def nextAmount: Box[Double] = currentAmount.map(_ + 1D)
    
    def travelDates: String = (Box.!!(inbound_on.is), Box.!!(outbound_on.is)) match {
      case (Full(in), Full(out)) => out.toString + ", returning " + in.toString
      case (Empty,Full(out)) => out.toString + " (one way)"
      case _ => "Travel dates not specified, call the vendor."
    }
  }
  
}}
