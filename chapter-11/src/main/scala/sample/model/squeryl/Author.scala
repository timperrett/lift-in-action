package sample.model.squeryl

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, LongTypedField, OptionalDateTimeField, OptionalIntField, StringField}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column

class Author private () extends Record[Author] with KeyedRecord[Long] {
    def meta = Author
    
    @Column(name="id")
    val idField = new LongField(this, 100)
    val name = new StringField(this, "")
    
    val age = new OptionalIntField(this)
    val birthday = new OptionalDateTimeField(this)
    
    // relationship helpers
    def books: Query[Book] = Bookstore.books.where(_.authorId === id)
}

object Author extends Author with MetaRecord[Author]
