package org.pinky.guice
import org.scalatra.test.scalatest._
import org.scalatest._
import org.scalatest.matchers._
import org.pinky.example.servlets._

class MockScalatra extends MyScalatraApp with Dependency {
     def out(s: String): xml.Elem = <p>HelloMock, {s}</p>
}

class SingleTest extends Spec with ScalatraSuite with ShouldMatchers {
  // `MyScalatraServlet` is your app which extends ScalatraServlet

  addServlet(classOf[MockScalatra], "/scalatra/*")
 
 describe("a scalatra app") {
  it ("should render a page using") {
     get("/scalatra/hello/peter") {
      //status should equal (200)
      println(body)
      body should include ("HelloMock, peter")
     }
   }
 }
}
// vim: set ts=4 sw=4 et:
