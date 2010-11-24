package sample.lib

import net.liftweb.http.{LiftResponse,XmlResponse,PlainTextResponse}
import net.liftweb.http.rest.RestHelper

/*
STEP 2: Define some service helpers and implementation
        magic to handle the service definition
*/
trait ReturnAs[A, B] {
  def as(a:A):B
}
object ReturnAs{
  implicit def f2ReturnAs[A, B](f: A => B): ReturnAs[A, B] = 
    new ReturnAs[A, B]{
      def as(a:A) = f(a)
    }
}
trait Return[A] {
  type As[B] = ReturnAs[A, B]
}
object Return {
  def apply[A, B](a: A)(implicit f: ReturnAs[A, B]) = f.as(a)
}


/*
STEP 4: Define the services themselves
*/
trait BookshopService {
  def list[R : Return[List[Book]]#As]:R =
    Return(Bookshop.stock)

  def listByPublisher[R : Return[List[Book]]#As](publisher:String):R =
    Return(Bookshop.stock.filter(_.publisher equalsIgnoreCase publisher))
}

/*
STEP 5: Impement the services. Dont forget to write this up in Boot.scala.
*/
object BookshopHttpServiceAdvanced extends BookshopService with RestHelper {
  serve {
    // xml services
    case "bookshop_adv" :: "books" :: Nil XmlGet _ => list[XmlResponse]
    case "bookshop_adv" :: "books" :: publisher :: Nil XmlGet _ => listByPublisher[XmlResponse](publisher)
    // plain text services
    case "bookshop_adv" :: "books" :: Nil Get _ => list[PlainTextResponse]
    case "bookshop_adv" :: "books" :: publisher :: Nil Get _ => listByPublisher[PlainTextResponse](publisher)
  }
}
