package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._
import net.liftweb.common.Full

object Book extends Book with LongKeyedMetaMapper[Book]{
  override def dbTableName = "books"
  override def dbAddTable = Full(populate _)
  private def populate {
    val titles = 
      "Lift in Action" :: 
      "Scala in Depth" :: 
      "Scala in Action" :: 
      "Hadoop in Action" :: Nil
   
   for(title <- titles)
    Book.create.title(title).save
  }
}

class Book extends LongKeyedMapper[Book] 
    with CreatedUpdated 
    with IdPK {
  def getSingleton = Book
  
  object title extends MappedString(this, 255)
}
