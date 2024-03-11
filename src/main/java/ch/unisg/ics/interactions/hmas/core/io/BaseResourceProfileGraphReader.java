package ch.unisg.ics.interactions.hmas.core.io;

import ch.unisg.ics.interactions.hmas.core.hostables.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.unisg.ics.interactions.hmas.core.vocabularies.CORE.*;

public class BaseResourceProfileGraphReader {

  protected final Resource profileIRI;
  private final ValueFactory rdf = SimpleValueFactory.getInstance();
  protected Model model;

  protected BaseResourceProfileGraphReader(RDFFormat format, String representation) {

    loadModel(format, representation);

    Optional<Resource> locatedProfile = Models.subject(model.filter(null, RDF.TYPE, RESOURCE_PROFILE));
    if (locatedProfile.isPresent()) {
      this.profileIRI = locatedProfile.get();
    } else {
      throw new InvalidResourceProfileException("Resource profile was not found. " +
              "Ensure that an " + NAMESPACE + "ResourceProfile is represented.");
    }
  }

  public static BaseResourceProfile readFromFile(String path) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(path)));
    return readFromString(content);
  }

  /* Currently, the only supported format is Turtle */
  public static BaseResourceProfile readFromString(String representation) {
    BaseResourceProfileGraphReader reader = new BaseResourceProfileGraphReader(RDFFormat.TURTLE, representation);

    BaseResourceProfile.Builder profileBuilder =
            new BaseResourceProfile.Builder(reader.readOwnerResource())
                    .addHMASPlatforms(reader.readHomeHMASPlatforms())
                    .addSemanticTypes(reader.readSemanticTypes())
                    .addGraph(reader.getModel());

    Optional<IRI> profileIRI = reader.readProfileIRI();
    profileIRI.ifPresent(profileBuilder::setIRI);

    return profileBuilder.build();
  }

  private void loadModel(RDFFormat format, String representation) {
    this.model = new LinkedHashModel();

    RDFParser parser = Rio.createParser(format);
    parser.setRDFHandler(new StatementCollector(model));
    StringReader stringReader = new StringReader(representation);
    try {
      parser.parse(stringReader);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public Model getModel() {
    return this.model;
  }

  protected AbstractResource readResource(Resource node) {

    Set<IRI> types = Models.objectIRIs(model.filter(node, RDF.TYPE, null));

    if (types.contains(AGENT)) {
      return readAgent(node);
    } else if (types.contains(ARTIFACT)) {
      return readArtifact(node);
    } else if (types.contains(WORKSPACE)) {
      return readWorkspace(node);
    } else if (types.contains(HMAS_PLATFORM)) {
      return readHMASPlatform(node);
    }
    throw new InvalidResourceProfileException("Unknown type of profiled resource. " +
            "Supported resource types: Agent, Artifact, Workspace, Platform.");
  }

  protected Agent readAgent(Resource node) {
    Agent.Builder builder = new Agent.Builder();
    return (Agent) readHostable(builder, node);
  }

  protected Artifact readArtifact(Resource node) {
    Artifact.Builder builder = new Artifact.Builder();
    return (Artifact) readHostable(builder, node);
  }

  private Workspace readWorkspace(Resource node) {
    Workspace.Builder builder = new Workspace.Builder();
    Set<Resource> containedNodes = Models.objectResources(model.filter(node, CONTAINS, null));
    for (Resource hostedNode : containedNodes) {
      builder.addContainedResource((AbstractHostable) readResource(hostedNode));
    }
    return (Workspace) readArtifact(builder, node);
  }

  private HypermediaMASPlatform readHMASPlatform(Resource node) {
    HypermediaMASPlatform.Builder builder = new HypermediaMASPlatform.Builder();
    Set<Resource> hostedNodes = Models.objectResources(model.filter(node, HOSTS, null));
    for (Resource hostedNode : hostedNodes) {
      builder.addHostedResource((AbstractHostable) readResource(hostedNode));
    }
    return (HypermediaMASPlatform) readResource(builder, node);
  }

  private Artifact readArtifact(Artifact.AbstractBuilder<?, ?> builder, Resource node) {
    return (Artifact) readHostable(builder, node);
  }

  protected AbstractHostable readHostable(AbstractHostable.AbstractBuilder<?, ?> builder, Resource node) {

    Set<Resource> platformNodes = Models.objectResources(model.filter(node, IS_HOSTED_ON, null));
    for (Resource platformNode : platformNodes) {
      builder.addHMASPlatform(readHMASPlatform(platformNode));
    }

    return (AbstractHostable) readResource(builder, node);
  }

  protected AbstractResource readResource(AbstractResource.AbstractBuilder<?, ?> builder, Resource node) {
    if (node.isIRI()) {
      builder.setIRI(SimpleValueFactory.getInstance().createIRI(node.stringValue()));
    }

    Set<IRI> semanticTypes = Models.objectIRIs(model.filter(node, RDF.TYPE, null));
    for (IRI type : semanticTypes) {
      if (!builder.TYPE.toString().equals(type.stringValue())) {
        builder.addSemanticType(type.stringValue());
      }
    }

    Model resourceModel = model.filter(node, null, null);

    if (node.isBNode()) {
      for (Statement statement : resourceModel) {
        builder.addTriple(statement.getPredicate(), statement.getObject());
      }
    } else {
      builder.addGraph(resourceModel);
    }

    return builder.build();
  }

  protected ProfiledResource readOwnerResource() {
    Optional<Resource> node = Models.objectResource(model.filter(profileIRI, IS_PROFILE_OF, null));
    if (node.isPresent()) {
      return (ProfiledResource) readResource(node.get());
    }
    throw new InvalidResourceProfileException("A resource profile must describe a resource.");
  }

  protected final Set<HypermediaMASPlatform> readHomeHMASPlatforms() {
    Set<HypermediaMASPlatform> platforms = new HashSet<>();
    Set<Resource> platformNodes = Models.objectResources(model.filter(profileIRI, IS_HOSTED_ON, null));
    for (Resource platformNode : platformNodes) {
      platforms.add(readHMASPlatform(platformNode));
    }
    return platforms;
  }

  protected final Set<String> readSemanticTypes() {
    Set<String> semanticTypes = new HashSet<>();
    Set<IRI> semanticTypeIRIs = Models.objectIRIs(model.filter(profileIRI, RDF.TYPE, null));
    for (IRI type : semanticTypeIRIs) {
      semanticTypes.add(type.stringValue());
    }

    return semanticTypes;
  }

  protected final Optional<IRI> readProfileIRI() {
    if (profileIRI.isIRI()) {
      return Optional.of((IRI) profileIRI);
    }

    return Optional.empty();
  }
}
