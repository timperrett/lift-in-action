package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{StatefulSnippet,SHtml}

class CountIncrement extends StatefulSnippet {
  val dispatch: DispatchIt = {
    case _ if count < 5 => renderBelowFive
    case _ if count >= 5 => renderAboveFive
  }
  
  def renderBelowFive =
    "count" #> count.toString & 
    "increment" #> SHtml.submit("Increment", () => count+=1)
  
  def renderAboveFive = (xhtml: NodeSeq) => Text("Count above five.")
  
  private var count = 0
}
