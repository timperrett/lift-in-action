package sample.model

import scala.xml.{NodeSeq,Node}
import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
import net.liftweb.sitemap.Loc._
import net.liftweb.mapper._
import com.twitter.ostrich.stats.Stats

object User extends User 
    with KeyedMetaMapper[Long, User]
    with MetaMegaProtoUser[User] with Loggable {
  
  override def dbTableName = "users"
  override def fieldOrder = id :: firstName :: lastName :: email :: password :: Nil
  override val basePath = "account" :: Nil
  override def homePage = "/"
  override def skipEmailValidation = true
  override def screenWrap: Box[Node] = 
    Full(
      <lift:surround with="default" at="content">
        <lift:bind />
      </lift:surround>
    )
  // for extended sessions
  onLogIn = List(u => Stats.incr("userLoggedIn"), ExtendedSession.userDidLogin(_))
  onLogOut = List(u => Stats.incr("usersLoggedOut"), ExtendedSession.userDidLogout(_))
}

class User extends MegaProtoUser[User] with CreatedUpdated {
  def getSingleton = User
}


