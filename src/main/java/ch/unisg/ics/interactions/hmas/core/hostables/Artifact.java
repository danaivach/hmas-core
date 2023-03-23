package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;

public class Artifact extends AbstractProfiledResource {

  protected Artifact(AbstractBuilder builder) {
    super(CORE.TERM.ARTIFACT, builder);
  }

  protected Artifact(HMAS type, AbstractBuilder builder) {
    super(type, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, Artifact> {
    public static final HMAS TYPE = CORE.TERM.ARTIFACT;

    public Artifact build() {
      return new Artifact(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Artifact>
          extends AbstractProfiledResource.AbstractBuilder<S, T> {
    public abstract T build();
  }
}
