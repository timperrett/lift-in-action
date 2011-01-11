package bootstrap.liftweb

// framework imports
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import net.liftweb.mapper.{DB,Schemifier,DefaultConnectionIdentifier,StandardDBVendor,MapperRules}
import net.liftweb.paypal.PaypalRules

// app imports
import example.travel.model.{Auction,Supplier,Customer,Bid,Order,OrderAuction,AuctionMachine}
import example.travel.lib.{PaypalHandler}

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("example.travel")
    
    /**** database settings ****/
    
    MapperRules.columnName = (_,name) => StringHelpers.snakify(name)
    MapperRules.tableName =  (_,name) => StringHelpers.snakify(name)
    
    // set the JNDI name that we'll be using
    DefaultConnectionIdentifier.jndiName = "jdbc/liftinaction"

    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      logger.error("No JNDI configured - using the default in-memory database.") 
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      // make sure cyote unloads database connections before shutting down
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!()) 
    }

    // automatically create the tables
    Schemifier.schemify(true, Schemifier.infoF _, 
      Bid, Auction, Supplier, Customer, Order, OrderAuction, AuctionMachine)

    // setup the loan pattern
    S.addAround(DB.buildLoanWrapper)

    /**** user experience settings ****/

    // set the time that notices should be displayed and then fadeout
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => Full(2 seconds, 2 seconds))

    LiftRules.loggedInTest = Full(() => Customer.loggedIn_?)
    
    /**** paypal settings ****/
    
    PaypalRules.init
    
    // wire up the various DispatchPFs for both PDT and IPN
    PaypalHandler.dispatch.foreach(LiftRules.dispatch.append(_))
    
    /**** request settings ****/
    
    val MustBeLoggedIn = Customer.loginFirst
    // set the application sitemap
    LiftRules.setSiteMap(SiteMap(List(
      Menu("Home") / "index" >> LocGroup("public"),
      Menu("Auctions") / "auctions" >> LocGroup("public"),
      Menu("Search") / "search" >> LocGroup("public") >> MustBeLoggedIn,
      Menu("History") / "history" >> LocGroup("public") >> MustBeLoggedIn,
      Menu("Auction Detail") / "auction" >> LocGroup("public") >> Hidden,
      Menu("Checkout") / "checkout" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu("Checkout Finalize") / "summary" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu("Transaction Complete") / "paypal" / "success" >> LocGroup("public") >> Hidden,
      Menu("Transaction Failure") / "paypal" / "failure" >> LocGroup("public") >> Hidden,
      // admin
      Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
      Menu("Suppliers") / "admin" / "suppliers" >> LocGroup("admin") submenus(Supplier.menus : _*),
      Menu("Auction Admin") / "admin" / "auctions" >> LocGroup("admin") submenus(Auction.menus : _*)
    ) ::: Customer.menus:_*))

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
    
  }
  
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_five;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
}
