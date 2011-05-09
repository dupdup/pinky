package org.pinky.guice

import javax.servlet.http.HttpServlet
import org.pinky.util.AsClass._


abstract class Single[T <: HttpServlet](val path: String="/*")(implicit m:Manifest[T]) extends ServletModuleBase{
  override def configureServlets {
     _serve(path).`with`((m.erasure.asInstanceOf[Class[T]]))   
  }
}
trait  Cake  extends Single[HttpServlet] with CakeBinder {
  val bindServlet: HttpServlet

  override def configureServlets {
    bindServlet(bindServlet).toUrl(path)
  }

}


