package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;

public class BaseResourceProfile extends AbstractHostable {
  private final ProfiledResource resource;

  protected BaseResourceProfile(AbstractBuilder builder) {
    super(CORE.TERM.RESOURCE_PROFILE, builder);
    this.resource = builder.resource;
  }

  public ProfiledResource getResource() {
    return this.resource;
  }

  public static class Builder extends AbstractBuilder<Builder, BaseResourceProfile> {

    public Builder(ProfiledResource resource) {
      super(resource);
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public BaseResourceProfile build() {
      return new BaseResourceProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends BaseResourceProfile>
          extends AbstractHostable.AbstractBuilder<S, T> {

    private final ProfiledResource resource;

    public AbstractBuilder(ProfiledResource resource) {
      this.resource = resource;
    }

    public abstract T build();
  }
}
