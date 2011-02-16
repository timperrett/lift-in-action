package sample.snippet

import net.liftweb.common.Full
import sample.model.Book

class Demo {
  def example = Book.findAll.head.toForm(Full("Submit"), { _.save })
}