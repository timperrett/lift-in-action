package sample.test

import net.liftweb.http.LiftRules

object BootManager {
  private var hasBooted = false
  def boot(){
    if(!hasBooted){
      println("************************* BOOTING")
      hasBooted = true
      new bootstrap.liftweb.Boot().boot
    }
  }
  def cleanup(){
    if(hasBooted)
      LiftRules.unloadHooks.toList.foreach(_())
  }
}

import org.specs.Specification

trait SetupAndDestroy { _: Specification => 
  def setup(): Unit
  def destroy(): Unit
  setup().beforeSpec
  destroy().afterSpec
}

trait BootSetupAndTearDown extends SetupAndDestroy { _: Specification => 
  def setup() = BootManager.boot
  def destroy() = BootManager.cleanup
}

trait JettySetupAndTearDown extends SetupAndDestroy { _: Specification => 
  def setup() = JettyTestServer.start()
  def destroy() = JettyTestServer.stop()
}