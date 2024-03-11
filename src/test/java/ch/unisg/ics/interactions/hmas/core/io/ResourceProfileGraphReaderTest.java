package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.AGENT;
import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.WORKSPACE;
import static org.junit.jupiter.api.Assertions.*;

public class ResourceProfileGraphReaderTest {

  private final static Logger LOGGER = Logger.getLogger(ResourceProfileGraphReaderTest.class.getCanonicalName());

  private static final String PREFIXES =
          "@prefix hmas: <" + CORE.NAMESPACE + "> \n";

  @Test
  public void testReadResourceProfileIRI() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:agent> .\n" +
            "<urn:agent> a hmas:Agent .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    assertEquals(CORE.RESOURCE_PROFILE, profile.getTypeAsIRI());
    assertTrue(profile.getIRI().isPresent());
    assertTrue(profile.getIRIAsString().isPresent());
    assertEquals("urn:profile", profile.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:profile"), profile.getIRI().get());
  }

  @Test
  public void testReadResourceProfileBNode() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "[] a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:agent> .\n" +
            "<urn:agent> a hmas:Agent .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    assertEquals(CORE.RESOURCE_PROFILE, profile.getTypeAsIRI());
    assertFalse(profile.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileMissing() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "[] a <http://example.org/unknown#ResourceProfile> .";

    Exception ex = assertThrows(InvalidResourceProfileException.class, () -> {
      BaseResourceProfileGraphReader.readFromString(expectedProfile);
    });

    String expectedMessage = "Resource profile was not found. " +
            "Ensure that an https://purl.org/hmas/ResourceProfile is represented.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  @Test
  public void testReadResourceProfileMissingOwner() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "[] a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:agent> .\n" +
            "<urn:agent> a <http://example.org/unknown#Agent> .";

    Exception ex = assertThrows(InvalidResourceProfileException.class, () -> {
      BaseResourceProfileGraphReader.readFromString(expectedProfile);
    });

    String expectedMessage = "Unknown type of profiled resource. " +
            "Supported resource types: Agent, Artifact, Workspace, Platform.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  @Test
  public void testReadResourceProfileUknownOwnerType() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "[] a hmas:ResourceProfile .";

    Exception ex = assertThrows(InvalidResourceProfileException.class, () -> {
      BaseResourceProfileGraphReader.readFromString(expectedProfile);
    });

    String expectedMessage = "A resource profile must describe a resource.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  @Test
  public void testReadResourceProfileFromFile() throws IOException, URISyntaxException {

    URL fileResource = ResourceProfileGraphReaderTest.class.getClassLoader()
            .getResource("resource-profile.ttl");

    String profilePath = Paths.get(fileResource.toURI()).toFile().getPath();
    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromFile(profilePath);

    ProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getTypeAsIRI());
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals("urn:agent", agent.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
  }

  @Test
  public void testReadResourceProfileOfAgentIRI() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:agent> .\n" +
            "<urn:agent> a hmas:Agent .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getTypeAsIRI());
    assertTrue(agent.getIRI().isPresent());
    assertTrue(agent.getIRIAsString().isPresent());
    assertEquals("urn:agent", agent.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
  }

  @Test
  public void testReadResourceProfileOfAgentBlankNode() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Agent ] .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getTypeAsIRI());
    assertFalse(agent.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileOfArtifact() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf [ a hmas:Artifact ] .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    ProfiledResource artifact = profile.getResource();
    assertEquals(CORE.ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileSemanticTypes() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile, <https://example.org/onto#TDDocument> ;\n" +
            " hmas:isProfileOf [ \n" +
            "   a hmas:Artifact, <https://www.w3.org/2019/wot/td#Thing>,  " +
            "     <https://saref.etsi.org/core/Actuator> ] .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    assertEquals(2, profile.getSemanticTypes().size());
    assertTrue(profile.getSemanticTypes().contains("https://example.org/onto#TDDocument"));

    ProfiledResource artifact = profile.getResource();
    assertEquals(CORE.ARTIFACT, artifact.getTypeAsIRI());
    assertFalse(artifact.getIRI().isPresent());
    assertEquals(3, artifact.getSemanticTypes().size());
    assertTrue(artifact.getSemanticTypes().contains("https://www.w3.org/2019/wot/td#Thing"));
    assertTrue(artifact.getSemanticTypes().contains("https://saref.etsi.org/core/Actuator"));
  }

  @Test
  public void testReadResourceProfileOfHMASPlatform() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:platform> ;\n" +
            " hmas:isHostedOn [ a hmas:HypermediaMASPlatform ] .\n" +
            "<urn:platform> a hmas:HypermediaMASPlatform ;\n" +
            " hmas:hosts [ a hmas:Agent ] .\n";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    Set<HypermediaMASPlatform> homePlatforms = profile.getHMASPlatforms();
    assertEquals(1, homePlatforms.size());
    HypermediaMASPlatform homePlatform = homePlatforms.iterator().next();
    assertFalse(homePlatform.getIRI().isPresent());
    assertEquals(0, homePlatform.getHostedResources().size());

    HypermediaMASPlatform ownerResource = (HypermediaMASPlatform) profile.getResource();
    assertEquals(CORE.HMAS_PLATFORM, ownerResource.getTypeAsIRI());
    assertTrue(profile.getIRI().isPresent());
    assertTrue(ownerResource.getIRIAsString().isPresent());
    assertTrue(ownerResource.getIRI().isPresent());
    assertEquals("urn:platform", ownerResource.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:platform"), ownerResource.getIRI().get());

    HypermediaMASPlatform ownerPlatform = ownerResource;
    assertEquals(1, ownerPlatform.getHostedResources().size());
    AbstractHostable hostedResource = ownerPlatform.getHostedResources().iterator().next();
    assertEquals(AGENT, hostedResource.getTypeAsIRI());
  }

  @Test
  public void testReadResourceProfileOfWorkspace() {
    String expectedProfile = PREFIXES +
            ".\n" +
            "<urn:profile> a hmas:ResourceProfile ;\n" +
            " hmas:isProfileOf <urn:workspace> .\n" +
            "<urn:workspace> a hmas:Workspace ;\n" +
            " hmas:contains [ a hmas:Agent ],\n" +
            "  [ a hmas:Workspace ] .";

    BaseResourceProfile profile =
            BaseResourceProfileGraphReader.readFromString(expectedProfile);

    Set<HypermediaMASPlatform> homePlatforms = profile.getHMASPlatforms();
    assertEquals(0, homePlatforms.size());

    ProfiledResource ownerResource = profile.getResource();
    assertEquals(WORKSPACE, ownerResource.getTypeAsIRI());
    assertTrue(profile.getIRI().isPresent());
    assertEquals("urn:workspace", ownerResource.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:workspace"), ownerResource.getIRI().get());

    Workspace ownerWorkspace = (Workspace) ownerResource;
    assertEquals(2, ownerWorkspace.getContainedResources().size());
    List<AbstractHostable> containedAgents = ownerWorkspace
            .getContainedResources()
            .stream()
            .filter(contained -> AGENT.equals(contained.getTypeAsIRI()))
            .collect(Collectors.toList());
    assertEquals(1, containedAgents.size());
    assertFalse(containedAgents.get(0).getIRI().isPresent());

    List<AbstractHostable> containedWorkspaces = ownerWorkspace
            .getContainedResources()
            .stream()
            .filter(contained -> WORKSPACE.equals(contained.getTypeAsIRI()))
            .collect(Collectors.toList());
    assertEquals(1, containedWorkspaces.size());
    assertFalse(containedWorkspaces.get(0).getIRI().isPresent());

    Workspace containedWorkspace = (Workspace) containedWorkspaces.get(0);
    assertEquals(0, containedWorkspace.getContainedResources().size());
  }
}
