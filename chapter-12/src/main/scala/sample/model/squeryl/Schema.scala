package sample.model.squeryl

import org.squeryl.Schema
import net.liftweb.squerylrecord.RecordTypeMode._

object Bookstore extends Schema {
  val authors = table[Author]("authors")
  // on(authors)(a => declare(
  //   a.id is(primaryKey,autoIncremented)
  // ))
  
  val books = table[Book]("books")
  // on(books)(b => declare(
  //   b.id is(primaryKey,autoIncremented)
  // ))
  
  val publishers = table[Publisher]("publishers")
  // on(publishers)(p => declare(
  //   p.id is(autoIncremented)
  // ))
  
}
