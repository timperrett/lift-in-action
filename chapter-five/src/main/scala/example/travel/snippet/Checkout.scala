package example.travel {
package snippet {
  
  import net.liftweb.http.{LiftScreen,S}
  import example.travel.model.{Customer,Order}
  
  object Checkout extends LiftScreen {
    object order extends ScreenVar(Customer.currentUser.flatMap(_.order) openOr Order.create)
    
    _register(() => order.shippingAddressOne)
    _register(() => order.shippingAddressTwo)
    _register(() => order.shippingAddressCity)
    _register(() => order.shippingAddressPostalCode)
    _register(() => order.shippingAddressCounty)
    
    def finish(){
      if(order.save){
        S.redirectTo("summary")
      } else {
        S.error("Unable to save order details")
      }
    }
  }
  
}}