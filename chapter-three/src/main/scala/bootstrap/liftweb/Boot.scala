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
// import example.travel.lib.{Helpers}

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("example.travel")
    
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
    
    // setup the 404 handler 
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })
    
    // LiftRules.loggedInTest = Full(() => User.loggedIn_?)
    
    // set the application sitemap
    // LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
    
    // def sitemap = SiteMap(
    //   Menu("Home") / "index" >> LocGroup("public"),
    //   Menu("Search") / "search" >> LocGroup("public"),
    //   Menu("History") / "history" >> LocGroup("public"),
    //   Menu("Auctions") / "auctions" >> LocGroup("public"),
    //   Menu("Auction Detail") / "auction" >> LocGroup("public"),
    //   // admin
    //   Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
    //   Menu("Suppliers") / "admin" / "suppliers" >> LocGroup("admin") submenus(Supplier.menus : _*),
    //   Menu("Auction Admin") / "admin" / "auctions" >> LocGroup("admin") submenus(Auction.menus : _*),
    // )
    // LiftRules.setSiteMap(sitemap)
    
    LiftRules.setSiteMap(SiteMap(Application.sitemap: _*))
    
    
    // setup the load pattern
    S.addAround(DB.buildLoanWrapper)
    
    // make requests utf-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}

object Application {
  val sitemap = List(
    Menu("Home") / "index" >> LocGroup("public"),
    Menu("Search") / "search" >> LocGroup("public"),
    Menu("History") / "history" >> LocGroup("public"),
    Menu("Auctions") / "auctions" >> LocGroup("public"),
    Menu("Auction Detail") / "auction" >> LocGroup("public") >> Hidden,
    // admin
    Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
    Menu("Suppliers") / "admin" / "suppliers" >> LocGroup("admin") submenus(Supplier.menus : _*),
    Menu("Auction Admin") / "admin" / "auctions" >> LocGroup("admin") submenus(Auction.menus : _*)
  ) ::: Customer.menus
    
    // Menu(Loc("Home", List("index"), "Home", LocGroup("public"))) ::
    // Menu(Loc("Search", List("search"), "Search", LocGroup("public"))) ::
    // Menu(Loc("History", List("history"), "History", LocGroup("public"))) ::
    // Menu(Loc("Auctions", List("auctions"), "Auctions", LocGroup("public"))) ::
    // Menu(Loc("Auction Detail", List("auction"), "Auction Detail", LocGroup("public"), Hidden)) ::
    // // admin
    // Menu(Loc("Admin", List("admin","index"), "Admin", LocGroup("admin"))) ::
    // Menu(Loc("Suppliers", List("admin", "suppliers"), "Suppliers", LocGroup("admin")), 
    //   Supplier.menus : _*
    // ) :: Menu(Loc("AuctionAdmin", List("admin", "auctions"), "Auctions", LocGroup("admin")),
    //   Auction.menus : _*
    // ) :: Customer.menus
    
  
  val database = DBVendor
  
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("com.mysql.jdbc.Driver"),
    Props.get("db.url").openOr("jdbc:mysql://localhost/liftinaction?user=root"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}



