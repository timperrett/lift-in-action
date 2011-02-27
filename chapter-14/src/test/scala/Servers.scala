package sample.test

import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.ServletHolder
import org.mortbay.jetty.webapp.WebAppContext

object JettyTestServer {
  private val port = System.getProperty("jetty.test.port", "8989").toInt
  val url = "http://localhost:" + port

  private val server: Server = {
    val svr = new Server(port)
    val context = new WebAppContext()
    context.setServer(svr)
    context.setContextPath("/")
    context.setWar("chapter-14/src/main/webapp")
    svr.addHandler(context)
    svr
  }
  
  def baseUrl = url
  
  def urlFor(path: String) = baseUrl + path

  def start() = server.start()

  def stop(){
    server.stop()
    server.join()
  }
}

import org.openqa.selenium.server.RemoteControlConfiguration
import org.openqa.selenium.server.SeleniumServer
import com.thoughtworks.selenium.DefaultSelenium

object SeleniumTestServer {
  private val rc = new RemoteControlConfiguration()
  private val seleniumserver = new SeleniumServer(rc)
  val port = System.getProperty("selenium.server.port", "4444").toInt
  
  def start(){
    rc.setPort(port)
    seleniumserver.boot()
    seleniumserver.start()
    seleniumserver.getPort()
  }
  def stop(){
    seleniumserver.stop()
  }
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
