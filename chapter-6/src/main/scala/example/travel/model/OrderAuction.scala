package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.mapper._
  
  object OrderAuction extends OrderAuction with LongKeyedMetaMapper[OrderAuction]{
    override def dbTableName = "order_auctions"
    override def fieldOrder = id :: order :: auction :: createdAt :: updatedAt :: Nil
  }

  class OrderAuction extends LongKeyedMapper[OrderAuction] with IdPK with CreatedUpdated {
    def getSingleton = OrderAuction
    object order extends LongMappedMapper(this, Order){
      override def dbColumnName = "order_id"
    }
    object auction extends LongMappedMapper(this, Auction){
      override def dbColumnName = "auction_id"
    }
  }
  
  
}}
