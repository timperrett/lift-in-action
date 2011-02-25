package sample.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http.{S,DispatchSnippet}

class Service extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = {
    fetch
    Text("Done!")
  }
  protected def fetch { Thread.sleep(5000) }
}

class StubService extends Service {
  override def fetch = { }
}