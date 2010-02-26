package example.travel {
package snippet {

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.util.Helpers._

class HelloWorld {
  def howdy(xhtml: NodeSeq): NodeSeq =
    bind("b", xhtml,
      "time" -> (new _root_.java.util.Date).toString
    )
}

}}