package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,S,RedirectResponse,SessionVar}
import net.liftweb.http.auth.{HttpBasicAuthentication,AuthRole,userRoles}
import net.liftweb.mapper.{DefaultConnectionIdentifier,DB,Schemifier,StandardDBVendor,MapperRules}
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import sample.lib.Wiki
import sample.model.WikiEntry

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
    MapperRules.columnName = (_,name) => snakify(name)
    MapperRules.tableName =  (_,name) => snakify(name)
    
    // set the JNDI name that we'll be using
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"

    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      logger.error("No JNDI configured - using the default H2 database.") 
      DB.defineConnectionManager(DefaultConnectionIdentifier, Application.database)
      LiftRules.unloadHooks.append(() => Application.database.closeAllConnections_!()) 
    }

    // automatically create the tables
    Schemifier.schemify(true, Schemifier.infoF _, WikiEntry)

    // setup the loan pattern
    S.addAround(DB.buildLoanWrapper)
    
    
    LiftRules.authentication = HttpBasicAuthentication("yourRealm"){
      case (un, pwd, req) => if(un == "admin" && pwd == "password"){
        userRoles(AuthRole("admin")); true
      } else false
    }
    
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
  }
}


object LoggedIn extends SessionVar[Box[Long]](Empty)

object Application {
  
  lazy val MySnippets = new DispatchLocSnippets {  
    val dispatch: PartialFunction[String, NodeSeq => NodeSeq] = {  
      case "demo" => xhtml => bind("l",xhtml,"sample" -> "sample")
      case "thing" => xhtml => bind("x",xhtml,"some" -> "example")
    }
  }
  
  val sitemap = List(
    Menu("Home") / "index",
    Menu("Submenu Example") / "sample" submenus(
      Menu("Another") / "sample" / "another",
      Menu("Example") / "sample" / "example"
    ),
    Menu("Confidential Thing") / "confidential" / "thing" >> HttpAuthProtected(req => Full(AuthRole("admin"))),
    Menu("Another") / "sample" / "withtitle" >> Title(x => Text("Some lovely title"))
        >> TestAccess(() => LoggedIn.is.choice(x => Empty)(Full(RedirectResponse("login")))),
    Menu("Example") / "sample" / "localhost-only" >> MySnippets >> Test(req => req.hostName == "localhost"),
    Menu("Edit Something") / "edit" >> Hidden >> Unless(() => S.param("id").isEmpty, () => RedirectResponse("index")),
    Menu(Wiki),
    Menu("JSON Menu") / "json"
  )
  
  val database = DBVendor
  
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/temp;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}