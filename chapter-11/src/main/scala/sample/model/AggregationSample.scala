package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._
import net.liftweb.util.FatLazy

object AggregationSample extends AggregationSample with LongKeyedMetaMapper[AggregationSample]

class AggregationSample extends LongKeyedMapper[AggregationSample] with IdPK {
  def getSingleton = AggregationSample
  
  object name extends MappedString(this, 100){
    private var firstName = FatLazy(defaultValue)
    private var lastName = FatLazy(defaultValue)
    private def wholeGet = "%s %s".format(firstName.get, lastName.get)
    
    override def dbColumnCount = 2
    
    override def dbColumnNames(name: String) = 
      List("first","last").map(_ + "_" + name.toLowerCase)
    
    override def real_i_set_!(value : String): String = {
      value.split(" ") match {
        case Array(first, last) => firstName.set(first); lastName.set(last)
        case _ => ""
      }
      this.dirty_?(true)
      wholeGet
    }
    
    override def i_is_! = wholeGet
    
    override lazy val dbSelectString = dbColumnNames(name).map(cn => 
      fieldOwner.getSingleton._dbTableNameLC + "." + cn).mkString(", ")
    
    override def jdbcFriendly(column: String) = 
      if(column.startsWith("first_"))
        firstName.get
      else if(column.startsWith("last_"))
        lastName.get
      else null
  }
}
