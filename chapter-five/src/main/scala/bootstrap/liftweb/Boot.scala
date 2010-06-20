package bootstrap.liftweb

// framework imports
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import net.liftweb.mapper.{DB,Schemifier,DefaultConnectionIdentifier,StandardDBVendor}

// app imports
import example.travel.model.{Auction,Supplier,Customer,Bid,Order,OrderAuction}
import example.travel.lib.{PaypalHandler}

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("example.travel")

    /**** database settings ****/

    // set the JNDI name that we'll be using
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"

    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      logger.error("No JNDI configured - using the default in-memory database.") 
      DB.defineConnectionManager(DefaultConnectionIdentifier, Application.database)
      // make sure cyote unloads database connections before shutting down
      LiftRules.unloadHooks.append(() => Application.database.closeAllConnections_!()) 
    }

    // automatically create the tables
    Schemifier.schemify(true, Schemifier.infoF _, 
      Bid, Auction, Supplier, Customer, Order, OrderAuction)

    // setup the loan pattern
    S.addAround(DB.buildLoanWrapper)

    /**** user experience settings ****/

    // set the time that notices should be displayed and then fadeout
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => Full(2 seconds, 2 seconds))

    LiftRules.loggedInTest = Full(() => Customer.loggedIn_?)
    
    /**** paypal settings ****/
    
    // wire up the various DispatchPFs for both PDT and IPN
    PaypalHandler.dispatch.foreach(LiftRules.dispatch.append(_))
    
    /**** request settings ****/

    // set the application sitemap
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))

    // setup the 404 handler 
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    // make requests utf-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("auction" :: key :: Nil,"",true,_),_,_) =>
           RewriteResponse("auction" :: Nil, Map("id" -> key.split("-")(0)))
    }
    
    logger.debug("DEBUG MODE ENABLED!")
  }
}

object Application {
  val MustBeLoggedIn = If(() => Customer.loggedIn_?, "")
  
  val sitemap = List(
    Menu("Home") / "index" >> LocGroup("public"),
    Menu("Auctions") / "auctions" >> LocGroup("public"),
    Menu("Search") / "search" >> LocGroup("public") >> MustBeLoggedIn,
    Menu("History") / "history" >> LocGroup("public") >> MustBeLoggedIn,
    Menu("Auction Detail") / "auction" >> LocGroup("public") >> Hidden,
    // admin
    Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
    Menu("Suppliers") / "admin" / "suppliers" >> LocGroup("admin") submenus(Supplier.menus : _*),
    Menu("Auction Admin") / "admin" / "auctions" >> LocGroup("admin") submenus(Auction.menus : _*)
  ) ::: Customer.menus
  
  val database = DBVendor
  
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("com.mysql.jdbc.Driver"),
    Props.get("db.url").openOr("jdbc:mysql://localhost/liftinaction?user=root"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}



