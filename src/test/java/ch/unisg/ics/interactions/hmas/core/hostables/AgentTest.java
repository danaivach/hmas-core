package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class AgentTest {

  @Test
  public void testAgent() {
    Agent.Builder builder = new Agent.Builder();

    HypermediaMASPlatform[] platforms = {
      new HypermediaMASPlatform.Builder().build(),
      new HypermediaMASPlatform.Builder().build()
    };

    builder.setIRIAsString("urn:agent");
    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    Agent agent = builder.build();
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
    assertEquals("urn:agent", agent.getIRIAsString().get());

    assertEquals(CORE.AGENT, agent.getType());
    assertEquals(CORE.PREFIX + "Agent", agent.getTypeAsString());
    assertEquals(CORE.AGENT.toIRI(), agent.getTypeAsIRI());

    assertEquals(3, agent.getHMASPlatforms().size());
    assertThrows(UnsupportedOperationException.class, () -> {
      agent.getHMASPlatforms().add(null);
    });
  }

  @Test
  public void testAgentDefault() {
    Agent agent = new Agent.Builder()
      .build();

    assertFalse(agent.getIRI().isPresent());
    assertFalse(agent.getIRIAsString().isPresent());
    assertEquals(CORE.AGENT, agent.getType());
    assertEquals(0, agent.getHMASPlatforms().size());
  }
}
