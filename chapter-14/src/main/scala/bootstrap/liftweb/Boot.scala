package bootstrap.liftweb

// framework imports
import net.liftweb.common.{LazyLoggable,Full}
import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.{S,LiftRules,RedirectResponse}
import net.liftweb.sitemap.{SiteMap,Loc,Menu}
import net.liftweb.mapper.{DB,Schemifier,DefaultConnectionIdentifier,StandardDBVendor,MapperRules}
import sample.env.{Environment,Development,Production}

class Boot extends LazyLoggable {
  def boot {
    // make requests utf-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    val environment: Environment = Props.mode match {
      case Props.RunModes.Production => Production
      case _ => Development
    }
    
    LiftRules.snippetDispatch.append {
      case "service" => environment.serviceSnippet
    }
    
    MapperRules.columnName = (_,name) => Helpers.snakify(name)
    MapperRules.tableName =  (_,name) => Helpers.snakify(name)
    
    // set the JNDI name that we'll be using
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"

    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      logger.warn("No JNDI configured - making a direct application connection") 
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      // make sure cyote unloads database connections before shutting down
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!()) 
    }
    
    // automatically create the tables
    // Schemifier.schemify(true, Schemifier.infoF _, 
      // Bid, Auction, Supplier, Customer, Order, OrderAuction, AuctionMachine)
    
    // setup the loan pattern
    S.addAround(DB.buildLoanWrapper)
    
    /**** user experience settings ****/
    
    import Loc.EarlyResponse
    
    // set the application sitemap
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index",
      Menu("Testing Frameworks") / "frameworks" / "index",
      Menu("TestKit Examples") / "testkit" / "index" >> EarlyResponse(() => Full(RedirectResponse("/testkit/cookies"))) submenus(
        Menu("Stateful Integration") / "testkit" / "cookies"
      ),
      Menu("Writing Testable Code") / "practices" / "index" submenus(
        
      )
    ))
  }
  
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_14;FILE_LOCK=NO"),
    Props.get("db.user"),
    Props.get("db.pass"))
}
