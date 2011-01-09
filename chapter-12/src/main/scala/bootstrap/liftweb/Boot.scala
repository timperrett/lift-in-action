package bootstrap.liftweb

import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.http.{LiftRules,S}
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.mapper.{DefaultConnectionIdentifier,DB,StandardDBVendor}
// squeryl imports
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.adapters.PostgreSqlAdapter
// application imports
import sample.model.squeryl.Bookstore

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    /**
     * Database setup
     */
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!())
    }
    
    /**
     * Tell Squeryl what database adapter you want to use. Other options are:
     * DB2Adapter
     * H2Adapter
     * MSSQLServer
     * MySQLAdapter
     * OracleAdapter
     */
    SquerylRecord.init(() => new PostgreSqlAdapter)
    
    /**
     * If this is development mode, then attempt to auto-generate the schema
     */
    if(Props.devMode)
      DB.use(DefaultConnectionIdentifier){ connection =>  Bookstore printDdl }
      //DB.use(DefaultConnectionIdentifier){ connection => Bookstore.create }
    
    /**
     * Add the request wrapper for database connectivity
     */
    S.addAround(DB.buildLoanWrapper)
    
    /**
     * Build the sitemap
     */
    LiftRules.setSiteMap(SiteMap(List(
      Menu("Home") / "index",
      Menu("Squeryl Bookstore") / "squeryl" / "index",
      Menu("MongoDB Bookstore") / "mongo" / "index"
    ):_*))
  }
  
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_12;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
}
