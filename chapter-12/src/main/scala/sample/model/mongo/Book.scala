package sample.model.mongo

import net.liftweb.record.field._
import net.liftweb.mongodb.{JsonObject,JsonObjectMeta}
import net.liftweb.mongodb.record.{MongoRecord,MongoMetaRecord,MongoId}
import net.liftweb.mongodb.record.field._

class Book private () extends MongoRecord[Book] with MongoId[Book]{
  def meta = Book
  
  object title extends StringField(this, "")
  object publishedInYear extends IntField(this, 1990)
  
  object publisher extends JsonObjectField[Book, Publisher](this, Publisher) {
    def defaultValue = Publisher("", "")
  }
  
  object authors extends JsonObjectField[Book, Authors](this, Authors) {
    def defaultValue = Authors(Nil)
  }
}

object Book extends Book with MongoMetaRecord[Book]


case class Publisher(name: String, description: String) extends JsonObject[Publisher] {
  def meta = Publisher
}
object Publisher extends JsonObjectMeta[Publisher]

case class Authors(list: List[Author]) extends JsonObject[Authors]{
  def meta = Authors
}
object Authors extends JsonObjectMeta[Authors]

case class Author(firstName: String, lastName: String, email: String) 
      extends JsonObject[Author] {
  def meta = Author
}
object Author extends JsonObjectMeta[Author]