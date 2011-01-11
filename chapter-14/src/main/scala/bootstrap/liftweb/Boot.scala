package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{LazyLoggable,Box,Full}
import net.liftweb.actor.LiftActor
import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.{LiftRules,S,Req,GetRequest,LiftSession,
  SessionMaster,SessionWatcherInfo,RequestVar,LiftResponse,RedirectResponse}
import net.liftweb.sitemap.{SiteMap,Menu,Loc}

// extended sessions
import net.liftweb.mapper.{StandardDBVendor,MapperRules,DefaultConnectionIdentifier,DB,Schemifier}
import sample.model.{User,ExtendedSession}

// jmx monitoring
import com.twitter.ostrich.{RuntimeEnvironment, ServiceTracker, Stats, StatsMBean, Service}
import net.lag.configgy.Config

class Boot extends LazyLoggable {
  def boot {
    // http
    LiftRules.addToPackages("sample")
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    // sitemap
    import Loc.ExtLink
    LiftRules.setSiteMap(SiteMap(List(
      Menu("Home") / "index",
      Menu("Distributed Words") / "words",
      Menu(Loc("ostrich.graphs", ExtLink("http://127.0.0.1:9990/graph/"), "Ostrich: Graphs")) 
    ) ::: User.menus:_*))
    
    // database
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!()) 
    }
    
    logger.info("About to schemify...")
    Schemifier.schemify(true, Schemifier.infoF _, User, ExtendedSession)
    S.addAround(DB.buildLoanWrapper)
    
    // extended sessions
    S.addAround(ExtendedSession.requestLoans)
    LiftRules.liftRequest.append { 
      case Req("classpath" :: _, _, _) => true
      case Req("favicon" :: Nil, "ico", GetRequest) => false
      case Req(_, "css", GetRequest) => false 
      case Req(_, "js", GetRequest) => false 
    }
    
    // comet configuration
    // import net.liftweb.http.provider.servlet.containers.Jetty7AsyncProvider
    // LiftRules.servletAsyncProvider = new Jetty7AsyncProvider(_)
    
    // exception handler
    LiftRules.exceptionHandler.prepend {
      case (Props.RunModes.Production, _, exception) => RedirectResponse("/error")
    }
    
    StatsMBean("manning.lia.sample")
    
    // jmx monitoring
    //if (Props.getBool("jmx.enable", false))
    logger.info("Booting Ostrich...")
    val runtime = new RuntimeEnvironment(getClass)
    var config = new Config
    config("admin_http_port") = 9990
    ServiceTracker.register(RequestTimer)
    ServiceTracker.startAdmin(config, runtime)
    
    Stats.makeGauge("current_session_count"){ 
      SessionMonitor.count.toDouble 
    }
    
    // session gauge
    SessionMaster.sessionWatchers = SessionMonitor :: SessionMaster.sessionWatchers
    
    // request timer
    LiftSession.onBeginServicing = RequestTimer.beginServicing _ ::
      LiftSession.onBeginServicing
    
    LiftSession.onEndServicing = RequestTimer.endServicing _ ::
      LiftSession.onEndServicing
    
  }
  
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:mem:chapter_fourteen;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
  
}

object SessionMonitor extends LiftActor {
  private var sessionSize = 0
  protected def messageHandler = {
    case SessionWatcherInfo(sessions) => sessionSize = sessions.size
  }
  def count = sessionSize
}

object RequestTimer extends Service {
  object startTime extends RequestVar(0L)
  
  def beginServicing(session: LiftSession, req: Req){
    startTime(Helpers.millis)
  }
  
  def endServicing(session: LiftSession, req: Req, response: Box[LiftResponse]) {
    val delta = Helpers.millis - startTime.is
    Stats.addTiming("request_duration", delta.toInt)
  }
  def shutdown(){}
  def quiesce(){}
}
