package bootstrap.liftweb

import net.liftweb.common.LazyLoggable
import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.{LiftRules,S}
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.mapper.{MapperRules,DefaultConnectionIdentifier,
  DBLogEntry,DB,Schemifier,StandardDBVendor}
import sample.model._

// object Log extends Logger

class Boot extends LazyLoggable {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("sample")
    
    MapperRules.columnName = (_,name) => Helpers.snakify(name)
    MapperRules.tableName  = (_,name) => Helpers.snakify(name)
    
    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Database)
      LiftRules.unloadHooks.append(() => Database.closeAllConnections_!()) 
    }
    
    S.addAround(DB.buildLoanWrapper)
        
    // add a logging function
    DB.addLogFunc((query, time) => {
      logger.info("All queries took " + time + " milliseconds")
      query.allEntries.foreach((entry: DBLogEntry) => 
        logger.info(entry.statement + " took " + entry.duration + "ms"))
    })
    
    // automatically create the tables
    if(Props.devMode)
      Schemifier.schemify(true, Schemifier.infoF _, 
        Author, BookAuthors, Book, Publisher, Account,
        MappedTypesExample, AggregationSample)
    
    LiftRules.stripComments.default.set(() => false)
    
    // Build the application SiteMap
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index",
      Menu("LiftScreen Sample") / "liftscreen",
      Menu("Mapper toForm Sample") / "toform"
    ))
  }
  
  object Database extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_eleven;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}
