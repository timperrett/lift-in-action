package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,SessionVar,RequestVar,S}
import net.liftweb.http.js.JsCmds.{SetHtml,Alert,Noop}
import net.liftweb.http.js.jquery.JqJsCmds.{Show,Hide}

case class Book(reference: String, var title: String)

class MoreAjax {
  object stock extends SessionVar[List[Book]](List(
    Book("ABCD", "Harry Potter and the Deathly Hallows"),
    Book("EFGH", "Harry Potter and the Goblet of Fire"),
    Book("IJKL", "Scala in Depth"),
    Book("MNOP", "Lift in Action")
  ))
  
  private val bookListDiv = "book_list"
  private val editFormDiv = "edit_display"
  
  def list = {
    ".line" #> stock.is.map { b => 
      ".name" #> b.title &
      ".line [id]" #> b.reference &
      ".edit" #>((ns: NodeSeq) => SHtml.a(() => 
        SetHtml(editFormDiv, SHtml.ajaxForm(edit(b)(ns), Hide(editFormDiv, 1 seconds))) & 
        Show(editFormDiv, 1 seconds), Text("Edit"))
      )
    }
  }
  
  def edit(b: Book) = {
    "#book_name" #> SHtml.text(b.title, b.title = _) &
    "type=submit" #> SHtml.ajaxSubmit("Update", 
      () => SetHtml(b.reference, Text(b.title))) 
  }
  
}