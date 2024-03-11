package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.eclipse.rdf4j.model.util.Values.iri;
import static org.junit.jupiter.api.Assertions.*;

public class ArtifactTest {
  @Test
  public void testArtifact() {
    Artifact.Builder builder = new Artifact.Builder();

    HypermediaMASPlatform[] platforms = {
            new HypermediaMASPlatform.Builder().build(),
            new HypermediaMASPlatform.Builder().build()
    };

    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    Artifact artifact = builder.build();

    assertEquals(CORE.TERM.ARTIFACT, artifact.getType());
    assertEquals(CORE.NAMESPACE + "Artifact", artifact.getTypeAsString());
    assertEquals(CORE.ARTIFACT, artifact.getTypeAsIRI());

    assertEquals(3, artifact.getHMASPlatforms().size());
    assertThrows(UnsupportedOperationException.class, () -> {
      artifact.getHMASPlatforms().add(null);
    });

    assertFalse(artifact.getGraph().isPresent());
    assertFalse(artifact.getResolvedGraph(iri("urn:artifact")).isPresent());
  }

  @Test
  public void testArtifactDefault() {
    Artifact artifact = new Artifact.Builder()
            .build();

    assertEquals(CORE.TERM.ARTIFACT, artifact.getType());
    assertEquals(0, artifact.getHMASPlatforms().size());
  }
}
