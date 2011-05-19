package org.pinky.core

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

object RequestMethods extends Enumeration {
  type RequestMethods = Value
  val GET = Value("GET")
  val POST = Value("POST")
  val DELETE = Value("DELETE")
  val PUT = Value("PUT")
}


/**
* Created by IntelliJ IDEA.
* User: phausel
* Date: 5/19/11
* Time: 9:16 AM
* To change this template use File | Settings | File Templates.
*/

trait PinkyServlet extends HttpServlet with ServletUtils {

  protected val dispatch: ServletDispatch

  def makeCall(method: String, request: HttpServletRequest, response: HttpServletResponse) {
    try {
      dispatch.callSuppliedBlock(request, response, handlers(method).asInstanceOf[Function2[HttpServletRequest, HttpServletResponse, Map[String, AnyRef]]])
    } catch {
      case ex: NoSuchElementException => throw new RuntimeException("could not find a handler for this request method, you'll need to implement a call to " + request.getMethod)
      case ex: NullPointerException => throw new RuntimeException("guice cound not inject a ServletDispatch, was a class with this type registered?")
    }
  }

  override def service(request: HttpServletRequest, response: HttpServletResponse) = {
    makeCall(request.getMethod, request, response)
  }

  private[core] val handlers = collection.mutable.Map[String, Function2[HttpServletRequest, HttpServletResponse, AnyRef]]()

  def convert(params: Map[String, Array[String]]): Map[String, AnyRef] = {
    val map = collection.mutable.Map[String, AnyRef]()
    for ((key, value) <- params) if (value.size == 1) map += ("key" -> value(0)) else map += ("key" -> value)
    return map.toMap
  }


}



