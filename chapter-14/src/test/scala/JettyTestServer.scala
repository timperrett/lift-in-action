package sample.test

import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.ServletHolder
import org.mortbay.jetty.webapp.WebAppContext
// import org.mortbay.jetty.nio.SelectChannelConnector

object JettyTestServer {
  private val port = System.getProperty("jetty.test.port", "8989").toInt
  private var url = "http://localhost:" + port

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