package sample.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, OptionalEmailField, StringField}

class Example extends Record[Example] {
  def meta = Example
  val name = new StringField(this, ""){
    override def validations = 
      valMinLen(5, "Must be more than 5 characters") _ :: 
      super.validations
  }
  val funds = new LongField(this)
  val email = new OptionalEmailField(this, 100)
}

object Example extends Example with MetaRecord[Example]

// object ExampleValidations {
//   def notLessThanZero(field : FieldIdentifier)(amount : Long) = 
//     if(amount < 0)
//       throw new Exception("Cannot be less that zero. You need ze monies!")
//     else List[FieldError]()
// }
