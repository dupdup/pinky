package org.pinky.core

import com.google.inject.servlet.ServletModule
import com.google.inject.Injector
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import org.pinky.guice.PinkyServletContextListener

/**
 * Created by IntelliJ IDEA.
 * User: phausel
 * Date: Jan 21, 2009
 * Time: 2:09:37 PM
 * To change this template use File | Settings | File Templates.
 */


class PinkyServletContextListenerTest extends Spec with ShouldMatchers {
  describe("A Servlet Context Listener") {


    it("should_fail_pass") {
      val f = new PinkyServletContextListener() {
        def getInjectorPublic(): Injector = {
          super.getInjector
        }
      }
      f.modules (new ServletModule() {})
      val i = f.getInjectorPublic
      i.getClass.getName should equal("com.google.inject.internal.InjectorImpl")
    }
  }
}

