package ch.unisg.ics.interactions.hmas.core.hostables;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class HostableTest {
  @Test
  public void testHostableSetIRI() {
    Agent.Builder builder = new Agent.Builder();

    builder.setIRI(SimpleValueFactory.getInstance().createIRI("urn:agent"));

    Agent agent = builder.build();
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
    assertEquals("urn:agent", agent.getIRIAsString().get());
  }

  @Test
  public void testHostableSetIRIAsString() {
    Agent.Builder builder = new Agent.Builder();

    builder.setIRIAsString("urn:agent");

    Agent agent = builder.build();
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
    assertEquals("urn:agent", agent.getIRIAsString().get());
  }

  @Test
  public void testHostableInvalidIRI() {
    Agent.Builder builder = new Agent.Builder();

    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
      builder.setIRIAsString("invalid-iri");
    });

    String expectedMessage = "The IRI of a Hostable must be valid.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  @Test
  public void testHostableAddHMASPlatforms() {
    Agent.Builder builder = new Agent.Builder();

    HypermediaMASPlatform[] platforms = {
      new HypermediaMASPlatform.Builder().build(),
      new HypermediaMASPlatform.Builder().build()
    };

    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    Agent agent = builder.build();
    assertFalse(agent.getIRI().isPresent());
    assertFalse(agent.getIRIAsString().isPresent());

    assertEquals(3, agent.getHMASPlatforms().size());
    assertThrows(UnsupportedOperationException.class, () -> {
      agent.getHMASPlatforms().add(null);
    });
  }
}
