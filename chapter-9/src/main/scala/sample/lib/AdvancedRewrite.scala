package sample.lib

// import net.liftweb.common.{Box,Full,Empty}
//import net.liftweb.http.{ParsePath,RequestVar}

//object CurrentAccountId extends RequestVar[Box[Int]](Empty)
// case class Account(name: String, value: Long)
// 
// object AccountExtractor {
//   // the extractor
//   def unapply(pp: ParsePath): Option[Account] = pp match {
//     case ParsePath(List("account", acc),"",true,false) => {
//       val result = CurrentAccount.is or Accounts.find(acc)
//       CurrentAccount(result)
//       result
//     }
//     case _ => None
//   }
// }
// 
// object Accounts {
//   val list = Account("current", 1000) :: Account("bussiness", 6789) :: Nil
//   def find(name: String): Box[Account] = {
//     println("finding...("+Box(Accounts.list.find(_.name == name))+")")
//     Box(Accounts.list.find(_.name == name))
//   }
// }
// 
