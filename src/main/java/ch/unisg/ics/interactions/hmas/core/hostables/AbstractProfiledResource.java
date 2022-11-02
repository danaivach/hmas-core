package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;

public abstract class AbstractProfiledResource extends AbstractHostable {

  protected AbstractProfiledResource(AbstractBuilder builder) {
    super(builder);
  }

  protected AbstractProfiledResource(HMAS type, AbstractBuilder builder) {
    super(type, builder);
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends AbstractProfiledResource>
    extends AbstractHostable.AbstractBuilder<S, T> {

    @SuppressWarnings("unchecked")
    public abstract T build();
  }
}
