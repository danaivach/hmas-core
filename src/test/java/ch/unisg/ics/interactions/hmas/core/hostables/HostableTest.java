package ch.unisg.ics.interactions.hmas.core.hostables;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

  @Test
  public void testHostableSemanticTypes() {
    Agent.Builder builder = new Agent.Builder();

    List<String> types = Arrays.asList("http:example.org/onto/AgentType1",
            "http:example.org/onto/AgentType2");
    List<String> moreTypes = Arrays.asList("http:example.org/onto/AgentType3",
            "http:example.org/onto/AgentType4");

    builder.addSemanticTypes(new HashSet<>(types));
    builder.addSemanticType("http:example.org/ontology/BDIAgent");
    builder.addSemanticTypes(new HashSet<>(moreTypes));

    Agent agent = builder.build();
    assertEquals(5, agent.getSemanticTypes().size());
    assertTrue(agent.getSemanticTypes().containsAll(types));
    assertTrue(agent.getSemanticTypes().contains("http:example.org/ontology/BDIAgent"));
    assertTrue(agent.getSemanticTypes().contains("http:example.org/onto/AgentType3"));
    assertTrue(agent.getSemanticTypes().contains("http:example.org/onto/AgentType4"));
  }
}
