package org.pinky.core

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import org.pinky.example.servlets._
import java.util.concurrent.{TimeUnit, CountDownLatch}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.pinky.code.util.LatchSupport._
import org.mockito.Mockito._
import akka.actor.{Actor}
import org.pinky.actor.ActorClient

/**
 * Created by IntelliJ IDEA.
 * User: phausel
 * Date: Jan 18, 2010
 * Time: 7:07:17 PM
 * To change this template use File | Settings | File Templates.
 */


class ActorClientTest extends Spec with ShouldMatchers {


  //prepare mocks
  trait CountDownActors extends Workers {
    this: ActorClient=>
    workers=Array(CountDownActors.pingActor,CountDownActors.pongActor)
  }
  object CountDownActors {
     val pongActor = Actor.actorOf(new PongActor with CountDown)
     val pingActor = Actor.actorOf(new PingActor(pongActor) with CountDown)
  }


  class DispatchMock extends ServletDispatch {
    def callSuppliedBlock(request: HttpServletRequest, response: HttpServletResponse, block: Function2[HttpServletRequest, HttpServletResponse, Map[String, AnyRef]]) = {
      block(request,response)
    }
  }

  describe("an ActorClient") {
  
    it("should run actors sucessfully") {
      latch = new CountDownLatch(2)
      var request = mock(classOf[HttpServletRequest])
      var response = mock(classOf[HttpServletResponse])
      when(request.getParameter("name")).thenReturn("Jonas")
      val servlet = new ExampleServlet(new PingPongClient with CountDownActors) {
        override val dispatch = new DispatchMock
      }
      servlet.makeCall(RequestMethods.GET.toString,request, response)
      val received = latch.await(4, TimeUnit.SECONDS)
      received should be(true)
      CountDownActors.pingActor.stop()
      CountDownActors.pongActor.stop()
    }
    it("should receive only one message if actors coming in wrong order") {
         latch = new CountDownLatch(1)
         val data = Map("name" -> "Jonas")
         val pingActor = Actor.actorOf(new PongActor with CountDown)
         val pongActor = Actor.actorOf(new PingActor(pingActor)  with CountDown)
         val client = new PingPongClient
         client.workers=Array(pingActor,pongActor)
         client.fireStarter(data)
         val received = latch.await(4, TimeUnit.SECONDS)
         received should be(true)
          pingActor.stop()
          pongActor.stop()
       }


  }
}