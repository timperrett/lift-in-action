package sample.model

import net.liftweb.common.Box
import net.liftweb.mapper.{MetaProtoExtendedSession,ProtoExtendedSession}

object ExtendedSession 
  extends ExtendedSession with MetaProtoExtendedSession[ExtendedSession]{
  override def dbTableName = "extended_sessions"
  def logUserIdIn(uid: String): Unit = User.logUserIdIn(uid)
  def recoverUserId: Box[String] = User.currentUserId
  type UserType = User
}

class ExtendedSession extends ProtoExtendedSession[ExtendedSession]{
  def getSingleton = ExtendedSession 
}
