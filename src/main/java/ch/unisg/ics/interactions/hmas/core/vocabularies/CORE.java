package ch.unisg.ics.interactions.hmas.core.vocabularies;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class CORE {

  public static final String NAMESPACE = "https://purl.org/hmas/";

  public static final String PREFIX = "hmas";

  public static final Namespace NS = Vocabulary.createNamespace(PREFIX, NAMESPACE);

  /* Classes */
  public static final IRI HOSTABLE;

  public static final IRI AGENT;

  public static final IRI ARTIFACT;

  public static final IRI WORKSPACE;

  public static final IRI HMAS_PLATFORM;

  public static final IRI RESOURCE_PROFILE;

  public static final IRI SIGNIFIER;

  /* Object Properties */
  public static final IRI CONTAINS;

  public static final IRI IS_CONTAINED_IN;

  public static final IRI HOSTS;

  public static final IRI IS_HOSTED_ON;

  public static final IRI EXPOSES_SIGNIFIER;

  public static final IRI IS_PROFILE_OF;

  public static final IRI TRANSITIVELY_CONTAINS;

  public static final IRI IS_TRANSITIVELY_CONTAINED_IN;

  static {
    SimpleValueFactory rdf = SimpleValueFactory.getInstance();

    HOSTABLE = rdf.createIRI(NAMESPACE + "Hostable");
    AGENT = rdf.createIRI(NAMESPACE + "Agent");
    ARTIFACT = rdf.createIRI(NAMESPACE + "Artifact");
    WORKSPACE = rdf.createIRI(NAMESPACE + "Workspace");
    HMAS_PLATFORM = rdf.createIRI(NAMESPACE + "HypermediaMASPlatform");
    RESOURCE_PROFILE = rdf.createIRI(NAMESPACE + "ResourceProfile");
    SIGNIFIER = rdf.createIRI(NAMESPACE + "Signifier");

    CONTAINS = rdf.createIRI(NAMESPACE + "contains");
    IS_CONTAINED_IN = rdf.createIRI(NAMESPACE + "isContainedIn");
    HOSTS = rdf.createIRI(NAMESPACE + "hosts");
    IS_HOSTED_ON = rdf.createIRI(NAMESPACE + "isHostedOn");
    EXPOSES_SIGNIFIER = rdf.createIRI(NAMESPACE + "exposesSignifier");
    IS_PROFILE_OF = rdf.createIRI(NAMESPACE + "isProfileOf");
    TRANSITIVELY_CONTAINS = rdf.createIRI(NAMESPACE + "transitivelyContains");
    IS_TRANSITIVELY_CONTAINED_IN = rdf.createIRI(NAMESPACE + "isTransitivelyContainedIn");
  }

  CORE() {
  }

  public enum TERM implements HMAS {

    /* Classes */
    HOSTABLE(CORE.HOSTABLE),
    AGENT(CORE.AGENT),
    ARTIFACT(CORE.ARTIFACT),
    WORKSPACE(CORE.WORKSPACE),
    HMAS_PLATFORM(CORE.HMAS_PLATFORM),
    RESOURCE_PROFILE(CORE.RESOURCE_PROFILE),
    SIGNIFIER(CORE.SIGNIFIER),

    /* Object Properties */
    CONTAINS(CORE.CONTAINS),
    IS_CONTAINED_IN(CORE.IS_CONTAINED_IN),
    HOSTS(CORE.HOSTS),
    IS_HOSTED_ON(CORE.IS_HOSTED_ON),
    EXPOSES_SIGNIFIER(CORE.EXPOSES_SIGNIFIER),
    IS_PROFILE_OF(CORE.IS_PROFILE_OF),
    TRANSITIVELY_CONTAINS(CORE.TRANSITIVELY_CONTAINS),
    IS_TRANSITIVELY_CONTAINED_IN(CORE.IS_TRANSITIVELY_CONTAINED_IN);


    private final IRI type;

    TERM(IRI type) {
      this.type = type;
    }

    public String toString() {
      return this.type.toString();
    }

    public IRI toIRI() {
      return this.type;
    }
  }
}
