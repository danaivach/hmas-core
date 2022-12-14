package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;

public class BaseSignifier extends AbstractHostable {

  protected BaseSignifier(AbstractBuilder builder) {
    super(CORE.TERM.SIGNIFIER, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, BaseSignifier> {
    public BaseSignifier build() {
      return new BaseSignifier(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends BaseSignifier>
          extends AbstractHostable.AbstractBuilder<S, T> {
    public abstract T build();
  }
}
