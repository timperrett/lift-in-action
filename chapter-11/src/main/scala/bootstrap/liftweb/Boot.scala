package bootstrap.liftweb

import net.liftweb.util.{Helpers,Props}
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.mapper.{MapperRules,DefaultConnectionIdentifier,DB,Schemifier,StandardDBVendor}
import sample.model.{Book,Author,Publisher,MappedTypesExample}

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("sample")
    
    MapperRules.columnName = (_,name) => Helpers.snakify(name)
    MapperRules.tableName =  (_,name) => Helpers.snakify(name)
    
    // handle JNDI not being avalible
    if (!DB.jndiJdbcConnAvailable_?){
      DB.defineConnectionManager(DefaultConnectionIdentifier, Environment.database)
      LiftRules.unloadHooks.append(() => Environment.database.closeAllConnections_!()) 
    }
    
    // automatically create the tables
    Schemifier.schemify(true, Schemifier.infoF _, 
      Book, Author, Publisher, MappedTypesExample)
    
    LiftRules.stripComments.default.set(() => false)
    
    // Build the application SiteMap
    def sitemap = SiteMap(Menu("Home") / "index")
    LiftRules.setSiteMap(sitemap)
  }
}

object Environment {
  lazy val sitemap = List(
    Menu("Home") / "index"
  )
  
  val database = DBVendor
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/chapter_eleven;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}