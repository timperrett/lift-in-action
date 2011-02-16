package sample.snippet

import net.liftweb.http.{LiftScreen,S}
import sample.model.mongo.Book

object MongoScreen extends LiftScreen {
  object book extends ScreenVar(Book.createRecord)
  addFields(() => book.is)
  def finish(){
    S.redirectTo("/")
  }
}