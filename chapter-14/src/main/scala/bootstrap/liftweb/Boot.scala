package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.{LiftRules,S,Req,GetRequest}
import net.liftweb.sitemap.{SiteMap,Menu}

// extended sessions
import net.liftweb.mapper.{StandardDBVendor,MapperRules,DefaultConnectionIdentifier,DB,Schemifier}
import sample.model.{User,ExtendedSession}

// jmx monitoring
import com.twitter.ostrich.{RuntimeEnvironment, ServiceTracker, Stats, StatsMBean}
import net.lag.configgy.Configgy

class Boot {
  def boot {
    // http
    LiftRules.addToPackages("sample")
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
    
    // mapper
    MapperRules.columnName = (_,name) => Helpers.snakify(name)
    MapperRules.tableName =  (_,name) => Helpers.snakify(name)
    
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Application.database)
      LiftRules.unloadHooks.append(() => Application.database.closeAllConnections_!()) 
    }
    
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
    
    // jmx monitoring
    //if (Props.getBool("jmx.enable", false))
    StatsMBean("manning.lia.sample")
    
    val runtime = new RuntimeEnvironment(getClass)
    val config = Configgy.config
    config("admin_text_port") = 9989
    config("admin_http_port") = 9990
    // start ostrich
    ServiceTracker.startAdmin(config, runtime)
    
  }
}

object Application {
  
  val sitemap = List(
    Menu("Home") / "index",
    Menu("Distributed Words") / "words"
  ) ::: User.menus
  
  lazy val database = DBVendor
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_fourteen;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
}


