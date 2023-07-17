package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class HypermediaMASPlatform extends AbstractResource implements ProfiledResource {

  private final Set<AbstractHostable> hostedResources;


  protected HypermediaMASPlatform(AbstractBuilder builder) {
    this(CORE.TERM.HMAS_PLATFORM, builder);
  }

  protected HypermediaMASPlatform(HMAS type, AbstractBuilder builder) {
    super(type, builder);
    this.hostedResources = ImmutableSet.copyOf(builder.hostedResources);
  }

  public Set<AbstractHostable> getHostedResources() {
    return this.hostedResources;
  }

  public static class Builder extends AbstractBuilder<Builder, HypermediaMASPlatform> {

    public HypermediaMASPlatform build() {
      return new HypermediaMASPlatform(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends HypermediaMASPlatform>
          extends AbstractResource.AbstractBuilder<S, T> {

    protected final Set<AbstractHostable> hostedResources;

    protected AbstractBuilder() {
      this(CORE.TERM.HMAS_PLATFORM);
    }

    protected AbstractBuilder(HMAS type) {
      super(type);
      this.hostedResources = new HashSet<>();
    }

    public S addHostedResource(final AbstractHostable hostable) {
      this.hostedResources.add(hostable);
      return (S) this;
    }

    public S addHostedResources(final Set<AbstractHostable> hostables) {
      this.hostedResources.addAll(hostables);
      return (S) this;
    }

    public abstract T build();
  }
}
