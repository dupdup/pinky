package org.pinky.example.servlets
import org.scalatra.ScalatraServlet

class MyScalatraApp extends ScalatraServlet {
  get("/hello/:name") {
      // Matches "GET /hello/foo" and "GET /hello/bar"
        // params("name") is "foo" or "bar"
              <p>Hello, {params("name")}</p>
  } 
}

