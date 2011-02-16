package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{StatefulSnippet,SHtml}

// Listing 6.8
class CountIncrement extends StatefulSnippet {
  val dispatch: DispatchIt = {
    case "render" if count < 5 => renderBelowFive _
    case "render" if count >= 5 => renderAboveFive _
  }
  
  def renderBelowFive(xhtml: NodeSeq) = bind("s",xhtml,
    "count" -> Text(count.toString),
    "increment" -> SHtml.submit("Increment", () => count+=1))
  
  def renderAboveFive(xhtml: NodeSeq) = Text("Count above five.")
  
  private var count = 0
}
