package sample.test

// class AnotherExample extends Specification with SetupAndTearDown {
//   // construcEnvironment().beforeSpec
//   "An Option" should {
//     "Be None if supplied a null value" in {
//       Option(null) must_== None
//     }
//   }
//   // tearDownEnvironment().afterSpec
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