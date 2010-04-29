package bootstrap {
package liftweb {
  
  import _root_.net.liftweb.http.LiftFilter
  import _root_.javax.servlet._ 
  import _root_.javax.servlet.http._
  
  class StaxLiftFilter extends LiftFilter {
    override def init(config: FilterConfig){
      System.setProperty("run.mode", "production")
      super.init(config)
    }
  }
  
}}
