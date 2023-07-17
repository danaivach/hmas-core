package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;

public class ResourceProfile extends AbstractHostable {
  private final ProfiledResource resource;

  protected ResourceProfile(AbstractBuilder builder) {
    super(CORE.TERM.RESOURCE_PROFILE, builder);
    this.resource = builder.resource;
  }

  public AbstractResource getResource() {
    return (AbstractResource) this.resource;
  }

  public static class Builder extends AbstractBuilder<Builder, ResourceProfile> {

    public Builder(ProfiledResource resource) {
      super(resource);
    }

    public ResourceProfile build() {
      return new ResourceProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ResourceProfile>
          extends AbstractHostable.AbstractBuilder<S, T> {

    private final ProfiledResource resource;

    public AbstractBuilder(ProfiledResource resource) {
      this.resource = resource;
    }

    public abstract T build();
  }
}
