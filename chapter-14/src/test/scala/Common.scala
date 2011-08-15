package sample.test

import org.specs.{Specification,ScalaCheck}
// import net.liftweb.http.BootManager

trait SetupAndDestroy { _: Specification => 
  def setup(): Unit
  def destroy(): Unit
  setup().beforeSpec
  destroy().afterSpec
}

trait JettySetupAndTearDown extends SetupAndDestroy { _: Specification => 
  def setup() = JettyTestServer.start
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
    private val browserPath = 
      if(System.getProperty("os.name").startsWith("Mac"))
        "*firefox /Applications/Firefox.app/Contents/MacOS/firefox"
      else
        "*firefox C:\\Program Files (x86)\\Mozilla Firefox\\Firefox.exe"
    
    lazy val browser = new DefaultSelenium("localhost", SeleniumTestServer.port, 
      browserPath, JettyTestServer.url+"/")

    def start(){
      browser.start()
    }
    def stop(){
      browser.stop()
    }
  }
} 

import net.liftweb.http.testing.TestKit
import net.liftweb.mockweb.WebSpec

class AllTests extends WebSpec with ScalaCheck with SeleniumSetupAndTearDown
  with SpecsExample 
  with TestKit
  with CookieListSpec 
  with WebServiceSpec
  with SeleniumExampleSpec {
    setSequential()
  }
  
