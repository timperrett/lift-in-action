package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object Account extends Account with LongKeyedMetaMapper[Account]{
  override def beforeSave = List(_.validate)
}

class Account extends LongKeyedMapper[Account] with IdPK {
  def getSingleton = Account
  object balance extends MappedLong(this){
    override def validations = 
      AccountValidations.notLessThanZero(this) _ :: Nil
  }
  def deposit(value: Long) = updateAndSave(value)
  def withdraw(value: Long) = updateAndSave(-value)
  def updateAndSave(value: Long) = balance(balance.is + value).save
}

import net.liftweb.util.{FieldIdentifier,FieldError}

object AccountValidations {
  def notLessThanZero(field : FieldIdentifier)(amount : Long) = 
    if(amount < 0)
      throw new Exception("Cannot be less that zero. You need ze monies!")
    else List[FieldError]()
}

/**** Transaction Sample ****

new bootstrap.liftweb.Boot().boot

import net.liftweb.mapper._; import sample.model._;

val account1 = Account.find(1).open_!
val account2 = Account.find(2).open_!

DB.use(DefaultConnectionIdentifier){ connection => 
  account1.deposit(5)
  account2.withdraw(5)
}



*****************************/