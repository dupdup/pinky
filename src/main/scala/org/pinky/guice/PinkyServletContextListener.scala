package org.pinky.guice

import com.google.inject.Injector
import com.google.inject.servlet.GuiceServletContextListener
import com.google.inject.{Module=>GModule}
import com.google.inject.Guice
import javax.servlet.ServletContext
import org.pinky.representation.Representation
import org.pinky.actor.ActorClient
import org.pinky.example.servlets.PingPongClient
import com.google.inject.name.Named
import com.google.inject.name.Names._
import com.google.inject.binder.LinkedBindingBuilder._

/**
 * adds varargs support to guice injector creator,
 * also hides guice form the listener
 *
 * @author peter hausel gmail com (Peter Hausel)
 */
abstract class PinkyServletContextListener extends GuiceServletContextListener {

  private var path: String = _

  override def contextInitialized( servletContextEvent: javax.servlet.ServletContextEvent) {
    path = servletContextEvent.getServletContext.getRealPath("/")
    super.contextInitialized(servletContextEvent)
  }

  def modules:List[GModule]


  /**
   * @return Injector
   * creates a guice injector from modules passed in via modules Array, without the array
   * this thing is not functioning
   *
   *
   */
  override protected def getInjector(): Injector = {
    val pathModule = List(new  Module {
      def configure {
        bindConstant.annotatedWith(named("root")).to(path)
      }
    })
    Guice.createInjector( (pathModule ++ modules): _* )
  }
}
