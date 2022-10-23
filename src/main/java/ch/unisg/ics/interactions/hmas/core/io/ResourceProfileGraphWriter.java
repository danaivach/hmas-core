package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS.*;

public class ResourceProfileGraphWriter {

  private final Resource profileIRI;
  private final ResourceProfile profile;
  private final ModelBuilder graphBuilder;
  private final ValueFactory rdf = SimpleValueFactory.getInstance();

  public ResourceProfileGraphWriter(ResourceProfile profile) {
    this.profileIRI = resolveHostableLocation(profile);
    this.profile = profile;
    this.graphBuilder = new ModelBuilder();
  }

  /**
   * Sets a prefix binding for a given namespace.
   *
   * @param prefix    the prefix to be used in the serialized representation
   * @param namespace the given namespace
   * @return this <code>ResourceProfileGraphWriter</code>
   */
  public ResourceProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  private Model getModel() {
    return graphBuilder.build();
  }

  public String write() {
    return this.addProfileIRI()
      .addOwnerResource()
      .addHomeHMASPlatforms()
      .addSignifiers()
      .write(RDFFormat.TURTLE);
  }

  public String write(RDFFormat format) {
    return ReadWriteUtils.writeToString(format, getModel());
  }

  public static String write(ResourceProfile resource) {
    return new ResourceProfileGraphWriter(resource).write();
  }

  private ResourceProfileGraphWriter addProfileIRI() {
    graphBuilder.add(profileIRI, RDF.TYPE, profile.getTypeAsIRI());
    return this;
  }

  private ResourceProfileGraphWriter addOwnerResource() {
    AbstractProfiledResource resource = profile.getResource();
    Resource node = resolveHostableLocation(resource);
    graphBuilder.add(profileIRI, IS_PROFILE_OF.toIRI(), node);
    addResource(resource, node);
    return this;
  }

  private ResourceProfileGraphWriter addHomeHMASPlatforms() {
    Set<HypermediaMASPlatform> platforms = profile.getHMASPlatforms();
    for (HypermediaMASPlatform platform : platforms) {
      Resource node = resolveHostableLocation(platform);
      graphBuilder.add(profileIRI, IS_HOSTED_ON.toIRI(), node);
      addResource(platform, node);
    }
    return this;
  }

  private ResourceProfileGraphWriter addSignifiers() {
    Set<BaseSignifier> signifiers = profile.getExposedSignifiers();
    if (!signifiers.isEmpty()) {
      for (BaseSignifier signifier : signifiers) {
        Resource locatedSignifier = resolveHostableLocation(signifier);
        graphBuilder.add(profileIRI, EXPOSES_SIGNIFIER.toIRI(), locatedSignifier);
        graphBuilder.add(locatedSignifier, RDF.TYPE, SIGNIFIER.toIRI());
      }
    }
    return this;
  }

  private ResourceProfileGraphWriter addResource(AbstractHostable resource, Resource node) {

    switch(resource.getType()) {
      case AGENT -> addAgent((Agent) resource, node);
      case ARTIFACT -> addArtifact((Artifact) resource, node);
      case WORKSPACE -> addWorkspace((Workspace) resource, node);
      case HMAS_PLATFORM -> addHMASPlatform((HypermediaMASPlatform) resource, node);
      default -> addHostable(resource, node);
    }
    return this;
  }

  private ResourceProfileGraphWriter addAgent(Agent agent, Resource node) {
    addHostable(agent, node);
    return this;
  }

  private ResourceProfileGraphWriter addArtifact(Artifact artifact, Resource node) {
    addHostable(artifact, node);
    return this;
  }

  private ResourceProfileGraphWriter addWorkspace(Workspace workspace, Resource node) {
    Set<AbstractHostable> contained = workspace.getContainedResources();
    for (AbstractHostable containedResource : contained) {
      Resource containedNode = resolveHostableLocation(containedResource);
      graphBuilder.add(node, CONTAINS.toIRI(), containedNode);
      addResource(containedResource, containedNode);
    }
    addHostable(workspace, node);
    return this;
  }

  private ResourceProfileGraphWriter addHMASPlatform(HypermediaMASPlatform platform, Resource node) {
    Set<AbstractHostable> hosted = platform.getHostedResources();
    for (AbstractHostable hostedResource : hosted) {
      Resource hostedNode = resolveHostableLocation(hostedResource);
      graphBuilder.add(node, HOSTS.toIRI(), hostedNode);
      addResource(hostedResource, hostedNode);
    }
    addHostable(platform, node);
    return this;
  }

  private ResourceProfileGraphWriter addHostable(AbstractHostable resource, Resource node) {
    graphBuilder.add(node, RDF.TYPE, resource.getTypeAsIRI());
    Set<HypermediaMASPlatform> platforms = resource.getHMASPlatforms();
    for (HypermediaMASPlatform platform : platforms) {
      Resource platformNode = resolveHostableLocation(platform);
      graphBuilder.add(node, IS_HOSTED_ON.toIRI() , platformNode);
      addHMASPlatform(platform, platformNode);
    }
    return this;
  }

  private Resource resolveHostableLocation(AbstractHostable hostable) {
    return hostable.getIRI().isPresent() ? hostable.getIRI().get() : rdf.createBNode();
  }

}
