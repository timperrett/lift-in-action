package sample.test

import org.specs._
import org.scalacheck.Prop._

trait SpecsExample { _: Specification with ScalaCheck =>  // extends Specification {
  "hello world" should {
    "have 11 characters" in {
      "hello world".size must_== 11
    }
    "match 'h.* w.*'" in {
      "hello world" must be matching("h.* w.*")
    }
  }
  
  // example 2
  "An Option" should {
    "Be None if supplied a null value" in {
      Option(null) must_== None
    }
  }
  // scala check example
  "Strings" should {
    "Start with" in { 
      forAll {
        (a: String, b: String) => (a + b).startsWith(a)
      } must pass
    }
  }
}
