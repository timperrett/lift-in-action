package sample.snippet

trait SetupAndTearDown {
  def construcEnvironment() = 
    new bootstrap.liftweb.Boot().boot
  def tearDownEnvironment() = 
    println("Tearing down the environment!")
}

class AnotherExample extends Specification with SetupAndTearDown {
  construcEnvironment().beforeSpec
  "An Option" should {
    "Be None if supplied a null value" in {
      Option(null) must_== None
    }
  }
  tearDownEnvironment().afterSpec
}

// object ExampleOneSpec extends WebSpec {
//   "AgentDetails" should {
//     setSequential()
//     val testReq =
//           new MockHttpServletRequest(
//             "http://127.0.0.1:8080/agent-details/2.4.0.1090", ""
//           )
//   }
// }
// 
// 
// class AnotherExample extends Specification with SetupAndTearDown {
//   construcEnvironment().beforeSpec
//   "An Option" should {
//     setSequential()
//     val testReq = new MockHttpServletRequest("http://127.0.0.1:8080/", "")
//     
//     val testSession = MockWeb.testS(testUrl) {
//       S.session
//     }
//     
//     "display service manager test results, all FAIL" withSFor(testReq) in {
//     }
//     
//   }
//   tearDownEnvironment().afterSpec
// }