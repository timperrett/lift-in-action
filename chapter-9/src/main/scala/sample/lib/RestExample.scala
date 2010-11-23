package sample.lib

import scala.xml.NodeSeq
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.http.{LiftResponse,XmlResponse,PlainTextResponse}

case class Book(publisher: String, name: String)

trait Services { 
  type R = () => Box[LiftResponse]
  
  trait Service[T]{
    def deliver(value: T): R
  }
  
  protected def flush[T : Service](t: T) = implicitly[Service[T]].deliver(t)
  
  def list: R
  def listByPublisher(what: String): R
  
  object actions {
    def list = BookshopDatabase.books
    def listByPublisher(what: String) = BookshopDatabase.books.filter(
      _.publisher.toLowerCase == what.toLowerCase)
  }
}

trait XmlServices extends Services {
  implicit object ListBooks extends Service[List[Book]] {
    def deliver(books: List[Book]) = () => Full(
      XmlResponse(<books>{
        books.map(b => <book publisher={b.publisher} name={b.name} />)
      }</books>))
  }
  
  // actual service implementations
  def list = flush(actions.list)
  def listByPublisher(what: String) = flush(actions.listByPublisher(what))
}

/*trait PlainTextService {
  implicit object ListBooks extends Service[List[Book]] {
    def deliver(books: List[Book]): PlainTextResponse = 
      PlainTextResponse("ok")
  }
}
*/
object BookshopDatabase {
  val books = List(
    Book("Bloomsbury", "Harry Potter and the Deathly Hallows"),
    Book("Bloomsbury", "Harry Potter and the Goblet of Fire"),
    Book("Manning", "Scala in Depth"),
    Book("Manning", "Lift in Action")
  )
}

object BookshopService {
  object xml extends XmlServices
}
