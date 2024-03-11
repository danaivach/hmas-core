package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.eclipse.rdf4j.model.util.Values.iri;
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

    builder.addTriple(iri("urn:agent"), CORE.IS_CONTAINED_IN, iri("http://example.org/myWorkspace"));
    Model model = new ModelBuilder()
            .setNamespace("jason", "http://example.org/jason/")
            .subject("urn:agent")
            .add(RDF.TYPE, iri("http://example.org/jason/Agent"))
            .build();
    builder.addGraph(model);
    builder.addTriple(iri("http://example.org/myWorkspace"), RDF.TYPE, iri("http://example.org/cartago/Workspace"));
    builder.addGraph(new ModelBuilder()
            .setNamespace("cartago", "http://example.org/cartago/")
            .build());
    builder.addTriple(CORE.IS_HOSTED_ON, iri("http://yggdrasil.interactions.ics.unisg.ch/#platform"));

    Agent agent = builder.build();
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
    assertEquals("urn:agent", agent.getIRIAsString().get());

    assertEquals(CORE.TERM.AGENT, agent.getType());
    assertEquals(CORE.NAMESPACE + "Agent", agent.getTypeAsString());
    assertEquals(CORE.AGENT, agent.getTypeAsIRI());

    assertEquals(3, agent.getHMASPlatforms().size());
    assertThrows(UnsupportedOperationException.class, () -> {
      agent.getHMASPlatforms().add(null);
    });

    Optional<Model> graphOp = agent.getGraph();
    assertTrue(graphOp.isPresent());
    Model graph = graphOp.get();
    assertEquals(2, graph.getNamespaces().size());
    assertTrue(graph.getNamespace("jason").isPresent());
    assertTrue(graph.getNamespace("cartago").isPresent());
    assertEquals("http://example.org/jason/", graph.getNamespace("jason").get().getName());
    assertEquals("http://example.org/cartago/", graph.getNamespace("cartago").get().getName());
    assertEquals(3, graph.size());
    assertTrue(graph.contains(iri("urn:agent"), CORE.IS_CONTAINED_IN, iri("http://example.org/myWorkspace")));
    assertTrue(graph.contains(iri("urn:agent"), RDF.TYPE, iri("http://example.org/jason/Agent")));
    assertTrue(graph.contains(iri("http://example.org/myWorkspace"), RDF.TYPE, iri("http://example.org/cartago/Workspace")));

    Optional<Model> resolvedGraphOp = agent.getResolvedGraph(iri("urn:agent"));
    assertTrue(resolvedGraphOp.isPresent());
    Model resolvedGraph = resolvedGraphOp.get();
    assertEquals(2, resolvedGraph.getNamespaces().size());
    assertTrue(resolvedGraph.getNamespace("jason").isPresent());
    assertTrue(resolvedGraph.getNamespace("cartago").isPresent());
    assertEquals("http://example.org/jason/", resolvedGraph.getNamespace("jason").get().getName());
    assertEquals("http://example.org/cartago/", resolvedGraph.getNamespace("cartago").get().getName());
    assertEquals(4, resolvedGraph.size());
    assertTrue(resolvedGraph.contains(iri("urn:agent"), CORE.IS_CONTAINED_IN, iri("http://example.org/myWorkspace")));
    assertTrue(resolvedGraph.contains(iri("urn:agent"), RDF.TYPE, iri("http://example.org/jason/Agent")));
    assertTrue(resolvedGraph.contains(iri("http://example.org/myWorkspace"), RDF.TYPE, iri("http://example.org/cartago/Workspace")));
    assertTrue(resolvedGraph.contains(iri("urn:agent"), CORE.IS_HOSTED_ON, iri("http://yggdrasil.interactions.ics.unisg.ch/#platform")));
  }

  @Test
  public void testAgentDefault() {
    Agent agent = new Agent.Builder()
            .build();

    assertFalse(agent.getIRI().isPresent());
    assertFalse(agent.getIRIAsString().isPresent());
    assertEquals(CORE.TERM.AGENT, agent.getType());
    assertEquals(0, agent.getHMASPlatforms().size());
  }
}
