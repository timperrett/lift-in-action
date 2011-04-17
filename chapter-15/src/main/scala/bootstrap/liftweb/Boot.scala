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
// import com.twitter.ostrich.{RuntimeEnvironment, ServiceTracker, Stats, StatsMBean, Service}
// import net.lag.configgy.Config

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
    LiftRules.earlyInStateful.append(ExtendedSession.testCookieEarlyInStateful)
    
    // exception handler
    LiftRules.exceptionHandler.prepend {
      case (Props.RunModes.Production, _, exception) => RedirectResponse("/error")
    }
    
    /**
     * Startup and shutdown the Ostrich server
     * when the application cycles
     */
    OstrichWebAdmin.service
    
    LiftRules.unloadHooks.append(
      () => OstrichWebAdmin.service.foreach(_.shutdown))
    
    import com.twitter.ostrich._,
      admin.ServiceTracker,
      stats.Stats
    
    ServiceTracker.register(RequestTimer)
    
    // unfortunatly there is an issue with making a !? call here and 
    // ostrich doesnt like it. I'll look into it.
    Stats.addGauge("current_session_count"){ 
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
    Props.get("db.url").openOr("jdbc:h2:mem:chapter_fifteen;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
  import com.twitter.ostrich._, 
    admin.{RuntimeEnvironment},
    admin.config.{StatsConfig,AdminServiceConfig,TimeSeriesCollectorConfig}
  
  object OstrichWebAdmin extends AdminServiceConfig { 
    httpPort = 9990 
    statsNodes = new StatsConfig {
      reporters = new TimeSeriesCollectorConfig
    }
    lazy val service = 
      super.apply()(new RuntimeEnvironment(this))
  }
}

// final case object GimmehCount
object SessionMonitor extends LiftActor {
  private var sessionSize = 0
  protected def messageHandler = {
    case SessionWatcherInfo(sessions) => sessionSize = sessions.size
    // case GimmehCount => sessionSize.toDouble
  }
  def count = sessionSize
}

import com.twitter.ostrich._, 
  stats.Stats,
  admin.Service

object RequestTimer extends Service {
  object startTime extends RequestVar(0L)
  
  def beginServicing(session: LiftSession, req: Req){
    startTime(Helpers.millis)
  }
  
  def endServicing(session: LiftSession, req: Req, response: Box[LiftResponse]) {
    val delta = Helpers.millis - startTime.is
    Stats.addMetric("request_duration", delta.toInt)
  }
  override def start(){}
  override def shutdown(){}
  override def quiesce(){}
}
