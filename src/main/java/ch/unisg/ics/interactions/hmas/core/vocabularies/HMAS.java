package ch.unisg.ics.interactions.hmas.core.vocabularies;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public enum HMAS {
  PREFIX("https://purl.org/hmas/core#"),

  /* Classes */
  HOSTABLE(PREFIX +"Hostable"),
  AGENT(PREFIX + "Agent"),
  ARTIFACT(PREFIX + "Artifact"),
  WORKSPACE(PREFIX + "Workspace"),
  HMAS_PLATFORM(PREFIX + "HypermediaMASPlatform"),
  RESOURCE_PROFILE(PREFIX + "ResourceProfile"),
  SIGNIFIER(PREFIX + "Signifier"),

  /* Object Properties */
  CONTAINS(PREFIX + "contains"),
  IS_CONTAINED_IN(PREFIX + "isContainedIn"),
  HOSTS(PREFIX + "hosts"),
  IS_HOSTED_ON(PREFIX + "isHostedOn"),
  EXPOSES_SIGNIFIER(PREFIX + "exposesSignifier"),
  IS_PROFILE_OF(PREFIX + "isProfileOf"),
  TRANSITIVELY_CONTAINS(PREFIX + "transitivelyContains"),
  IS_TRANSITIVELY_CONTAINED_IN(PREFIX + "isTransitivelyContainedIn");

  private final String type;

  private HMAS(String type) {
    this.type = type;
  }

  public String toString() {
    return this.type;
  }

  public IRI toIRI() {
      return SimpleValueFactory.getInstance().createIRI(this.type);
  }
}
