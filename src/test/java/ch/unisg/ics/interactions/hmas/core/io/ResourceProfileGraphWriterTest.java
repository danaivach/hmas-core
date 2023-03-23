package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceProfileGraphWriterTest {

  private final static Logger LOGGER = Logger.getLogger(ResourceProfileGraphWriterTest.class.getCanonicalName());

  private static final String PREFIX = "@prefix hmas: <" + CORE.NAMESPACE + ">";
  private static final String BASE_URI = "http://example.org/";

  private static Model readModelFromString(String profile, String baseURI)
          throws RDFParseException, RDFHandlerException, IOException {
    StringReader stringReader = new StringReader(profile);

    RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
    Model model = new LinkedHashModel();
    rdfParser.setRDFHandler(new StatementCollector(model));

    rdfParser.parse(stringReader, baseURI);

    return model;
  }

  @Test
  public void testWriteResourceProfileOfAgentIRI() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:agent> .\n" +
            "<urn:agent> a hmas:Agent .";

    ResourceProfile profile =
            new ResourceProfile.Builder(new Agent.Builder()
                    .setIRIAsString("urn:agent").build())
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileOfAgentBlankNode() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Agent ] .";

    ResourceProfile profile =
            new ResourceProfile.Builder(new Agent.Builder().build())
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileOfArtifact() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ] .";

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder().build())
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileOfHMASPlatform() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:platform> ;\n" +
            " hmas:isHostedOn [ a hmas:HypermediaMASPlatform ] .\n" +
            "<urn:platform> a hmas:HypermediaMASPlatform ;\n" +
            " hmas:isHostedOn [ a hmas:HypermediaMASPlatform ] ;\n" +
            " hmas:hosts [ a hmas:Agent ] .\n";

    ResourceProfile profile =
            new ResourceProfile.Builder(
                    new HypermediaMASPlatform.Builder().setIRIAsString("urn:platform")
                            .addHostedResource(new Agent.Builder().build())
                            .addHMASPlatform(new HypermediaMASPlatform.Builder().build())
                            .build())
                    .addHMASPlatform(new HypermediaMASPlatform.Builder().build())
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileNested() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:platform> ;\n" +
            " hmas:isHostedOn <urn:super-platform> .\n" +
            "<urn:platform> a hmas:HypermediaMASPlatform ;\n" +
            " hmas:isHostedOn <urn:super-platform> ;\n" +
            " hmas:hosts [ a hmas:Agent ] .\n" +
            "<urn:super-platform> a hmas:HypermediaMASPlatform .";

    HypermediaMASPlatform superPlatform =
            new HypermediaMASPlatform.Builder()
                    .setIRIAsString("urn:super-platform")
                    .build();

    ResourceProfile profile =
            new ResourceProfile.Builder(
                    new HypermediaMASPlatform.Builder().setIRIAsString("urn:platform")
                            .addHostedResource(new Agent.Builder().build())
                            .addHMASPlatform(superPlatform).build())
                    .addHMASPlatform(superPlatform)
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileOfWorkspace() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:workspace> .\n" +
            "<urn:workspace> a hmas:Workspace ;\n" +
            " hmas:contains [ a hmas:Agent ],\n" +
            "  [ a hmas:Workspace ;\n" +
            "  hmas:contains [ a hmas:HypermediaMASPlatform ;\n" +
            "   hmas:hosts [ a hmas:Artifact ]\n" +
            "   ]\n" +
            "  ] .";

    ResourceProfile profile =
            new ResourceProfile.Builder(new Workspace.Builder()
                    .setIRIAsString("urn:workspace")
                    .addContainedResource(new Agent.Builder().build())
                    .addContainedResource(
                            new Workspace.Builder()
                                    .addContainedResource(new HypermediaMASPlatform.Builder()
                                            .addHostedResource(new Artifact.Builder().build())
                                            .build())
                                    .build())
                    .build())
                    .setIRIAsString("urn:profile")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  @Test
  public void testWriteResourceProfileSemanticTypes() throws IOException {
    String expectedProfile = PREFIX +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile, <https://example.org/onto#TDDocument> ;\n" +
            " hmas:isProfileOf <urn:artifact> .\n" +
            "<urn:artifact> a hmas:Artifact, <https://www.w3.org/2019/wot/td#Thing>,  " +
            " <https://saref.etsi.org/core/Actuator>.";

    ResourceProfile profile =
            new ResourceProfile.Builder(new Artifact.Builder()
                    .setIRIAsString("urn:artifact")
                    .addSemanticType("https://www.w3.org/2019/wot/td#Thing")
                    .addSemanticType("https://saref.etsi.org/core/Actuator")
                    .build())
                    .setIRIAsString("urn:profile")
                    .addSemanticType("https://example.org/onto#TDDocument")
                    .build();

    assertIsomorphicGraphs(expectedProfile, profile);
  }

  private void assertIsomorphicGraphs(String expectedProfile, ResourceProfile profile) throws RDFParseException,
          RDFHandlerException, IOException {

    Model expectedModel = readModelFromString(expectedProfile, BASE_URI);

    String actualProfile = new ResourceProfileGraphWriter(profile)
            .setNamespace("hmas", CORE.NAMESPACE)
            .write();

    LOGGER.info("Expected:\n" + expectedProfile);
    LOGGER.info("Actual:\n" + actualProfile);

    Model actualModel = readModelFromString(actualProfile, BASE_URI);

    assertTrue(Models.isomorphic(expectedModel, actualModel));
  }

}
