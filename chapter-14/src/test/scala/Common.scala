package sample.test

import org.specs.Specification
import net.liftweb.http.BootManager

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

import com.thoughtworks.selenium.DefaultSelenium

trait SeleniumSetupAndTearDown extends JettySetupAndTearDown { _: Specification => 
  override def setup(){
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
  }
  
  object SeleniumTestClient {
    lazy val browser = new DefaultSelenium("localhost", SeleniumTestServer.port, 
      "*firefox", JettyTestServer.url+"/")

    def start(){
      browser.start()
    }
    def stop(){
      browser.stop()
    }
  }
} 




