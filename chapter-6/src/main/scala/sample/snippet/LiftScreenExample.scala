package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.common.Box
import net.liftweb.http.{LiftScreen,S,SHtml}

class AskAboutIceCreamOne extends LiftScreen {
  val flavor = field("What's your favorite Ice cream flavor", "")
  def finish(){
    S.notice("I like "+flavor.is+" too!")
  }
}

object AskAboutIceCreamTwo extends LiftScreen {
  val flavor = field("What's your favorite Ice cream flavor", "",
          trim, valMinLen(2,"Name too short"),
          valMaxLen(40,"That's a long name"))
  
  val sauce = field("Like chocalate sauce?", false)
  
  def finish(){
    if(sauce){
      S.notice(flavor.is+" tastes especially good with chocolate sauce!")
    }
    else S.notice("I like "+flavor.is+" too!")
  }
}

case class Book(reference: Long, title: String)

object ScreenWithCustomField extends LiftScreen {
  val password = new Field { 
    type ValueType = String 
    override def name = "Password" 
    override implicit def manifest = buildIt[String] 
    override def default = "" 
    override def toForm: Box[NodeSeq] = SHtml.password(is, set _) 
  }
  
  val books = field("Choose book", 
    List(Book(123L, "Lift in Action"), Book(456L, "Scala in Depth")))
    
  def finish() = println("Submitted")
}
