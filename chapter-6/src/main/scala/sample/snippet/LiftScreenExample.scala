package sample.snippet

import net.liftweb.http.{LiftScreen,S}

object AskAboutIceCreamOne extends LiftScreen {
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

