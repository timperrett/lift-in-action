package sample.snippet

import scala.xml.{NodeSeq,Text}
import scala.collection.JavaConversions._
import javax.validation.ConstraintViolationException
import net.liftweb.common.{Failure,Empty,Full}
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,SHtml,S}
import sample.model.{Book,Author,Model}

object Authors {
  object authorVar extends RequestVar(new Author)
}
class Authors {
  import Authors._
  def author = authorVar.is
  
  def list =
    "tr" #> Model.createNamedQuery[Author]("findAllAuthors").getResultList.map { a => 
      ".name" #> a.name &
      ".books" #> SHtml.link("/jee/books/add",
          () => authorVar(a), 
          Text("%s books (Add more)".format(a.books.size))) &
      ".edit" #> SHtml.link("add", () => authorVar(a), Text("Edit"))
    }
  
  def add = {
    val currentId = author.id
    "type=hidden" #> SHtml.hidden(() => author.id = currentId) &
    "type=text" #> SHtml.text(author.name, author.name = _) &
    "type=submit" #> SHtml.onSubmitUnit(() =>
      tryo(Model.mergeAndFlush(author)) match {
        case Failure(msg,Full(err: ConstraintViolationException),_) => 
          S.error(err.getConstraintViolations.toList.flatMap(c => <p>{c.getMessage}</p>)) 
        case _ => S.redirectTo("index")
      })
  }
}
