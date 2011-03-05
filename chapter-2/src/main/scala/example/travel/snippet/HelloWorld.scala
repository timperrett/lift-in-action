package example.travel.snippet

import scala.xml.{NodeSeq}
import net.liftweb.util.Helpers._

class HelloWorld {
  def howdy = "*" #> <strong>hello world!</strong>
}
