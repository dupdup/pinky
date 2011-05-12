package org.pinky.guice

import javax.servlet.http.HttpServlet
import org.pinky.util.AsClass._

abstract class Single(val path: String="/*") extends ServletModuleBase{
  type Bind <: HttpServlet
  implicit val cm: ClassManifest[Bind]

  override def configureServlets {
     _serve(path).`with`((cm.erasure.asInstanceOf[Class[Bind]]))   
  }
}

trait Cake extends Single with CakeBinder {
  type Bind = HttpServlet
  override val cm = null

  val bind: HttpServlet

  override def configureServlets {
    bindServlet(bind).toUrl(path)
  }

}


