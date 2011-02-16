package sample.model.couch

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field._
import net.liftweb.couchdb.{CouchRecord,CouchMetaRecord}

class Book private () extends CouchRecord[Book]{
    def meta = Book
    
    val title = new StringField(this, "")
    val publishedInYear = new IntField(this, 1990)
}

object Book extends Book with CouchMetaRecord[Book]
