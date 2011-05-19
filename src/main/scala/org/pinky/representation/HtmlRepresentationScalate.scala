package org.pinky.representation

import java.io.{File, OutputStream, OutputStreamWriter}
import com.google.inject.Inject
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.support.TemplateFinder
import com.google.inject.name.Named

/**
 * HTML representation which uses Scalate to do its heavy lifting.
 *
 * @param ctx ServletContext instance
 * @author max@bumnetworks.com
 */
class HtmlRepresentationScalate @Inject()(@Named("root") path: String) extends Representation {
  val roots: List[String] = (path + File.separator +  "template") :: Nil
  val defaultExtension: Option[String] = None

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

  def write(data: Map[String, AnyRef], out: OutputStream): Unit =
    Some(new OutputStreamWriter(out)).map {
      writer =>
        val path = data.get("template").map(_.asInstanceOf[String]).map {
          p => if (p.contains(".")) p else "%s%s".format(p, defaultExtension.map(".%s".format(_)).getOrElse(""))
        }.getOrElse(throw new IllegalArgumentException("no 'template' key found"))
      writer.write(engine.layout(finder.findTemplate(path).getOrElse(throw new Exception("failed to find template %s".format(path))), data))
      writer.flush
    }
}
