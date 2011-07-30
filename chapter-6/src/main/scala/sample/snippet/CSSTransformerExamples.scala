package sample.snippet 

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml

class BasicExample {
  def sample = {
    var message = "Change this text"
    "type=text" #> SHtml.text(message, message = _) &
    "type=submit" #> SHtml.onSubmitUnit(() => println(message))
  }
}

class SelectorDemo {
  def render = {
    // leave this first one out as it will replace everything!
    // "*" #> <p>Replace all</p> &
    "#thing" #> <p>Replaced</p> &
    ".amazing" #> <p>WOW</p> &
    "type=text [class]" #> "textinput" &
    "@signup" #> NodeSeq.Empty & 
    ":button" #> SHtml.button("Hit me", () => println("w00t")) &
    ".foo [class+]" #> "bar" &
    "li *" #> List("monday", "tuesday", "wednesday") &
    "#append_target *+" #> "Timothy" &
    "#prepend_target -*" #> "Timothy" 
    // template selectors
    // "#hats ^^" #> NodeSeq.Empty
    // "#hats ^*" #> NodeSeq.Empty
  }
}

object Library {
  case class Book(name: String)
  case class Author(name: String, books: List[Book])
  val books = List(
    Author("JK Rowling", List(
      Book("Harry Potter and the Deathly Hallows"),
      Book("Harry Potter and the Goblet of Fire"))
    ),
    Author("Joshua Suereth", List(Book("Scala in Depth"))))
}

class IntermediateExample {
  def list = 
    "ul" #> Library.books.map { author =>
      ".name" #> author.name &
      ".books" #> ("li *" #> author.books.map(_.name))
    }
}
