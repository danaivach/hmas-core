package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * A resource that can be hosted on a MAS platform. An agent, an artefact, other
 * MAS platforms, workspaces, resource profiles, signifiers, organizations, can
 * be hosted on a MAS platform.
 */
public abstract class AbstractHostable extends AbstractResource {

  /**
   * The Hypermedia MAS Platform on which the hostable resource is hosted.
   */
  private final transient Set<HypermediaMASPlatform> platforms;

  /**
   * Construct a hostable resource.
   *
   * @param builder the builder of hostables
   * @return the hostable resource
   */
  protected AbstractHostable(final HMAS type, final AbstractBuilder builder) {
    super(type, builder);
    this.platforms = ImmutableSet.copyOf(builder.platforms);
  }

  protected AbstractHostable(final AbstractBuilder builder) {
    this(CORE.TERM.HOSTABLE, builder);
  }

  public Set<HypermediaMASPlatform> getHMASPlatforms() {
    return this.platforms;
  }

  public static abstract class AbstractBuilder<S extends AbstractBuilder, T extends AbstractHostable>
          extends AbstractResource.AbstractBuilder<S, T> {

    private final transient Set<HypermediaMASPlatform> platforms;

    protected AbstractBuilder() {
      this(CORE.TERM.HOSTABLE);
    }

    protected AbstractBuilder(HMAS type) {
      super(type);
      this.platforms = new HashSet<>();
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
    public abstract T build();
  }
}
