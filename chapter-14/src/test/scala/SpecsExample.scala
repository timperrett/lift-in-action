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
