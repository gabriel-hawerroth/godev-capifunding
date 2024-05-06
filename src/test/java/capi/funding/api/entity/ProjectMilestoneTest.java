package capi.funding.api.entity;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectMilestoneTest {

    @Test
    @DisplayName("project milestone - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectMilestone());
    }

    @Test
    @DisplayName("project milestone - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectMilestone(
                1L,
                68,
                "milestone title",
                "milestone description",
                3,
                false,
                BigDecimal.valueOf(1000)
        ));
    }

    @Test
    @DisplayName("project milestone - createProjectMilestoneDTO constructor")
    void testCreateProjectMilestoneDTOConstructor() {
        final CreateProjectMilestoneDTO createMilestoneDTO = new CreateProjectMilestoneDTO(
                1,
                "title",
                "description",
                null,
                null,
                null
        );
        assertDoesNotThrow(() -> new ProjectMilestone(createMilestoneDTO));

        final CreateProjectMilestoneDTO createMilestoneDTO2 = new CreateProjectMilestoneDTO(
                1,
                "title",
                "description",
                5,
                true,
                BigDecimal.ZERO
        );
        assertDoesNotThrow(() -> new ProjectMilestone(createMilestoneDTO2));
    }

    @Test
    @DisplayName("project milestone - getters and setters")
    void testGetters() {
        final ProjectMilestone milestone = new ProjectMilestone();

        assertDoesNotThrow(() -> {
            milestone.setId(1L);
            milestone.setProject_id(100);
            milestone.setTitle("milestone title");
            milestone.setDescription("milestone description");
            milestone.setSequence(3);
            milestone.setCompleted(false);
            milestone.setContribution_goal(BigDecimal.valueOf(1000));
        });

        assertAll("test getters",
                () -> assertEquals(1L, milestone.getId()),
                () -> assertEquals(100, milestone.getProject_id()),
                () -> assertEquals("milestone title", milestone.getTitle()),
                () -> assertEquals("milestone description", milestone.getDescription()),
                () -> assertEquals(3, milestone.getSequence()),
                () -> assertFalse(milestone.isCompleted()),
                () -> assertEquals(BigDecimal.valueOf(1000), milestone.getContribution_goal())
        );
    }

    @Test
    @DisplayName("project milestone - update values with null attributes")
    void testUpdateValuesWithNullAttributes() {
        final ProjectMilestone milestone = new ProjectMilestone();
        milestone.setTitle("milestone title");

        final EditProjectMilestoneDTO editMilestoneDTO = new EditProjectMilestoneDTO(
                null,
                null,
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> milestone.updateValues(editMilestoneDTO));
        assertEquals("milestone title", milestone.getTitle());
    }

    @Test
    @DisplayName("project milestone - update values with blank attributes")
    void testUpdateValuesWithBlankAttributes() {
        final ProjectMilestone milestone = new ProjectMilestone();
        milestone.setTitle("milestone title");

        final EditProjectMilestoneDTO editMilestoneDTO = new EditProjectMilestoneDTO(
                "",
                "",
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> milestone.updateValues(editMilestoneDTO));
        assertEquals("milestone title", milestone.getTitle());
    }

    @Test
    @DisplayName("project milestone - update values with valid attributes")
    void testUpdateValuesWithValidAttributes() {
        final ProjectMilestone milestone = new ProjectMilestone();
        milestone.setTitle("milestone title");
        milestone.setDescription("milestone description");
        milestone.setContribution_goal(BigDecimal.TEN);
        milestone.setSequence(1);

        final EditProjectMilestoneDTO editMilestoneDTO = new EditProjectMilestoneDTO(
                "new title",
                "new description",
                5,
                true,
                BigDecimal.valueOf(200)
        );

        assertDoesNotThrow(() -> milestone.updateValues(editMilestoneDTO));
        assertEquals("new title", milestone.getTitle());
        assertEquals("new description", milestone.getDescription());
        assertEquals(5, milestone.getSequence());
        assertTrue(milestone.isCompleted());
        assertEquals(BigDecimal.valueOf(200), milestone.getContribution_goal());
    }
}
