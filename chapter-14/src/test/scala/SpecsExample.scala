package sample.test

import org.specs._

class SpecsExample extends Specification {
  "hello world" should {
    "have 11 characters" in {
      "hello world".size must_== 11
    }
    "match 'h.* w.*'" in {
      "hello world" must be matching("h.* w.*")
    }
  }
}

class AnotherExample extends Specification with SetupAndTearDown {
  setup().beforeSpec
  "An Option" should {
    "Be None if supplied a null value" in {
      Option(null) must_== None
    }
  }
  destroy().afterSpec
}

import org.scalacheck.Prop._

class ScalaCheckExample extends Specification with ScalaCheck {
  "Strings" should {
    "Start with" in { 
      forAll {
        (a: String, b: String) => (a + b).startsWith(a)
      } must pass
    }
  }
}