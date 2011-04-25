package example.travel
package snippet

import net.liftweb.http.{LiftScreen,S}
import example.travel.model.{Customer,Order}

object Checkout extends LiftScreen {
  object order extends ScreenVar(Customer.currentUser.flatMap(_.order) openOr Order.create)
  
  addFields(() => order.shippingAddressOne)
  addFields(() => order.shippingAddressTwo)
  addFields(() => order.shippingAddressCity)
  addFields(() => order.shippingAddressPostalCode)
  addFields(() => order.shippingAddressCounty)
  
  def finish(){
    if(order.save){
      S.redirectTo("summary")
    } else {
      S.error("Unable to save order details")
    }
  }
}
