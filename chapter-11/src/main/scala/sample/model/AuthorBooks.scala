package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object BookAuthors extends BookAuthors with MetaMapper[BookAuthors]

class BookAuthors extends Mapper[BookAuthors] {
  def getSingleton = BookAuthors
  object author extends LongMappedMapper(this, Author)
  object book extends LongMappedMapper(this, Book)
}