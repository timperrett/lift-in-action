package sample.test

import net.liftweb.http.LiftRules

object BootManager {
  private var hasBooted = false
  def boot(){
    if(!hasBooted){
      hasBooted = true
      new bootstrap.liftweb.Boot().boot
    }
  }
  def cleanup(){
    if(hasBooted)
      LiftRules.unloadHooks.toList.foreach(_())
  }
}

trait SetupAndTearDown {
  def setup() = BootManager.boot
  def destroy() = BootManager.cleanup
}

trait JettySetupAndTearDown {
  def setup() = JettyTestServer.start()
  def destroy() = JettyTestServer.stop()
}