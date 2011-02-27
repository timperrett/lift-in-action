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

trait SeleniumSetupAndTearDown extends JettySetupAndTearDown { _: Specification => 
  override def setup(){
    println("************************* STARTING")
    super.setup()
    SeleniumTestServer.start()
    Thread.sleep(1000)
    SeleniumTestClient.start()
  }
  override def destroy(){
    SeleniumTestClient.stop()
    Thread.sleep(1000)
    SeleniumTestServer.stop()
    super.destroy()
    println("******** SHUTTING DOWN")
  }
} 




