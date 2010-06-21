package example.travel {
package lib {
  
  import net.liftweb.common.{Box,Empty}
  import net.liftweb.http.SessionVar
  import example.travel.model.Order
  
  object CurrentOrder extends SessionVar[Box[Order]](Empty)
  
  
}}