package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http._

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}
