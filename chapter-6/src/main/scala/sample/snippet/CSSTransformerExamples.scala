package sample.snippet 

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml

class BasicExample {
  def samepl = {
    var static = "Change this text"
    "type=text" #> SHtml.text(static, static = _) &
    "type=submit" #> SHtml.onSubmitUnit(() => println(static))
  }
}
