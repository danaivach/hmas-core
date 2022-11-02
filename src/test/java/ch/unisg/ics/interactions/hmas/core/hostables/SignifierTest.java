package ch.unisg.ics.interactions.hmas.core.hostables;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignifierTest {
/*
  @Test
  public void testSignifier() {
    BaseSignifier.Builder builder = new BaseSignifier.Builder();

    HypermediaMASPlatform[] platforms = {
      new HypermediaMASPlatform.Builder().build(),
      new HypermediaMASPlatform.Builder().build()
    };

    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    BaseSignifier signifier = builder.build();
    assertEquals(HMAS.SIGNIFIER, signifier.getType());
    assertEquals(HMAS.PREFIX + "Signifier", signifier.getTypeAsString());
    assertEquals(HMAS.SIGNIFIER.toIRI(), signifier.getTypeAsIRI());

    assertEquals(3, signifier.getHMASPlatforms().size());
    assertThrows(UnsupportedOperationException.class, () -> {
      signifier.getHMASPlatforms().add(null);
    });
  }

  @Test
  public void testSignifierDefault() {
    BaseSignifier signifier = new BaseSignifier.Builder()
      .build();

    assertEquals(HMAS.SIGNIFIER, signifier.getType());
    assertEquals(0, signifier.getHMASPlatforms().size());
  }
*/
}
