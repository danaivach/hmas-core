package ch.unisg.ics.interactions.hmas.core.hostables;

import ch.unisg.ics.interactions.hmas.core.vocabularies.HMAS;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkspaceTest {
  @Test
  public void testWorkspace() {
    Workspace.Builder builder = new Workspace.Builder();

    AbstractProfiledResource[] resources = {
      new Agent.Builder().build(),
      new Artifact.Builder().build()
    };

    HypermediaMASPlatform[] platforms = {
      new HypermediaMASPlatform.Builder().build(),
      new HypermediaMASPlatform.Builder().build()
    };

    builder.addContainedResources(new HashSet<>(Arrays.asList(resources)));
    builder.addContainedResource(new Agent.Builder().build());
    builder.addHMASPlatforms(new HashSet<>(Arrays.asList(platforms)));
    builder.addHMASPlatform(new HypermediaMASPlatform.Builder().build());

    Workspace workspace = builder.build();
    assertEquals(HMAS.WORKSPACE, workspace.getType());
    assertEquals(HMAS.PREFIX + "Workspace", workspace.getTypeAsString());
    assertEquals(HMAS.WORKSPACE.toIRI(), workspace.getTypeAsIRI());

    assertEquals(3, workspace.getContainedResources().size());
    assertEquals(3, workspace.getHMASPlatforms().size());

    assertThrows(UnsupportedOperationException.class, () -> {
      workspace.getContainedResources().add(null);
    });

    assertThrows(UnsupportedOperationException.class, () -> {
      workspace.getHMASPlatforms().add(null);
    });
  }

  @Test
  public void testWorkspaceDefault() {
    Workspace workspace = new Workspace.Builder()
      .build();

    assertEquals(0, workspace.getContainedResources().size());
    assertEquals(0, workspace.getHMASPlatforms().size());
  }
}
