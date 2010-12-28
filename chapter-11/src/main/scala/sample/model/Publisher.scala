package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object Publisher extends Publisher with LongKeyedMetaMapper[Publisher]{
  override def dbTableName = "publishers"
}

class Publisher extends LongKeyedMapper[Publisher] with CreatedUpdated with IdPK {
  def getSingleton = Publisher
  
  object name extends MappedString(this, Titles)
  object description extends MappedText(this, 255)
}
