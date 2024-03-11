package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;

public class Artifact extends AbstractHostable implements ProfiledResource {

  protected Artifact(AbstractBuilder builder) {
    super(CORE.TERM.ARTIFACT, builder);
  }

  protected Artifact(HMAS type, AbstractBuilder builder) {
    super(type, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, Artifact> {
    public static final HMAS TYPE = CORE.TERM.ARTIFACT;

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public Artifact build() {
      return new Artifact(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Artifact>
          extends AbstractHostable.AbstractBuilder<S, T> {

    protected AbstractBuilder() {
      super(CORE.TERM.ARTIFACT);
    }

    protected AbstractBuilder(HMAS type) {
      super(type);
    }

    public abstract T build();
  }
}
