package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A resource that can be hosted on a MAS platform. An agent, an artefact, other
 * MAS platforms, workspaces, resource profiles, signifiers, organizations, can
 * be hosted on a MAS platform.
 */
public abstract class AbstractHostable {

  /**
   * The Hypermedia MAS Platform on which the hostable resource is hosted.
   */
  private final transient Set<HypermediaMASPlatform> platforms;
  private final Optional<String> IRI;
  private final HMAS type;

  /**
   * Construct a hostable resource.
   *
   * @param builder the builder of hostables
   * @return the hostable resource
   */
  protected AbstractHostable(final HMAS type, final AbstractBuilder builder) {

    this.type = type;
    this.IRI = builder.IRI;
    this.platforms = ImmutableSet.copyOf(builder.platforms);
  }

  protected AbstractHostable(final AbstractBuilder builder) {
    this(HMAS.HOSTABLE, builder);
  }

  public Set<HypermediaMASPlatform> getHMASPlatforms() {
    return this.platforms;
  }

  public String getTypeAsString() {
    return this.type.toString();
  }

  public IRI getTypeAsIRI() {
    return this.type.toIRI();
  }

  public HMAS getType() {
    return this.type;
  }

  public Optional<IRI> getIRI() {
    if (this.IRI.isPresent()) {
      return Optional.of(SimpleValueFactory.getInstance().createIRI(this.IRI.get()));
    }
    return Optional.empty();
  }

  public Optional<String> getIRIAsString() {
    return this.IRI;
  }

  public static abstract class AbstractBuilder<S extends AbstractBuilder, T extends AbstractHostable> {

    private final transient Set<HypermediaMASPlatform> platforms;
    private Optional<String> IRI;

    protected AbstractBuilder() {
      this.platforms = new HashSet<>();
      this.IRI = Optional.empty();
    }

    private static boolean validateIRI(String IRI) {
      try {
        SimpleValueFactory.getInstance().createIRI(IRI);
        return true;
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("The IRI of a Hostable must be valid.");
      }
    }

    @SuppressWarnings("unchecked")
    public S addHMASPlatform(final HypermediaMASPlatform platform) {
      this.platforms.add(platform);
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S addHMASPlatforms(final Set<HypermediaMASPlatform> platforms) {
      this.platforms.addAll(platforms);
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setIRI(final IRI IRI) {
      this.IRI = Optional.of(IRI.toString());
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setIRIAsString(final String IRI) {
      validateIRI(IRI);
      this.IRI = Optional.of(IRI);
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public abstract T build();
  }
}
