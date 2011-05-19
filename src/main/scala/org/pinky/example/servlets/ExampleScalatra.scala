package org.pinky.example.servlets
import org.scalatra.ScalatraServlet

class MyScalatraApp extends ScalatraServlet {
  this: Dependency=>
  get("/hello/:name") {
      // Matches "GET /hello/foo" and "GET /hello/bar"
        // params("name") is "foo" or "bar"
          out(params("name"))
  } 
}

trait Dependency {
 def out(s: String): xml.Elem
}
trait MyDependency extends Dependency {
  def out(s: String) = <p>Hello, {s}</p>

}

