package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.common.LazyLoggable
import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.{LiftRules,S,Req,GetRequest}
import net.liftweb.sitemap.{SiteMap,Menu,Loc}

// extended sessions
import net.liftweb.mapper.{StandardDBVendor,MapperRules,DefaultConnectionIdentifier,DB,Schemifier}
import sample.model.{User,ExtendedSession}

// jmx monitoring
import com.twitter.ostrich.{RuntimeEnvironment, ServiceTracker, Stats, StatsMBean}
import net.lag.configgy.Config

class Boot extends LazyLoggable {
  def boot {
    // http
    LiftRules.addToPackages("sample")
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
    
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Application.database)
      LiftRules.unloadHooks.append(() => Application.database.closeAllConnections_!()) 
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
    
    // jmx monitoring
    //if (Props.getBool("jmx.enable", false))
    logger.info("Booting Ostrich...")
    val runtime = new RuntimeEnvironment(getClass)
    var config = new Config
    config("admin_http_port") = 9990
    config("admin_jmx_package") = "manning.lia.sample"
    ServiceTracker.startAdmin(config, runtime)
  }
}

object Application {
  import Loc.ExtLink
  val sitemap = List(
    Menu("Home") / "index",
    Menu("Distributed Words") / "words",
    Menu(Loc("ostrich.graphs", ExtLink("http://127.0.0.1:9990/graph/"), "Ostrich: Graphs")) 
  ) ::: User.menus
  
  lazy val database = DBVendor
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:mem:chapter_fourteen;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
}


