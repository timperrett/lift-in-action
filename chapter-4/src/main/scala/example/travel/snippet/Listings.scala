package example.travel
package snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{S,DispatchSnippet}
import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
import net.liftweb.mapper.view.{MapperPaginatorSnippet}
import example.travel.model.Auction
import example.travel.lib.AuctionHelpers

class Listings extends DispatchSnippet with AuctionHelpers {
  override def dispatch = {
    case "all" => all
    case "top" => top
    case "paginate" => paginator.paginate _
  }
  
  private val paginator = new MapperPaginatorSnippet(Auction){
    override def itemsPerPage = 5
  }
  
  def all = "li *" #> many(paginator.page)
  
  def top =
    ".auction_row *" #> many(Auction.findAll(
      By(Auction.isClosed, false), 
      MaxRows(3), 
      OrderBy(Auction.id, Descending)))
}
