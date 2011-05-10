package org.pinky.core

import javax.servlet.http.{Cookie, HttpServletResponse, HttpServletRequest}
import java.util.Enumeration


trait ServletUtils {

  implicit def enum2Iterator[A](e: Enumeration[A]) = new Iterator[A] {
    def next = e.nextElement

    def hasNext = e.hasMoreElements
  }

  implicit def requestToRich(r: HttpServletRequest) = new RichRequest(r)

  implicit def responseToRich(r: HttpServletResponse) = new RichResponse(r)

  class RichRequest(request: HttpServletRequest) {

    def cookie(name: String): Option[String] = {
      val cookies = request.getCookies
      for (i <- 0 until cookies.length) {
        val cookie = cookies(i)
        if (name == cookie.getName) Some(cookie.getValue())
      }
      None
    }

    def headers: List[_]  = request.getHeaderNames.toList map (x=>x.toString) map ( x => Map(x-> headers(x)))

    def headers(name: String): List[_] = request.getHeaders(name).toList

    def path = request.getContextPath

    def uri = request.getRequestURI

    def url = request.getRequestURL

    def ip = request.getRemoteAddr

    def host = request.getRemoteHost

    def port = request.getRemotePort

    def header(name: String): Option[String] = {
      val result = request.getHeader(name)
      if (result == null) None else Some(result)
    }

    def attribute(name: String): Option[AnyRef] = {
      val result = request.getAttribute(name)
      if (result == null) None else Some(result)
    }


    def readBuffer: String = { 
      val sb = new StringBuilder
      val reader = request.getReader
      var line = reader.readLine
      while (line != null) {
        sb.append(line + "\n")
        line = reader.readLine
      }   
      sb.toString
    } 
  
    def parameter(name: String): Option[String] = {
      val result = request.getParameter(name)
      if (result == null) None else Some(result)
    }
    
    def parameters: collection.immutable.Map[String, Either[Option[String], List[String]]] = { 
      import collection.JavaConverters._
      request.getParameterMap.asInstanceOf[java.util.Map[String, Array[String]]].asScala.map(x =>
      if (x._2 == null || x._2.size == 1)
          if (x._2 == null || x._2(0) == "") Map(x._1 -> Left(None)) else Map(x._1 -> Left(Some(x._2(0))))
        else
          Map(x._1 -> Right(x._2.toList))
      ).foldLeft(Map[String, Either[Option[String], List[String]]]())(_ ++ _)
    }
  }

  class RichResponse(response: HttpServletResponse) {

    def addCookie(data: Tuple2[String, String], maxAge: Int = -1) {
      val c = new Cookie(data._1, data._2);
      c.setMaxAge(maxAge)
      response.addCookie(c)
    }

  }

}
