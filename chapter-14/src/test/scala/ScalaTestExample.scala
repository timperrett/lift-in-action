package sample.test

import org.scalatest.FunSuite

class BasicSuite extends FunSuite {
  val shared = List("a","b","c")

  test("that tail yeilds 'b' and 'c'"){
    assert(shared.tail === List("b","c"))
  }
}

// class BasicSuiteWithFixtures extends FunSuite {
//   private var reader: FileReader = _
//   override def withFixture(test: NoArgTest) {
//     val FileName = "TempFile.txt"
// 
//     // Set up the temp file needed by the test
//     val writer = new FileWriter(FileName)
//     try {
//       writer.write("Hello, test!")
//     }
//     finally {
//       writer.close()
//     }
// 
//     // Create the reader needed by the test
//     reader = new FileReader(FileName)
// 
//     try {
//       test() // Invoke the test function
//     }
//     finally {
//       // Close and delete the temp file
//       reader.close()
//       val file = new File(FileName)
//       file.delete()
//     }
//   }
// 
//   test("reading from the temp file") {
//     var builder = new StringBuilder
//     var c = reader.read()
//     while (c != -1) {
//       builder.append(c.toChar)
//       c = reader.read()
//     }
//     assert(builder.toString === "Hello, test!")
//   }
// 
//   test("first char of the temp file") {
//     assert(reader.read() === 'H')
//   }
// 
//   test("without a fixture") {
//     assert(1 + 1 === 2)
//   }
// }