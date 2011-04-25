package sample.snippet

import net.liftweb.http.{LiftScreen,S}
import net.liftweb.http.js.JsCmds.JsHideId

object LiftScreenAJAXExample extends LiftScreen {
  val flavour = field("What's your favorite Ice cream flavor", "",
    trim, valMinLen(2,"Name too short"), valMaxLen(40,"That's a long name"))
  
  val sauce = field("Like chocalate sauce?", false)
  
  override def calcAjaxOnDone = JsHideId("screen")
  
  def finish(){
    if(sauce) S.notice("%s tastes great with chocolate sauce!".format(flavour.is))
    else S.notice("I like %s too!".format(flavour.is))
  }
}
