package sample.model.squeryl

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.annotations.Column
import net.liftweb.record.field._

class Book private () extends Record[Book] with KeyedRecord[Long] {
    def meta = Book
    
    @Column(name="id")
    val idField = new LongField(this, 100)
    // forigen keys
    val publisherId = new LongField(this, 0)
    val authorId = new LongField(this, 0)
    // fields
    val title = new StringField(this, "")
    val publishedInYear = new IntField(this, 1990)
    
    // relatinoship helpers
    def author = Bookstore.authors.lookup(authorId.value)
    def publisher = Bookstore.publishers.where(p => p.id === publisherId)
}

object Book extends Book with MetaRecord[Book]
