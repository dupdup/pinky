package org.pinky.representation

import java.io.{File, OutputStream, OutputStreamWriter}
import com.google.inject.Inject
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.support.TemplateFinder
import com.google.inject.name.Named

/**
 * HTML representation which uses Scalate to do its heavy lifting.
 *
 * @param path template location coming from servlet context
 * @param defaultExtension
 * @author max@bumnetworks.com
 */
class HtmlRepresentationScalate @Inject()(@Named("template.root") path: String, @Named("scalatra.template.engine")  defaultExtension: String) extends Representation {
  val roots: List[String] = (path + File.separator +  "template") :: Nil

  protected def engine0 =
    Some(new TemplateEngine).map {
      e =>
        e.templateDirectories = roots
      e.allowReload = true
      e.allowCaching = false
      e
    }.get

  lazy val engine = engine0
  private lazy val finder = new TemplateFinder(engine)

  def write(data: Map[String, AnyRef], out: OutputStream): Unit = {
    Some(new OutputStreamWriter(out)).map {
      writer =>
        val path = data.get("template").map(_.asInstanceOf[String]).map {
          p => if (p.contains(".")) p else "%s%s".format(p, defaultExtension)
        }.getOrElse(throw new IllegalArgumentException("no 'template' key found"))
      writer.write(engine.layout(finder.findTemplate(path).getOrElse(throw new Exception("failed to find template %s".format(path))), data))
      println("about to flush...")
      writer.flush
    }
  }
}
