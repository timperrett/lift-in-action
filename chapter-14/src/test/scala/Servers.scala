package sample.test

import org.mortbay.jetty.{Server,Connector}
import org.mortbay.jetty.servlet.ServletHolder
import org.mortbay.jetty.webapp.WebAppContext
import org.mortbay.jetty.nio.SelectChannelConnector

object JettyTestServer {
  
  private val server: Server = {
    val svr = new Server
    val connector = new SelectChannelConnector
    connector.setMaxIdleTime(30000);
    
    val context = new WebAppContext
    context.setServer(svr)
    context.setContextPath("/")
    context.setWar("chapter-14/src/main/webapp")
    
    svr.setConnectors(Array(connector));
    svr.addHandler(context)
    svr
  }
  
  lazy val port = server.getConnectors.head.getLocalPort
  lazy val url = "http://localhost:" + port
  
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

object SeleniumTestServer {
  private val rc = new RemoteControlConfiguration()
  rc.setPort(port)
  
  private val seleniumserver = new SeleniumServer(rc)
  lazy val port = System.getProperty("selenium.server.port", "4444").toInt
  
  def start(){
    seleniumserver.boot()
    seleniumserver.start()
    seleniumserver.getPort()
  }
  def stop(){
    seleniumserver.stop()
  }
}
