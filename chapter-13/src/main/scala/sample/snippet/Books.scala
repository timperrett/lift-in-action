package sample.snippet

import scala.xml.{NodeSeq,Text}
import scala.collection.JavaConversions._
import javax.validation.ConstraintViolationException
import net.liftweb.common.{Failure,Empty,Full,Box}
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,SHtml,S}
import sample.model.{Book,Author,Model}

class Books {
  object bookVar extends RequestVar(new Book)
  def book = bookVar.is
  lazy val formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
  
  def add = {
    val current = book
    val authors = Model.createNamedQuery[Author]("findAllAuthors").getResultList
    val choices = authors.map(author => (author.id.toString -> author.name)).toList
    val default = (Box !! book.author).map(_.id.toString) or Empty
    
    "type=hidden" #> SHtml.hidden(() => bookVar(current)) &
    "name=title" #> SHtml.text(book.title, book.title = _) &
    "name=published" #> SHtml.text(formatter.format(book.published), v => book.published = formatter.parse(v)) &
    "#author" #> SHtml.select(choices, default, {authId : String => book.author = Model.getReference(classOf[Author], authId.toLong)}) &
    "type=submit" #> SHtml.onSubmitUnit(() =>
      tryo(Model.mergeAndFlush(book)) match {
        case Failure(msg,Full(err: ConstraintViolationException),_) => 
          S.error(err.getConstraintViolations.toList.flatMap(c => <p>{c.getMessage}</p>)) 
        case _ => S.redirectTo("/jee/authors/")
      })
  }    
}