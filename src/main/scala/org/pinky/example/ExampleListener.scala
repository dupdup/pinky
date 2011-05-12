package org.pinky.example


import servlets._
import org.pinky.comet.CometServlet
import org.eclipse.jetty.continuation.ContinuationFilter
import com.google.inject.{Scopes, AbstractModule}
import org.pinky.guice.{Module, Single, Cake, ServletModule, PinkyServletContextListener, RepresentationModule}
import org.pinky.actor.ActorClient

/**
 * Listener example which demonstrates how to configure guice managed filters, servlets and other components the "pinky way"
 *
 * @author peter hausel gmail com (Peter Hausel)
 *
 */
class ExampleListener extends PinkyServletContextListener
{
  modules(
    new RepresentationModule(),
    new Module{
      def configure {
        bind[ActorClient].to[PingPongClient] 
        bind[ContinuationFilter].in(Scopes.SINGLETON)
      }   
    },
    new Single("/scalatra/*") with Cake{
      val bind = new MyScalatraApp
    },
    new ServletModule {
      override def configureServlets{
        bindFilter[ExampleFilter].toUrl("/hello/*")
        bindFilter[ContinuationFilter].toUrl("/comet*") 
        bindServlet[ExampleRssServlet].toUrl("*.rss") 
      }  
    },
    new ServletModule with CakeExampleContainer with ExampleServletCakeContainer {
      val example = new Eater 
      override def configureServlets{
        val example = new Eater
        bindServlet(new ExampleServletCake).toUrl("/hello/*")
      }
    }


    )

}


