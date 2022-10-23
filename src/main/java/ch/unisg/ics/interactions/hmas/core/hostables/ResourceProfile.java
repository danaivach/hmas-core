package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class ResourceProfile extends AbstractHostable {
  private final AbstractProfiledResource resource;
  private final Set<BaseSignifier> signifiers;

  protected ResourceProfile(AbstractBuilder builder) {
    super(HMAS.RESOURCE_PROFILE, builder);
    this.resource = builder.resource;
    this.signifiers = ImmutableSet.copyOf(builder.signifiers);
  }

  public AbstractProfiledResource getResource() {
    return this.resource;
  }

  public Set<BaseSignifier> getExposedSignifiers() {
    return this.signifiers;
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
    private final Set<BaseSignifier> signifiers;

    public AbstractBuilder(AbstractProfiledResource resource) {
      this.resource = resource;
      this.signifiers = new HashSet<>();
    }

    public S exposeSignifier(BaseSignifier signifier) {
      this.signifiers.add(signifier);
      return (S) this;
    }

    public S exposeSignifiers(Set<BaseSignifier> signifiers) {
      this.signifiers.addAll(signifiers);
      return (S) this;
    }

    public abstract T build();
  }
}
