package org.pinky.guice

import scala.reflect.Manifest
import javax.servlet.http.HttpServlet
import javax.servlet.Filter
import com.google.inject.Key
import org.pinky.util.AsClass._



trait CakeBinder {
self: ServletModuleBase=>

  def bindServlet[T](servlet: HttpServlet): Builder[T] = {
    if (binderAccess == null) throw new RuntimeException("you can call this method only from configureServlets")
    binderAccess.bind(servlet.asClass).toInstance(servlet)
    new CakeServletBuilder(this, servlet)
  }
  def bindFilter[T](filter: Filter): Builder[T] = {
    if (binderAccess == null) throw new RuntimeException("you can call this method only from configureServlets")
    binderAccess.bind(filter.asClass).toInstance(filter)
    new CakeFilterBuilder(this, filter)
  }
}

  

class CakeFilterBuilder[T](module: ServletModuleBase, filter: Filter) extends Builder[T] {
  def toUrl(pattern: String, patterns: String*) {
     module._filter(pattern, patterns: _*).through(filter.asClass)
  }
  def toRegexUrl(pattern: String, patterns: String*){
     module._filterRegex(pattern, patterns: _*).through(filter.asClass)
  }

}
class CakeServletBuilder[T](module: ServletModuleBase, servlet: HttpServlet) extends Builder[T] {
  def toUrl(pattern: String, patterns: String*) {
     module._serve(pattern, patterns: _*).`with`(servlet.asClass)
  }
  def toRegexUrl(pattern: String, patterns: String*){
     module._serveRegex(pattern, patterns: _*).`with`(servlet.asClass)
  }
}

