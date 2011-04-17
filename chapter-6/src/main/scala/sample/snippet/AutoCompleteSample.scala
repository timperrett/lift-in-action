package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.widgets.autocomplete.AutoComplete

class AutoCompleteSample {
  private val data = List("Timothy","Derek","Ross","Tyler","Indrajit","Harry","Greg","Debby")
  
  def sample = "*" #> AutoComplete("", (current,limit) => 
    data.filter(_.toLowerCase.startsWith(current.toLowerCase)), 
      x => println("Submitted: " + x))
}