package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.mapper._
  import net.liftweb.util.Helpers.randomLong
  
  object OrderStatus extends Enumeration {
    val Open = Value(1,"open")
    val Pending = Value(2,"pending")
    val Complete = Value(3,"complete")
    val Failed = Value(4,"failed")
  }
  
  object Order extends Order with LongKeyedMetaMapper[Order]{
    override def dbTableName = "orders"
    override def beforeCreate = List(_.customer(Customer.currentUser).status(OrderStatus.Open).reference(randomLong(99999999L)))
  }
  
  class Order extends LongKeyedMapper[Order] 
      with IdPK with OneToMany[Long, Order] with CreatedUpdated {
    def getSingleton = Order
    // fields
    object reference extends MappedLong(this)
    object status extends MappedEnum(this, OrderStatus)
    object shippingAddressOne extends MappedString(this,255){
      override def displayName = "Address One"
    }
    object shippingAddressTwo extends MappedString(this,255){
      override def displayName = "Address Two"
    }
    object shippingAddressCity extends MappedString(this,255){
      override def displayName = "City"
    }
    object shippingAddressPostalCode extends MappedPostalCode(this,shippingAddressCounty){
      override def displayName = "Postcode"
    }
    object shippingAddressCounty extends MappedCountry(this){
      override def displayName = "Country"
    }
    
    // relationships
    object customer extends LongMappedMapper(this, Customer){
      override def dbColumnName = "customer_id"
    }
    object order_auctions extends MappedOneToMany(OrderAuction, OrderAuction.order) 
        with Owned[OrderAuction]
    
    // helpers 
    def totalValue: Double = (for(
      oa <- order_auctions.all;
      au <- oa.auction.obj;
      av <- au.currentAmount) yield av).reduceLeft(_ + _)    
    
  }
  
  
}}
