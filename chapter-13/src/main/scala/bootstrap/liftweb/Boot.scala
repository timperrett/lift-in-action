package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,LazyLoggable}
import net.liftweb.util.Helpers.{tryo,randomString}
import net.liftweb.http.{LiftRules,S,RequestMemoize,RedirectResponse}
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.sitemap.Loc.{EarlyResponse,Hidden}

class Boot extends LazyLoggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    /**
     * Set the character encoding to utf-8 early in the pipline
     */
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    /**
     * Build the sitemap
     */
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index" >> EarlyResponse(() => Full(RedirectResponse("/jee/"))) >> Hidden,
      Menu("Java Enterprise Integration") / "jee" / "index" submenus(
        Menu("JPA: Authors: List") / "jee" / "authors" / "index",
        Menu("JPA: Authors: Add") / "jee" / "authors" / "add",
        Menu("JPA: Books: Add") / "jee" / "books" / "add"
      ),
      Menu("Messaging and Distribution") / "distributed" >> EarlyResponse(() => Full(RedirectResponse("/distributed/akka-calculator"))) submenus(
        Menu("Comet Calculator") / "distributed" / "akka-calculator"
      )
    ))
    
    import akka.actor.Actor
    import akka.actor.Actor.{remote,actorOf}
    import akka.actor.Supervisor
    import akka.config.Supervision.{SupervisorConfig,OneForOneStrategy,Supervise,Permanent}
    import sample.actor.{HelloWorldActor,IntTransformer}
    
    /**
     * Boot the akka remote actor service
     * I've disabled this during development as its sodding 
     * annoying to keep having the ports occupied!
     */
    // remote.start("localhost", 2552)
    // remote.register("hello-service", actorOf[HelloWorldActor])
    
    LiftRules.unloadHooks.append(() => {
      Actor.registry.shutdownAll
    }) 
    
    /**
     * Configure the supervisor heirarchy and determine the 
     * respective cases of failure.
     */
    Supervisor(
      SupervisorConfig(
        OneForOneStrategy(List(classOf[Throwable]), 3, 1000),
        Supervise(
          actorOf[sample.actor.IntTransformer],
          Permanent,
          true) ::
        Supervise(
          actorOf[sample.comet.Calculator],
          Permanent) ::
        Nil))
  }
}
