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
            new HypermediaMASPlatform.Builder().build(),
            new ResourceProfile.Builder(new Agent.Builder().build()).build(),
            new BaseSignifier.Builder().build()
    };

    HypermediaMASPlatform[] platforms = {
            new HypermediaMASPlatform.Builder().build(),
            new HypermediaMASPlatform.Builder().build()
    };

    HypermediaMASPlatform hMASPlatform = builder.addHostedResources(new HashSet<>(Arrays.asList(hostables)))
            .addHostedResource(new Workspace.Builder().build())
            .addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)))
            .addHMASPlatform(new HypermediaMASPlatform.Builder().build())
            .build();

    assertEquals(CORE.TERM.HMAS_PLATFORM, hMASPlatform.getType());
    assertEquals(CORE.NAMESPACE + "HypermediaMASPlatform", hMASPlatform.getTypeAsString());
    assertEquals(CORE.HMAS_PLATFORM, hMASPlatform.getTypeAsIRI());

    assertEquals(6, hMASPlatform.getHostedResources().size());
    assertEquals(3, hMASPlatform.getHMASPlatforms().size());

    assertThrows(UnsupportedOperationException.class, () -> {
      hMASPlatform.getHostedResources().add(null);
    });

    assertThrows(UnsupportedOperationException.class, () -> {
      hMASPlatform.getHMASPlatforms().add(null);
    });
  }

  @Test
  public void testHMASPlatformDefault() {
    HypermediaMASPlatform hMASPlatform = new HypermediaMASPlatform.Builder()
            .build();

    assertEquals(CORE.TERM.HMAS_PLATFORM, hMASPlatform.getType());
    assertEquals(0, hMASPlatform.getHostedResources().size());
    assertEquals(0, hMASPlatform.getHMASPlatforms().size());
  }
}
