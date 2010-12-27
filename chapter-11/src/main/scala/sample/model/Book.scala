package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object Book extends Book with LongKeyedMetaMapper[Book]{
  override def dbTableName = "books"
}

class Book extends LongKeyedMapper[Book] with CreatedUpdated with IdPK {
  def getSingleton = Book
  
  object title extends MappedString(this, 255)
  object blurb extends MappedText(this)
  object publishedOn extends MappedDateTime(this)
}