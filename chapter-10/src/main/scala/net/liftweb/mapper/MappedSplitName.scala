package net.liftweb.mapper

import net.liftweb.mapper._
import scala.xml.Elem
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.util.FatLazy
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonAST.JString
import net.liftweb.http.S
import net.liftweb.http.js.{JE,JsExp}
import java.sql.Types
import java.lang.reflect.Method
import java.util.Date

abstract class MappedSplitName[T<:Mapper[T]](val fieldOwner: T, val maxLen: Int) extends MappedField[String, T] {
  private var firstName = FatLazy(defaultValue)
  private var lastName = FatLazy(defaultValue)
  
  private def wholeGet = "%s %s".format(firstName.get, lastName.get)
  
  def dbFieldClass = classOf[String]
  
  override def dbColumnCount = 2
  
  override def dbColumnNames(name: String) = 
    List("first","last").map(_ + "_" + name.toLowerCase)
  
  override def real_i_set_!(value : String): String = {
    println("setting value: " )
    value.split(" ") match {
      case Array(first, last) => firstName.set(first); lastName.set(last)
      case _ => ""
    }
    this.dirty_?(true)
    wholeGet
  }
  
  override def i_is_! = {
    println("caling i_is_!")
    wholeGet
  }
  
  override lazy val dbSelectString = dbColumnNames(name).map(cn => 
    fieldOwner.getSingleton._dbTableNameLC + "." + cn).mkString(", ")
  
  override def jdbcFriendly(column: String) = 
    if(column.startsWith("first_"))
      firstName.get
    else if(column.startsWith("last_"))
      lastName.get
    else null
  
  def targetSQLType = Types.VARCHAR
  
  def defaultValue = ""
  
  override def writePermission_? = true
  override def readPermission_? = true
  
  protected def i_was_! = wholeGet
  
  def asJsonValue: Box[JsonAST.JValue] = Full(is match {
    case null => JsonAST.JNull
    case str => JsonAST.JString(str)
  })

  override protected[mapper] def doneWithSave(){}

  protected def i_obscure_!(in : String) : String = ""

  override def _toForm: Box[Elem] = Empty
  override def toForm: Box[Elem] = Empty

  override def setFromAny(in: Any): String = {
    in match {
      case JsonAST.JNull => this.set(null) 
      case seq: Seq[_] if !seq.isEmpty => seq.map(setFromAny).apply(0)
      case (s: String) :: _ => this.set(s)
      case s :: _ => this.setFromAny(s)
      case JsonAST.JString(v) => this.set(v)
      case null => this.set(null)
      case s: String => this.set(s)
      case Some(s: String) => this.set(s)
      case Full(s: String) => this.set(s)
      case None | Empty | Failure(_, _, _) => this.set(null)
      case o => this.set(o.toString)
    }
  }
  
  def apply(ov: Box[String]): T = {
    ov match {
      case Full(s) => this.set(s)
      case _ => this.set(null)
    }
    fieldOwner
  }

  def asJsExp: JsExp = JE.Str(is)

  override def apply(ov: String): T = apply(Full(ov))

  def real_convertToJDBCFriendly(value: String): Object = value

  private def wholeSet(in: String){
    real_i_set_!(in)
  }

  def buildSetActualValue(accessor: Method, inst: AnyRef, columnName: String): (T, AnyRef) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedSplitName[T] => f.wholeSet(if (v eq null) null else v.toString)})
  
  def buildSetLongValue(accessor: Method, columnName: String): (T, Long, Boolean) => Unit =
  (inst, v, isNull) => doField(inst, accessor, {case f: MappedSplitName[T] => f.wholeSet(if (isNull) null else v.toString)})
  
  def buildSetStringValue(accessor: Method, columnName: String): (T, String) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedSplitName[T] => f.wholeSet(if (v eq null) null else v)})
  
  def buildSetDateValue(accessor: Method, columnName: String): (T, Date) => Unit =
  (inst, v) => doField(inst, accessor, {case f: MappedSplitName[T] => f.wholeSet(if (v eq null) null else v.toString)})
  
  def buildSetBooleanValue(accessor: Method, columnName: String): (T, Boolean, Boolean) => Unit =
  (inst, v, isNull) => doField(inst, accessor, {case f: MappedSplitName[T] => f.wholeSet(if (isNull) null else v.toString)})

  def fieldCreatorString(dbType: DriverType, colName: String): String = colName+" "+dbType.varcharColumnType(maxLen) + notNullAppender()

}