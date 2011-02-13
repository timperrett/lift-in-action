package example.travel {
package model {
  
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.sitemap.Loc._
  import net.liftweb.mapper._
  import scala.xml.NodeSeq
  
  object Supplier 
      extends Supplier 
      with LongKeyedMetaMapper[Supplier]
      with CRUDify[Long,Supplier]{
    override def dbTableName = "suppliers"
    override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
    override def pageWrapper(body: NodeSeq) = 
      <lift:surround with="admin" at="content">{body}</lift:surround>
    override def calcPrefix = List("admin",_dbTableNameLC)
    override def displayName = "Supplier"
    override def showAllMenuLocParams = LocGroup("admin") :: Nil
    override def createMenuLocParams = LocGroup("admin") :: Nil
    override def viewMenuLocParams = LocGroup("admin") :: Nil
    override def editMenuLocParams = LocGroup("admin") :: Nil
    override def deleteMenuLocParams = LocGroup("admin") :: Nil
  }
  
  class Supplier extends LongKeyedMapper[Supplier] 
      with IdPK with OneToMany[Long, Supplier] with CreatedUpdated {
    def getSingleton = Supplier
    // fields
    object name extends MappedString(this, 150)
    object telephone extends MappedString(this, 30)
    object email extends MappedEmail(this, 200)
    object address extends MappedText(this)
    object opening_hours extends MappedString(this, 255)
    
    // relationships
    object auctions extends MappedOneToMany(Auction, Auction.supplier, 
      OrderBy(Auction.ends_at, Descending)) 
        with Owned[Auction] 
        with Cascade[Auction] 
  }
  
  
}}
