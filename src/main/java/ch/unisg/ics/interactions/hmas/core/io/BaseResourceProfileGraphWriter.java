package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.*;

public class BaseResourceProfileGraphWriter<T extends BaseResourceProfile> implements GraphWriter {

  protected final Resource profileIRI;
  protected final T profile;
  protected final ModelBuilder graphBuilder;
  protected final ValueFactory rdf = SimpleValueFactory.getInstance();


  public BaseResourceProfileGraphWriter(final T profile) {
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
            .addGraph()
            .write(RDFFormat.TURTLE);
  }

  private Model getModel() {
    return graphBuilder.build();
  }

  @Override
  public BaseResourceProfileGraphWriter setNamespace(String prefix, String namespace) {
    this.graphBuilder.setNamespace(prefix, namespace);
    return this;
  }

  protected BaseResourceProfileGraphWriter addProfileIRI() {
    this.graphBuilder.add(profileIRI, RDF.TYPE, profile.getTypeAsIRI());
    return this;
  }

  protected BaseResourceProfileGraphWriter addSemanticTypes() {
    Set<String> semanticTypes = this.profile.getSemanticTypes();
    for (String type : semanticTypes) {
      this.graphBuilder.add(profileIRI, RDF.TYPE, rdf.createIRI(type));
    }
    return this;
  }

  protected BaseResourceProfileGraphWriter addOwnerResource() {
    AbstractResource resource = (AbstractResource) profile.getResource();
    Resource node = resolveHostableLocation(resource);
    this.graphBuilder.add(profileIRI, IS_PROFILE_OF, node);
    writeResource(resource, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter<T> addHomeHMASPlatforms() {
    Set<HypermediaMASPlatform> platforms = profile.getHMASPlatforms();
    for (HypermediaMASPlatform platform : platforms) {
      Resource node = resolveHostableLocation(platform);
      graphBuilder.add(profileIRI, IS_HOSTED_ON, node);
      writeResource(platform, node);
    }
    return this;
  }

  protected BaseResourceProfileGraphWriter writeResource(AbstractResource resource, Resource node) {

    if (AGENT.equals(resource.getTypeAsIRI())) {
      addAgent((Agent) resource, node);
    } else if (ARTIFACT.equals(resource.getTypeAsIRI())) {
      addArtifact((Artifact) resource, node);
    } else if (WORKSPACE.equals(resource.getTypeAsIRI())) {
      addWorkspace((Workspace) resource, node);
    } else if (HMAS_PLATFORM.equals(resource.getTypeAsIRI())) {
      addHMASPlatform((HypermediaMASPlatform) resource, node);
    } else {
      addResource(resource, node);
    }
    return this;
  }

  protected BaseResourceProfileGraphWriter addAgent(Agent agent, Resource node) {
    addHostable(agent, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addArtifact(Artifact artifact, Resource node) {
    addHostable(artifact, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addWorkspace(Workspace workspace, Resource node) {
    Set<AbstractHostable> contained = workspace.getContainedResources();
    for (AbstractHostable containedResource : contained) {
      Resource containedNode = resolveHostableLocation(containedResource);
      graphBuilder.add(node, CONTAINS, containedNode);
      writeResource(containedResource, containedNode);
    }
    addHostable(workspace, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addHMASPlatform(HypermediaMASPlatform platform, Resource node) {
    Set<AbstractHostable> hosted = platform.getHostedResources();
    for (AbstractHostable hostedResource : hosted) {
      Resource hostedNode = resolveHostableLocation(hostedResource);
      graphBuilder.add(node, HOSTS, hostedNode);
      writeResource(hostedResource, hostedNode);
    }
    addResource(platform, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addHostable(AbstractHostable resource, Resource node) {
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

    addResource(resource, node);
    return this;
  }

  protected BaseResourceProfileGraphWriter addResource(AbstractResource resource, Resource node) {
    graphBuilder.add(node, RDF.TYPE, resource.getTypeAsIRI());

    Set<String> semanticTypes = resource.getSemanticTypes();
    for (String type : semanticTypes) {
      graphBuilder.add(node, RDF.TYPE, rdf.createIRI(type));
    }
    return this.addGraph(resource, node);
  }

  protected BaseResourceProfileGraphWriter addGraph() {
    return this.addGraph(profile, profileIRI);
  }

  protected BaseResourceProfileGraphWriter addGraph(AbstractResource resource, Resource node) {
    Optional<Model> graph = resource.getResolvedGraph(node);
    if (graph.isPresent()) {
      getModel().addAll(graph.get());

      graph.get().getNamespaces().stream()
              .filter(ns -> !getModel().getNamespace(ns.getPrefix()).isPresent())
              .forEach(graphBuilder::setNamespace);
    }
    return this;
  }

  protected Resource resolveHostableLocation(AbstractResource resource) {
    return resource.getIRI().isPresent() ? resource.getIRI().get() : rdf.createBNode();
  }
}
