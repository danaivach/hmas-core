package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.AbstractResource;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Set;

public abstract class AbstractGraphReader {
  protected Model model;

  protected AbstractResource readResource(AbstractResource.AbstractBuilder<?, ?> builder, Resource node) {
    if (node.isIRI()) {
      builder.setIRI(SimpleValueFactory.getInstance().createIRI(node.stringValue()));
    }

    Set<IRI> semanticTypes = Models.objectIRIs(model.filter(node, RDF.TYPE, null));
    for (IRI type : semanticTypes) {
      if (!builder.TYPE.toString().equals(type.stringValue())) {
        builder.addSemanticType(type.stringValue());
      }
    }

    Model resourceModel = model.filter(node, null, null);

    for (Statement statement : resourceModel) {
      Value object = statement.getObject();
      // Add the statement to filteredModel only if the object is not a BNode
      if (!(object instanceof BNode)) {
        if (node.isBNode()) {
          builder.addTriple(statement.getPredicate(), statement.getObject());
        } else {
          builder.addTriple(statement.getSubject(), statement.getPredicate(), statement.getObject());
        }
      }
    }

    return builder.build();
  }

}
