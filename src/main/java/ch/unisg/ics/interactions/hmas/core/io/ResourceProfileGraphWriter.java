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

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.*;

public class ResourceProfileGraphWriter {

  protected final Resource profileIRI;
  protected final ResourceProfile profile;
  protected final ModelBuilder graphBuilder;
  protected final ValueFactory rdf = SimpleValueFactory.getInstance();

  public ResourceProfileGraphWriter(ResourceProfile profile) {
    this.profileIRI = resolveHostableLocation(profile);
    this.profile = profile;
    this.graphBuilder = new ModelBuilder();
  }

  public static String write(ResourceProfile resource) {
    return new ResourceProfileGraphWriter(resource).write();
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
            .write(RDFFormat.TURTLE);
  }

  public String write(RDFFormat format) {
    return ReadWriteUtils.writeToString(format, getModel());
  }

  private ResourceProfileGraphWriter addProfileIRI() {
    graphBuilder.add(profileIRI, RDF.TYPE, profile.getTypeAsIRI());
    return this;
  }

  private ResourceProfileGraphWriter addOwnerResource() {
    AbstractProfiledResource resource = profile.getResource();
    Resource node = resolveHostableLocation(resource);
    graphBuilder.add(profileIRI, IS_PROFILE_OF, node);
    addResource(resource, node);
    return this;
  }

  private ResourceProfileGraphWriter addHomeHMASPlatforms() {
    Set<HypermediaMASPlatform> platforms = profile.getHMASPlatforms();
    for (HypermediaMASPlatform platform : platforms) {
      Resource node = resolveHostableLocation(platform);
      graphBuilder.add(profileIRI, IS_HOSTED_ON, node);
      addResource(platform, node);
    }
    return this;
  }

  private ResourceProfileGraphWriter addResource(AbstractHostable resource, Resource node) {

    if (AGENT.equals(resource.getTypeAsIRI())) {
      addAgent((Agent) resource, node);
    } else if (ARTIFACT.equals(resource.getTypeAsIRI())) {
      addArtifact((Artifact) resource, node);
    } else if (WORKSPACE.equals(resource.getTypeAsIRI())) {
      addWorkspace((Workspace) resource, node);
    } else if (HMAS_PLATFORM.equals(resource.getTypeAsIRI())) {
      addHMASPlatform((HypermediaMASPlatform) resource, node);
    } else {
      addHostable(resource, node);
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
      graphBuilder.add(node, CONTAINS, containedNode);
      addResource(containedResource, containedNode);
    }
    addHostable(workspace, node);
    return this;
  }

  private ResourceProfileGraphWriter addHMASPlatform(HypermediaMASPlatform platform, Resource node) {
    Set<AbstractHostable> hosted = platform.getHostedResources();
    for (AbstractHostable hostedResource : hosted) {
      Resource hostedNode = resolveHostableLocation(hostedResource);
      graphBuilder.add(node, HOSTS, hostedNode);
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
      graphBuilder.add(node, IS_HOSTED_ON, platformNode);
      addHMASPlatform(platform, platformNode);
    }
    return this;
  }

  protected Resource resolveHostableLocation(AbstractHostable hostable) {
    return hostable.getIRI().isPresent() ? hostable.getIRI().get() : rdf.createBNode();
  }

}
