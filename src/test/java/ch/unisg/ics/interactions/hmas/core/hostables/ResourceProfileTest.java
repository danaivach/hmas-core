package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceProfileTest {

  @Test
  public void testResourceProfile() {

    Agent agent = new Agent.Builder().build();

    ResourceProfile.Builder builder = new ResourceProfile.Builder(agent);

    BaseSignifier[] signifiers = {
      new BaseSignifier.Builder().build(),
      new BaseSignifier.Builder().build()
    };

    HypermediaMASPlatform[] platforms = {
      new HypermediaMASPlatform.Builder().build(),
      new HypermediaMASPlatform.Builder().build()
    };

    builder.exposeSignifiers(new HashSet<>(Arrays.asList(signifiers)));
    builder.exposeSignifier(new BaseSignifier.Builder().build());
    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    ResourceProfile resourceProfile = builder.build();
    assertEquals(HMAS.RESOURCE_PROFILE, resourceProfile.getType());
    assertEquals(HMAS.PREFIX + "ResourceProfile", resourceProfile.getTypeAsString());
    assertEquals(HMAS.RESOURCE_PROFILE.toIRI(), resourceProfile.getTypeAsIRI());

    assertEquals(agent, resourceProfile.getResource());
    assertEquals(3, resourceProfile.getExposedSignifiers().size());
    assertEquals(3, resourceProfile.getHMASPlatforms().size());

    assertThrows(UnsupportedOperationException.class, () -> {
      resourceProfile.getExposedSignifiers().add(null);
    });

    assertThrows(UnsupportedOperationException.class, () -> {
      resourceProfile.getHMASPlatforms().add(null);
    });
  }

  @Test
  public void testResourceProfileDefault() {

    Agent agent = new Agent.Builder().build();

    ResourceProfile resourceProfile = new ResourceProfile.Builder(agent)
      .build();

    assertEquals(HMAS.RESOURCE_PROFILE, resourceProfile.getType());
    assertEquals(agent, resourceProfile.getResource());
    assertEquals(0, resourceProfile.getExposedSignifiers().size());
    assertEquals(0, resourceProfile.getHMASPlatforms().size());
  }
}
