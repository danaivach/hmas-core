package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;

public class ResourceProfile extends AbstractHostable {
  private final AbstractProfiledResource resource;

  protected ResourceProfile(AbstractBuilder builder) {
    super(CORE.RESOURCE_PROFILE, builder);
    this.resource = builder.resource;
  }

  public AbstractProfiledResource getResource() {
    return this.resource;
  }

  public static class Builder extends AbstractBuilder<Builder, ResourceProfile> {

    public Builder(AbstractProfiledResource resource) {
      super(resource);
    }

    public ResourceProfile build() {
      return new ResourceProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ResourceProfile>
    extends AbstractHostable.AbstractBuilder<S, T> {

    private final AbstractProfiledResource resource;

    public AbstractBuilder(AbstractProfiledResource resource) {
      this.resource = resource;
    }

    public abstract T build();
  }
}
