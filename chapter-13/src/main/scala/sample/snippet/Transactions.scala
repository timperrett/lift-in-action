package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.transaction.TransactionContext
import sample.model.{Account,Model}

class Transactions {
  def sample = {
    val account1 = Model.createNamedQuery[Account]("findAccountById").setParameter("id", 1).getSingleResult
    // val account2 = Model.createNamedQuery[Account]("findAccountById").setParameter("id", 2).getSingleResult

    // TransactionContext.withTxRequired {
    //   account1.withdraw(10L)
    //   account2.deposit(10L)
    // }
    
    <p>Sample</p>
  }
}