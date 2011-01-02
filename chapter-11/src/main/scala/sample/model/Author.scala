package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object Author extends Author with LongKeyedMetaMapper[Author]{
  override def dbTableName = "authors"
}

class Author extends LongKeyedMapper[Author] 
    with CreatedUpdated with IdPK with ManyToMany {
  def getSingleton = Author
  
  object title extends MappedEnum(this, Titles)
  object firstName extends MappedString(this, 255)
  object lastName extends MappedText(this)
  object email extends MappedEmail(this, 150)
  
  object books extends MappedManyToMany(BookAuthors, BookAuthors.author, BookAuthors.book, Book)
  
}

object Titles extends Enumeration {
  val Mr, Mrs, Miss, Dr = Value
}
