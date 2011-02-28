package net.liftweb.http

object BootManager {
  def boot(){
    if(!LiftRules.doneBoot)
      new bootstrap.liftweb.Boot().boot
  }
  def cleanup(){
    // if(LiftRules.doneBoot)
      // LiftRules.unloadHooks.toList.foreach(_())
  }
}
