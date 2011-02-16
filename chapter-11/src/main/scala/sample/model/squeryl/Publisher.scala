package sample.model.squeryl

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column

class Publisher private () extends Record[Publisher] with KeyedRecord[Long] {
    def meta = Publisher
    
    @Column(name="id")
    val idField = new LongField(this, 1)
    val name = new StringField(this, "")
    
    def books: Query[Book] = Bookstore.books.where(_.publisherId === id)
}

object Publisher extends Publisher with MetaRecord[Publisher]
