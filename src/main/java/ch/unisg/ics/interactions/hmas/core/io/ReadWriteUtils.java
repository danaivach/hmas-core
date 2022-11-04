package ch.unisg.ics.interactions.hmas.core.io;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ReadWriteUtils {

  private final static Logger LOGGER = Logger.getLogger(ReadWriteUtils.class.getCanonicalName());

  private ReadWriteUtils() {
  }

  static Model readModelFromString(RDFFormat format, String description, String baseURI)
          throws RDFParseException, RDFHandlerException, IOException {
    StringReader stringReader = new StringReader(description);

    RDFParser rdfParser = Rio.createParser(format);
    Model model = new LinkedHashModel();
    rdfParser.setRDFHandler(new StatementCollector(model));

    rdfParser.parse(stringReader, baseURI);

    return model;
  }

  static String writeToString(RDFFormat format, Model model) {
    OutputStream out = new ByteArrayOutputStream();

    try {
      Rio.write(model, out, format,
              new WriterConfig().set(BasicWriterSettings.INLINE_BLANK_NODES, true));
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    }

    return out.toString();
  }
}
