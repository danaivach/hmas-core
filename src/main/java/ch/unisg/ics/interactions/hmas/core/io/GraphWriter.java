package ch.unisg.ics.interactions.hmas.core.io;

import org.eclipse.rdf4j.rio.RDFFormat;

public interface GraphWriter {

  String write();

  String write(RDFFormat format);

  GraphWriter setNamespace(String prefix, String namespace);
}
