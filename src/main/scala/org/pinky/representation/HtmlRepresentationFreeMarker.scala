package org.pinky.representation

import com.google.inject.Inject
import freemarker.template.{DefaultObjectWrapper, Configuration}
import java.io.{File, BufferedWriter, OutputStreamWriter, OutputStream}
import com.google.inject.name.Named

/**
* Provides FreeMarker rendering
*
* @param ctx the ServletContext is needed for the webapp path
* @author peter hausel gmail com (Peter Hausel)
*/
class HtmlRepresentationFreeMarker @Inject()(@Named("root") path: String) extends Representation {
  protected def spawnConfiguration = {
    val cfg = new Configuration
    cfg.setObjectWrapper(new DefaultObjectWrapper())
    cfg.setDirectoryForTemplateLoading( new File(path + File.separator.toString +  "template") )
    cfg
  }

  lazy val cfg = spawnConfiguration

  /**
   * @param data data coming from the user
   * @param out outputstream used to print out the response
   */
  def write(data: Map[String, AnyRef], out: OutputStream) = {
    try {
      val templateFile = if (data("template").asInstanceOf[String].endsWith("ftl"))
        data("template").asInstanceOf[String]
      else
        data("template").asInstanceOf[String] + ".ftl"
      val template = cfg.getTemplate(templateFile)
      val tmplWriter = new BufferedWriter(new OutputStreamWriter(out));
      template.process(data, tmplWriter)
      // Process the template
      tmplWriter.flush();
    } catch {case e: Exception => {e.printStackTrace; throw e}}

  }

}
