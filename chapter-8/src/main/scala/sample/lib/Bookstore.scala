package sample.lib

import net.liftweb.http.{XmlResponse,PlainTextResponse}

case class Book(publisher: String, title: String)

object Bookshop {
  val stock = List(
    Book("Bloomsbury", "Harry Potter and the Deathly Hallows"),
    Book("Bloomsbury", "Harry Potter and the Goblet of Fire"),
    Book("Manning", "Scala in Depth"),
    Book("Manning", "Lift in Action")
  )
}

/*
 WHAT FOLLOWS IS THE COMPANION FOR ADVANCED ONLY
*/
object Book {
  implicit val booksAsXml: Return[List[Book]]#As[XmlResponse] = 
    (books:List[Book]) => XmlResponse(
      <books>{books.flatMap(b => 
        <book publisher={b.publisher} title={b.title}/>)
      }</books>)
  
  implicit val booksAsPlainText: ReturnAs[List[Book], PlainTextResponse] = 
    (books:List[Book]) => PlainTextResponse("Books\n"+
      books.map(b => "publisher:"+b.publisher + ", title:"+b.title))
}