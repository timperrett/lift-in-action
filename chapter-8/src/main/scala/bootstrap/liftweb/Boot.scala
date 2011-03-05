package bootstrap.liftweb

import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers.AsInt
import net.liftweb.http.{LiftRules,RewriteRequest,RewriteResponse,ParsePath,Req,GetRequest}
import sample.lib.{BasicDispatchUsage,SecondDispatchUsage,
  BookshopHttpServiceBasic,BookshopHttpServiceAdvanced}

class Boot {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    // listing 8.1
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("category" :: cid :: "product" :: pid :: Nil,"",true,_),_,_) =>
           RewriteResponse("product" :: Nil, Map("pid" -> pid))
    }
    
    // section 8.2.2
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("account" :: AsInt(aid) :: Nil,"",true,false),_,_) => {
           RewriteResponse("account" :: Nil)
      }
    }
    
    // listing 8.2
    LiftRules.dispatch.append(BasicDispatchUsage)
    
    // listing 8.3
    LiftRules.dispatch.append(SecondDispatchUsage)
    
    LiftRules.urlDecorate.prepend {
      case url => if(url.contains("?")) url + "&srv_id=001" else "?srv_id=001"
    }
    
    LiftRules.liftRequest.append {
      case Req("nolift" :: Nil,"xml",_) => false
    }
    
    import net.liftweb.util.Helpers._
    import java.util.Calendar
    
    val onMondays: PartialFunction[Req, Unit] = { 
      case _ if day(now) == Calendar.MONDAY => 
    }
    
    // section 8.3.2
    LiftRules.dispatch.append(onMondays guard BookshopHttpServiceBasic)
    
    // section 8.3.3
    LiftRules.dispatch.append(BookshopHttpServiceAdvanced)
  }
}
