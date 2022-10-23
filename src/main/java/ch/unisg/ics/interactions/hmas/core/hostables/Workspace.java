package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class Workspace extends Artifact {

  private final Set<AbstractHostable> containedResources;

  protected Workspace(AbstractBuilder builder) {
    super(HMAS.WORKSPACE, builder);
    this.containedResources = ImmutableSet.copyOf(builder.containedResources);
  }

  public Set<AbstractHostable> getContainedResources() {
    return this.containedResources;
  }

  public static class Builder extends AbstractBuilder<Builder, Workspace> {

    public Workspace build() {
      return new Workspace(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Workspace>
    extends Artifact.AbstractBuilder<S, T> {

    private final Set<AbstractHostable> containedResources;

    public AbstractBuilder() {
      this.containedResources = new HashSet<>();
    }

    public S addContainedResource(AbstractHostable hostable) {
      this.containedResources.add(hostable);
      return (S) this;
    }

    public S addContainedResources(Set<AbstractHostable> hostables) {
      this.containedResources.addAll(hostables);
      return (S) this;
    }

    public abstract T build();
  }
}
