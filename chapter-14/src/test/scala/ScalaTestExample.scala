package sample.test

import org.scalatest.FunSuite

class BasicSuite extends FunSuite {
  val shared = List("a","b","c")

  test("that tail yeilds 'b' and 'c'"){
    assert(shared.tail === List("b","c"))
  }
}
