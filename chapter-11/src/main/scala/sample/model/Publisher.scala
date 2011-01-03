package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object Publisher extends Publisher with LongKeyedMetaMapper[Publisher]{
  override def dbTableName = "publishers"
}

class Publisher extends LongKeyedMapper[Publisher] 
    with CreatedUpdated with IdPK with OneToMany[Long,Publisher] {
  def getSingleton = Publisher
  
  object name extends MappedString(this, 100)
  object description extends MappedText(this)
  
  // relationships
  object books extends MappedOneToMany(Book, Book.publisher, 
    OrderBy(Book.title, Ascending)) with Owned[Book] with Cascade[Book]
  
}
