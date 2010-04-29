package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.mapper._
  
  object Order extends Order with LongKeyedMetaMapper[Order]{
    override def dbTableName = "orders"
  }

  class Order extends LongKeyedMapper[Order] 
      with IdPK with OneToMany[Long, Order] with CreatedUpdated {
    def getSingleton = Order
    // fields
    // object reference extends MappedString(this, 150)
    
    // relationships
    object customer extends LongMappedMapper(this, Customer){
      override def dbColumnName = "customer_id"
    }
    object order_auctions extends MappedOneToMany(OrderAuction, OrderAuction.order) 
        with Owned[OrderAuction] 
  }
  
  
}}
