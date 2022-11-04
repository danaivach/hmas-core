package ch.unisg.ics.interactions.hmas.core.vocabularies;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.base.AbstractNamespace;

public class Vocabulary {

  public static Namespace createNamespace(String prefix, String namespace) {
    return new AbstractNamespace() {

      @Override
      public String getPrefix() {
        return prefix;
      }

      @Override
      public String getName() {
        return namespace;
      }

    };
  }
}
