package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import org.eclipse.rdf4j.model.IRI;

import java.util.Optional;
import java.util.Set;

public interface Resource {

  Set<String> getSemanticTypes();

  String getTypeAsString();

  IRI getTypeAsIRI();

  HMAS getType();

  Optional<IRI> getIRI();

  Optional<String> getIRIAsString();

}
