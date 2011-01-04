package sample.snippet

import net.liftweb.http.{LiftScreen,S}
import sample.model.Book

object BookScreen extends LiftScreen {
  object book extends ScreenVar(Book.find(1) openOr Book.create)
  
  _register(() => book)
  
  def finish(){
    if(book.save) S.notice("Saved!")
    else S.error("Unable to complete save :-(")
  }
}