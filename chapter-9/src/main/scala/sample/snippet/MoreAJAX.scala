package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,SessionVar}
import net.liftweb.http.js.JsCmds.{SetHtml,Alert}
import net.liftweb.http.js.jquery.JqJsCmds.{Show,Hide}

case class Book(reference: String, var title: String)

class MoreAjax {
  object stock extends SessionVar[List[Book]](List(
    Book("ABCD", "Harry Potter and the Deathly Hallows"),
    Book("EFGH", "Harry Potter and the Goblet of Fire"),
    Book("IJKL", "Scala in Depth"),
    Book("MNOP", "Lift in Action")
  ))
  
  private val editFormDiv = "edit_display"
  
  def list = 
    ".line" #> stock.is.map { b => 
      ".name *" #> b.title &
      ".name [id]" #> b.reference &
      ".edit" #> edit(b)
    }
  
  def edit(b: Book): NodeSeq => NodeSeq = { ns =>
    val form = 
      "#book_name" #> SHtml.text(b.title, b.title = _) &
      "type=submit" #> SHtml.ajaxSubmit("Update", 
        () => SetHtml(b.reference, Text(b.title))
      ) andThen SHtml.makeFormsAjax 
    
    SHtml.a(() => 
      SetHtml(editFormDiv, form(ns)) & Show(editFormDiv, 1 seconds), 
      Text("Edit"))
  }
}