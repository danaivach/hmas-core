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

public class ResourceProfileGraphWriter<T extends ResourceProfile> implements GraphWriter {

  protected final Resource profileIRI;
  protected final T profile;
  protected final ModelBuilder graphBuilder;
  protected final ValueFactory rdf = SimpleValueFactory.getInstance();


  public ResourceProfileGraphWriter(final T profile) {
    this.profileIRI = resolveHostableLocation(profile);
    this.profile = profile;
    this.graphBuilder = new ModelBuilder();
  }

  @Override
  public String write(RDFFormat format) {
    return ReadWriteUtils.writeToString(format, getModel());
  }

  @Override
  public String write() {

    return this.setNamespace(PREFIX, NAMESPACE)
            .addProfileIRI()
            .addSemanticTypes()
            .addOwnerResource()
            .addHomeHMASPlatforms()
            .write(RDFFormat.TURTLE);
  }

  private Model getModel() {
    return graphBuilder.build();
  }

  @Override
  public ResourceProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  private ResourceProfileGraphWriter addProfileIRI() {
    this.graphBuilder.add(profileIRI, RDF.TYPE, profile.getTypeAsIRI());
    return this;
  }

  public ResourceProfileGraphWriter addSemanticTypes() {
    Set<String> semanticTypes = this.profile.getSemanticTypes();
    for (String type : semanticTypes) {
      this.graphBuilder.add(profileIRI, RDF.TYPE, rdf.createIRI(type));
    }
    return this;
  }

  private ResourceProfileGraphWriter addOwnerResource() {
    AbstractProfiledResource resource = profile.getResource();
    Resource node = resolveHostableLocation(resource);
    this.graphBuilder.add(profileIRI, IS_PROFILE_OF, node);
    addResource(resource, node);
    return this;
  }

  private ResourceProfileGraphWriter<T> addHomeHMASPlatforms() {
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

  protected ResourceProfileGraphWriter addHostable(AbstractHostable resource, Resource node) {
    graphBuilder.add(node, RDF.TYPE, resource.getTypeAsIRI());

    Set<HypermediaMASPlatform> platforms = resource.getHMASPlatforms();
    for (HypermediaMASPlatform platform : platforms) {
      Resource platformNode = resolveHostableLocation(platform);
      graphBuilder.add(node, IS_HOSTED_ON, platformNode);
      addHMASPlatform(platform, platformNode);
    }

    Set<String> semanticTypes = resource.getSemanticTypes();
    for (String type : semanticTypes) {
      graphBuilder.add(node, RDF.TYPE, rdf.createIRI(type));
    }
    return this;
  }

  protected Resource resolveHostableLocation(AbstractHostable hostable) {
    return hostable.getIRI().isPresent() ? hostable.getIRI().get() : rdf.createBNode();
  }
}
