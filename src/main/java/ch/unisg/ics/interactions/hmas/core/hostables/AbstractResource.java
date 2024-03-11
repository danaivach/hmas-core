package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractResource implements Resource {

  private final Optional<String> IRI;
  private final HMAS type;
  private final Set<String> semanticTypes;
  private final Optional<Model> graph;
  private final Optional<Model> bNodeGraph;

  @SuppressWarnings("unchecked")
  protected AbstractResource(final HMAS type, final AbstractBuilder builder) {

    this.type = type;
    this.IRI = builder.IRI;
    this.semanticTypes = ImmutableSet.copyOf(builder.semanticTypes);
    this.graph = builder.graph;
    this.bNodeGraph = builder.bNodeGraph;
  }

  public Set<String> getSemanticTypes() {
    return this.semanticTypes;
  }

  public String getTypeAsString() {
    return this.type.toString();
  }

  public IRI getTypeAsIRI() {
    return this.type.toIRI();
  }

  public HMAS getType() {
    return this.type;
  }

  public Optional<IRI> getIRI() {
    return this.IRI.map(s -> SimpleValueFactory.getInstance().createIRI(s));
  }

  public Optional<String> getIRIAsString() {
    return this.IRI;
  }

  public Optional<Model> getGraph() { return graph; }

  public Optional<Model> getResolvedGraph(org.eclipse.rdf4j.model.Resource IRIOrBNode) {
    if (this.bNodeGraph.isPresent()) {
      ModelBuilder resolvedModelBuilder = new ModelBuilder();
      this.bNodeGraph.get().forEach(statement ->
              resolvedModelBuilder.add(IRIOrBNode, statement.getPredicate(), statement.getObject())
      );
      Model resolvedModel = resolvedModelBuilder.build();

      if (this.graph.isPresent()) {
        this.graph.get().getNamespaces().stream()
                .forEach(ns -> resolvedModel.setNamespace(ns.getPrefix(), ns.getName()));
        resolvedModel.addAll(this.graph.get());
      }
      return Optional.of(resolvedModel);
    } else {
      return this.graph;
    }
  }

  public static abstract class AbstractBuilder<S extends AbstractBuilder, T extends AbstractResource> {

    public final HMAS TYPE;
    protected final Set<String> semanticTypes;
    private Optional<String> IRI;
    private Optional<Model> graph;
    private Optional<Model> bNodeGraph;

    protected AbstractBuilder(HMAS type) {
      this.TYPE = type;
      this.IRI = Optional.empty();
      this.semanticTypes = new HashSet<>();
      this.semanticTypes.add(type.toString());
      this.graph = Optional.empty();
      this.bNodeGraph = Optional.empty();
    }

    abstract protected S getBuilder();

    protected static boolean validateIRI(String IRI) {
      try {
        SimpleValueFactory.getInstance().createIRI(IRI);
        return true;
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("The IRI of a Hostable must be valid");
      }
    }

    public S setIRI(final IRI IRI) {
      this.IRI = Optional.of(IRI.toString());
      return getBuilder();
    }

    public S setIRIAsString(final String IRI) {
      validateIRI(IRI);
      this.IRI = Optional.of(IRI);
      return getBuilder();
    }

    public S addSemanticType(final String type) {
      this.semanticTypes.add(type);
      return getBuilder();
    }

    public S addSemanticTypes(final Set<String> types) {
      this.semanticTypes.addAll(types);
      return getBuilder();
    }

    /**
     * Adds an RDF graph. If an RDF graph is already present, it will be merged with the new graph.
     *
     * @param graph the RDF graph to be added
     * @return this <code>Builder</code>
     */
    public S addGraph(Model graph) {
      if (this.graph.isPresent()) {
        this.graph.get().addAll(graph);
      } else {
        this.graph = Optional.of(graph);
      }
      graph.getNamespaces().stream()
              .filter(ns -> !this.graph.get().getNamespace(ns.getPrefix()).isPresent())
              .forEach(ns -> this.graph.get().setNamespace(ns.getPrefix(), ns.getName()));

      return getBuilder();
    }

    /**
     * Convenience method used to add a single triple. If an RDF graph is already present, the triple
     * will be added to the existing graph.
     *
     * @param subject   the subject
     * @param predicate the predicate
     * @param object    the object
     * @return this <code>Builder</code>
     */
    public S addTriple(org.eclipse.rdf4j.model.Resource subject, IRI predicate, Value object) {
      if (this.graph.isPresent()) {
        this.graph.get().add(subject, predicate, object);
      } else {
        this.graph = Optional.of(new ModelBuilder().add(subject, predicate, object).build());
      }

      return getBuilder();
    }

    /**
     * Convenience method used to add a single triple with this Resource as a subject. If an RDF graph is already present,
     * the triple will be added to the existing graph.
     *
     * @param predicate the predicate
     * @param object    the object
     * @return this <code>Builder</code>
     */
    public S addTriple(IRI predicate, Value object) {
      if (this.bNodeGraph.isPresent()) {
        org.eclipse.rdf4j.model.Resource bNode = this.bNodeGraph.get().subjects().iterator().next();
        this.bNodeGraph.get().add(bNode, predicate, object);
      } else {
        this.bNodeGraph = Optional.of(new ModelBuilder().add(Values.bnode(), predicate, object).build());
      }

      return getBuilder();
    }


    public abstract T build();
  }
}
