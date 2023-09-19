package ch.unisg.ics.interactions.hmas.core.io;

import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.Optional;

public interface GraphWriter {

  String write();

  String write(RDFFormat format, Optional<String> baseURI);

  GraphWriter setNamespace(String prefix, String namespace);
}
