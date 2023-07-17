package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import com.google.common.collect.ImmutableSet;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractResource {

  private final Optional<String> IRI;
  private final HMAS type;
  private final Set<String> semanticTypes;

  protected AbstractResource(final HMAS type, final AbstractBuilder builder) {

    this.type = type;
    this.IRI = builder.IRI;
    this.semanticTypes = ImmutableSet.copyOf(builder.semanticTypes);
  }

  public Set<String> getSemanticTypes() {
    return this.semanticTypes;
  }

  public String getTypeAsString() {
    return this.type.toString();
  }

  public org.eclipse.rdf4j.model.IRI getTypeAsIRI() {
    return this.type.toIRI();
  }

  public HMAS getType() {
    return this.type;
  }

  public Optional<IRI> getIRI() {
    if (this.IRI.isPresent()) {
      return Optional.of(SimpleValueFactory.getInstance().createIRI(this.IRI.get()));
    }
    return Optional.empty();
  }

  public Optional<String> getIRIAsString() {
    return this.IRI;
  }

  public static abstract class AbstractBuilder<S extends AbstractBuilder, T extends AbstractResource> {

    public final HMAS TYPE;
    protected final Set<String> semanticTypes;
    private Optional<String> IRI;

    protected AbstractBuilder(HMAS type) {
      this.TYPE = type;
      this.IRI = Optional.empty();
      this.semanticTypes = new HashSet<>();
    }

    private static boolean validateIRI(String IRI) {
      try {
        SimpleValueFactory.getInstance().createIRI(IRI);
        return true;
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("The IRI of a Hostable must be valid.");
      }
    }

    @SuppressWarnings("unchecked")
    public S setIRI(final IRI IRI) {
      this.IRI = Optional.of(IRI.toString());
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setIRIAsString(final String IRI) {
      validateIRI(IRI);
      this.IRI = Optional.of(IRI);
      return (S) this;
    }

    public S addSemanticType(final String type) {
      this.semanticTypes.add(type);
      return (S) this;
    }

    public S addSemanticTypes(final Set<String> types) {
      this.semanticTypes.addAll(types);
      return (S) this;
    }

    @SuppressWarnings("unchecked")
    public abstract T build();
  }
}
