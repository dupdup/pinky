package org.pinky.example.servlets


import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.pinky.core.{SimpleDispatch, AutoDispatch, PinkyServlet}

/**
* A regular controller(serlvet) example
*
* @author peter hausel gmail com (Peter Hausel)
*/

trait CakeExampleComponent {
  val example: InnerCake
  trait InnerCake {
    def saySomething: Map[String,String] 
  } 
}

trait CakeExampleContainer extends CakeExampleComponent{
  class Eater extends InnerCake {
    def saySomething = Map("name" -> "peter111" )
  }
}

trait ExampleServletCakeContainer {
  this: CakeExampleComponent =>

  class ExampleServletCake extends PinkyServlet with SimpleDispatch {
    GET {
      (request: HttpServletRequest, response: HttpServletResponse) =>
        ("text/html"->example.saySomething)
    }
  }
}
