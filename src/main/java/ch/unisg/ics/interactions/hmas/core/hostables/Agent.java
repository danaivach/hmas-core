package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;

public class Agent extends AbstractHostable implements ProfiledResource {

  protected Agent(AbstractBuilder builder) {
    super(CORE.TERM.AGENT, builder);
  }

  public static class Builder extends AbstractBuilder<Builder, Agent> {
    public static final HMAS TYPE = CORE.TERM.AGENT;

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public Agent build() {
      return new Agent(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends Agent>
          extends AbstractHostable.AbstractBuilder<S, T> {
    public abstract T build();
  }
}
