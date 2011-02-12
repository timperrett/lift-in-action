package sample.snippet

import scala.xml.{NodeSeq,Text}
import javax.persistence.{EntityExistsException,PersistenceException}
import net.liftweb.common.LazyLoggable
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,SHtml}
import sample.model.{Book,Author,Model}
import Model._

class Authors extends LazyLoggable {
  object authorVar extends RequestVar(new Author)
  def author = authorVar.is
  
  def list =
    "tr" #> Model.createNamedQuery[Author]("findAllAuthors").getResultList.map { a => 
      ".name" #> a.name
    }
  
  def add = {
    val currentId = author.id
    "#aid" #> SHtml.hidden(() => author.id = currentId) &
    "#name" #> SHtml.text(author.name, author.name = _) &
    "type=submit" #> SHtml.onSubmitUnit(() => {}
      // tryo(Model.mergeAndFlush(author)).pass(S.redirectTo("index")) match {
        // case Failure(msg,)
      // }
    )

  }
    
    
    
  
  
  //   
  //   authors.flatMap(author =>
  //     bind("author", xhtml,
  //    "name" -> Text(author.name),
  //    "count" -> SHtml.link("/books/search.html", {() =>
  //      BookOps.resultVar(Model.createNamedQuery[Book]("findBooksByAuthor", "id" ->author.id).getResultList().toList)
  //      }, Text(author.books.size().toString)),
  //    "edit" -> SHtml.link("add.html", () => authorVar(author), Text(?("Edit")))))
  // }
}
