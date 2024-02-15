package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HypermediaMASPlatformTest {
  @Test
  public void testHMASPlatform() {
    HypermediaMASPlatform.Builder builder = new HypermediaMASPlatform.Builder();

    AbstractHostable[] hostables = {
            new Agent.Builder().build(),
            new Artifact.Builder().build(),
            new BaseResourceProfile.Builder(new Agent.Builder().build()).build(),
            new BaseSignifier.Builder().build()
    };

    HypermediaMASPlatform hMASPlatform = builder.addHostedResources(new HashSet<>(Arrays.asList(hostables)))
            .addHostedResource(new Workspace.Builder().build())
            .build();

    assertEquals(CORE.TERM.HMAS_PLATFORM, hMASPlatform.getType());
    assertEquals(CORE.NAMESPACE + "HypermediaMASPlatform", hMASPlatform.getTypeAsString());
    assertEquals(CORE.HMAS_PLATFORM, hMASPlatform.getTypeAsIRI());

    assertEquals(5, hMASPlatform.getHostedResources().size());

    assertThrows(UnsupportedOperationException.class, () -> {
      hMASPlatform.getHostedResources().add(null);
    });

  }

  @Test
  public void testHMASPlatformDefault() {
    HypermediaMASPlatform hMASPlatform = new HypermediaMASPlatform.Builder()
            .build();

    assertEquals(CORE.TERM.HMAS_PLATFORM, hMASPlatform.getType());
    assertEquals(0, hMASPlatform.getHostedResources().size());
  }
}
