package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
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

import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.*;
import static org.junit.jupiter.api.Assertions.*;

public class ResourceProfileGraphReaderTest {

  private final static Logger LOGGER = Logger.getLogger(ResourceProfileGraphReaderTest.class.getCanonicalName());

  private static final String PREFIXES =
    "@prefix hmas: <" + HMAS.PREFIX + "> \n" ;

  @Test
  public void testReadResourceProfileIRI() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf <urn:agent> .\n" +
      "<urn:agent> a hmas:Agent .";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    assertEquals(HMAS.RESOURCE_PROFILE, profile.getType());
    assertTrue(profile.getIRI().isPresent());
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

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    assertEquals(HMAS.RESOURCE_PROFILE, profile.getType());
    assertFalse(profile.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileMissing() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "[] a <http://example.org/unknown#ResourceProfile> .";

    Exception ex = assertThrows(InvalidResourceProfileException.class, () -> {
      ResourceProfileGraphReader.readFromString(expectedProfile);
    });

    String expectedMessage = "Resource profile was not found. " +
      "Ensure that an https://purl.org/hmas/core#ResourceProfile is represented.";
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
      ResourceProfileGraphReader.readFromString(expectedProfile);
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
      ResourceProfileGraphReader.readFromString(expectedProfile);
    });

    String expectedMessage = "A resource profile must describe a resource.";
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  @Test
  public void testReadResourceProfileFromFile() throws IOException, URISyntaxException {

    URL fileResource = ResourceProfileGraphReaderTest.class.getClassLoader()
      .getResource("resource-profile.ttl");

    String profilePath = Paths.get(fileResource.toURI()).toFile().getPath();
    ResourceProfile profile =
      ResourceProfileGraphReader.readFromFile(profilePath);

    AbstractProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getType());
    assertTrue(agent.getIRI().isPresent());
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

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    AbstractProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getType());
    assertTrue(agent.getIRI().isPresent());
    assertEquals("urn:agent", agent.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:agent"), agent.getIRI().get());
  }

  @Test
  public void testReadResourceProfileOfAgentBlankNode() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf [ a hmas:Agent ] .";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    AbstractProfiledResource agent = profile.getResource();
    assertEquals(AGENT, agent.getType());
    assertFalse(agent.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileOfArtifact() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf [ a hmas:Artifact ] .";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    AbstractProfiledResource artifact = profile.getResource();
    assertEquals(HMAS.ARTIFACT, artifact.getType());
    assertFalse(artifact.getIRI().isPresent());
  }

  @Test
  public void testReadResourceProfileOfHMASPlatform() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf <urn:platform> ;\n" +
      " hmas:isHostedOn [ a hmas:HypermediaMASPlatform ] .\n" +
      "<urn:platform> a hmas:HypermediaMASPlatform ;\n" +
      " hmas:isHostedOn [ a hmas:HypermediaMASPlatform ] ;\n" +
      " hmas:hosts [ a hmas:Agent ] .\n";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    Set<HypermediaMASPlatform> homePlatforms = profile.getHMASPlatforms();
    assertEquals(1, homePlatforms.size());
    HypermediaMASPlatform homePlatform = homePlatforms.iterator().next();
    assertFalse(homePlatform.getIRI().isPresent());
    assertEquals(0, homePlatform.getHostedResources().size());

    AbstractProfiledResource ownerResource = profile.getResource();
    assertEquals(HMAS.HMAS_PLATFORM, ownerResource.getType());
    assertTrue(profile.getIRI().isPresent());
    assertEquals("urn:platform", ownerResource.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:platform"), ownerResource.getIRI().get());
    assertEquals(1, ownerResource.getHMASPlatforms().size());

    HypermediaMASPlatform ownerPlatform = (HypermediaMASPlatform) ownerResource;
    assertEquals(1, ownerPlatform.getHostedResources().size());
    AbstractHostable hostedResource = ownerPlatform.getHostedResources().iterator().next();
    assertEquals(AGENT, hostedResource.getType());
  }

  //TODO Pass test
  @Test
  public void testReadResourceProfileCircular() {
    /*
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf <urn:platform> ;\n" +
      " hmas:isHostedOn <urn:super-platform> .\n" +
      "<urn:platform> a hmas:HypermediaMASPlatform ;\n" +
      " hmas:isHostedOn <urn:super-platform> ;\n" +
      " hmas:hosts [ a hmas:Agent ] .\n" +
      "<urn:super-platform> a hmas:HypermediaMASPlatform ;\n" +
      " hmas:hosts <urn:platform> .";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    Set<HypermediaMASPlatform> homePlatforms = profile.getHMASPlatforms();
    assertEquals(1, homePlatforms.size());
    HypermediaMASPlatform homePlatform = homePlatforms.iterator().next();
    assertTrue(homePlatform.getIRI().isPresent());
    assertEquals("urn:super-platform", homePlatform.getIRIAsString());
    assertEquals(1, homePlatform.getHostedResources());
    Hostable hostedResource = homePlatform.getHostedResources().iterator().next();
    assertEquals("urn:platform", hostedResource.getIRIAsString());

     */
  }

  @Test
  public void testReadResourceProfileOfWorkspace() {
    String expectedProfile = PREFIXES +
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
      ResourceProfileGraphReader.readFromString(expectedProfile);

    Set<HypermediaMASPlatform> homePlatforms = profile.getHMASPlatforms();
    assertEquals(0, homePlatforms.size());

    AbstractProfiledResource ownerResource = profile.getResource();
    assertEquals(WORKSPACE, ownerResource.getType());
    assertTrue(profile.getIRI().isPresent());
    assertEquals("urn:workspace", ownerResource.getIRIAsString().get());
    assertEquals(SimpleValueFactory.getInstance().createIRI("urn:workspace"), ownerResource.getIRI().get());
    assertEquals(0, ownerResource.getHMASPlatforms().size());

    Workspace ownerWorkspace = (Workspace) ownerResource;
    assertEquals(2, ownerWorkspace.getContainedResources().size());
    List<AbstractHostable> containedAgents = ownerWorkspace
      .getContainedResources()
      .stream()
      .filter(contained -> AGENT.equals(contained.getType()))
      .collect(Collectors.toList());
    assertEquals(1, containedAgents.size());
    assertFalse(containedAgents.get(0).getIRI().isPresent());

    List<AbstractHostable> containedWorkspaces = ownerWorkspace
      .getContainedResources()
      .stream()
      .filter(contained -> WORKSPACE.equals(contained.getType()))
      .collect(Collectors.toList());
    assertEquals(1, containedWorkspaces.size());
    assertFalse(containedWorkspaces.get(0).getIRI().isPresent());

    Workspace containedWorkspace = (Workspace) containedWorkspaces.get(0);
    assertEquals(1, containedWorkspace.getContainedResources().size());
    AbstractHostable nestedResource = containedWorkspace.getContainedResources().iterator().next();
    assertEquals(HMAS_PLATFORM, nestedResource.getType());
    assertFalse(nestedResource.getIRI().isPresent());

    HypermediaMASPlatform nestedPlatform = (HypermediaMASPlatform) nestedResource;
    assertEquals(1, nestedPlatform.getHostedResources().size());
    AbstractHostable hostedArtifact = nestedPlatform.getHostedResources().iterator().next();
    assertEquals(ARTIFACT, hostedArtifact.getType());
  }

  @Test
  public void testReadResourceProfileWithSignifier() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf [ a hmas:Artifact ];\n" +
      " hmas:exposesSignifier [ a hmas:Signifier ].";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    AbstractProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getType());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(1, profile.getExposedSignifiers().size());
  }

  @Test
  public void testReadResourceProfileWithSignifiersWithIRI() {
    String expectedProfile = PREFIXES +
      ".\n" +
      "<urn:profile> a hmas:ResourceProfile ;\n" +
      " hmas:isProfileOf [ a hmas:Artifact ];\n" +
      " hmas:exposesSignifier <urn:signifier-1>, [ a hmas:Signifier ].\n" +
      "<urn:signifier-1> a hmas:Signifier .";

    ResourceProfile profile =
      ResourceProfileGraphReader.readFromString(expectedProfile);

    AbstractProfiledResource artifact = profile.getResource();
    assertEquals(ARTIFACT, artifact.getType());
    assertFalse(artifact.getIRI().isPresent());

    assertEquals(2, profile.getExposedSignifiers().size());
    List<BaseSignifier> signifiersIRI = profile
      .getExposedSignifiers()
      .stream()
      .filter(signifier -> signifier.getIRI().isPresent())
      .collect(Collectors.toList());
    assertEquals(1, signifiersIRI.size());
    assertEquals("urn:signifier-1", signifiersIRI.get(0).getIRIAsString().get());

    List<BaseSignifier> signifiersBNode = profile
      .getExposedSignifiers()
      .stream()
      .filter(signifier -> !signifier.getIRI().isPresent())
      .collect(Collectors.toList());
    assertEquals(1, signifiersBNode.size());
  }
}
