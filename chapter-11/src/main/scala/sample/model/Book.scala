package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._
import net.liftweb.common.Full

object Book extends Book with LongKeyedMetaMapper[Book]{
  override def dbTableName = "books"
  override def beforeSave = List(book => println("About to save '%s'".format(book.title.is)))
  override def afterSave = List(book => println("I've saved!"))
}

class Book extends LongKeyedMapper[Book] 
    with CreatedUpdated 
    with IdPK
    with ManyToMany {
  def getSingleton = Book
  
  object title extends MappedString(this, 255){
    override def validations = 
      Validations.onlyInActionBooks(this) _ :: Nil
  }
  object blurb extends MappedText(this)
  object publishedOn extends MappedDate(this)
  
  // relationships
  object publisher extends LongMappedMapper(this, Publisher){
    override def dbColumnName = "publisher_id"
    override def validSelectValues = 
      Full(Publisher.findMap(OrderBy(Publisher.name, Ascending)){
        case p: Publisher => Full(p.id.is -> p.name.is)
      })
  } 
  object authors extends MappedManyToMany(BookAuthors, BookAuthors.book, BookAuthors.author, Author)
}

import net.liftweb.util.{FieldIdentifier,FieldError}

object Validations {
  def onlyInActionBooks(field : FieldIdentifier)(string : String) = 
    if(!string.toLowerCase.endsWith("in action"))
      List(FieldError(field, "What?! Its not an in action book?"))
    else List[FieldError]()
}