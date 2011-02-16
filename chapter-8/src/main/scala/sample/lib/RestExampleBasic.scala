package sample.lib

import net.liftweb.http.rest.RestHelper

object BookshopHttpServiceBasic extends RestHelper {
  serve {
    case "bookshop" :: "books" :: Nil XmlGet _ => 
      response(Bookshop.stock)
    case "bookshop" :: "books" :: publisher :: Nil XmlGet _ => 
      response(Bookshop.stock.filter(_.publisher equalsIgnoreCase publisher))
  }
  
  private def response(in: List[Book]) = 
    <books>{in.flatMap(b => 
      <book publisher={b.publisher} title={b.title}/>)
    }</books>
}