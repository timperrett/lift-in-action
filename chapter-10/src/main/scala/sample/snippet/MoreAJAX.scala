package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,SessionVar,RequestVar,S}
import net.liftweb.http.js.JsCmds.{SetHtml,Noop}
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
  private val editFormDiv = "edit_form"
  
  def list(xhtml: NodeSeq): NodeSeq = 
    stock.is.flatMap(b => bind("b", chooseTemplate("book","list",xhtml),
      AttrBindParam("id", "book_"+b.reference, "id"),
      "name" -> b.title,
      "edit" -> SHtml.a(
        () => SetHtml(editFormDiv, edit(xhtml, b)) & Show(editFormDiv, 1 seconds), Text("Edit"))
    ))
  
  def edit(xhtml: NodeSeq, b: Book): NodeSeq =
    SHtml.ajaxForm(bind("f", chooseTemplate("edit","form",xhtml),
      "name" -> SHtml.text(b.title, b.title = _) % ("size" -> 35),
      "submit" -> SHtml.ajaxSubmit("Update", 
        () => SetHtml("book_"+b.reference, Text(b.title)))
    ), Hide(editFormDiv, 1 seconds))
  
}