package capi.funding.api.entity;

import capi.funding.api.dto.EditProjectDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectTest {

    @Test
    @DisplayName("project - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new Project());
    }

    @Test
    @DisplayName("project - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new Project(
                1L,
                "project title",
                "project description",
                2138,
                4,
                3,
                false,
                LocalDateTime.now().minusDays(10),
                LocalDate.now().minusDays(8),
                LocalDate.now().plusDays(5),
                new byte[1830239]
        ));
    }

    @Test
    @DisplayName("project - getters and setters")
    void testGettersAndSetters() {
        final LocalDateTime now = LocalDateTime.now();
        final byte[] cover_image = new byte[1830239];
        final Project project = new Project();

        assertDoesNotThrow(() -> {
            project.setId(1L);
            project.setTitle("project title");
            project.setDescription("project description");
            project.setCreator_id(2138);
            project.setCategory_id(4);
            project.setStatus_id(3);
            project.setNeed_to_follow_order(false);
            project.setCreation_date(now.minusDays(10));
            project.setInitial_date(LocalDate.now().minusDays(8));
            project.setFinal_date(LocalDate.now().plusDays(5));
            project.setCover_image(cover_image);
        });

        assertAll("test getters",
                () -> assertEquals(1L, project.getId()),
                () -> assertEquals("project title", project.getTitle()),
                () -> assertEquals("project description", project.getDescription()),
                () -> assertEquals(2138, project.getCreator_id()),
                () -> assertEquals(4, project.getCategory_id()),
                () -> assertEquals(3, project.getStatus_id()),
                () -> assertFalse(project.isNeed_to_follow_order()),
                () -> assertEquals(now.minusDays(10), project.getCreation_date()),
                () -> assertEquals(LocalDate.now().minusDays(8), project.getInitial_date()),
                () -> assertEquals(LocalDate.now().plusDays(5), project.getFinal_date()),
                () -> assertEquals(cover_image, project.getCover_image())
        );
    }

    @Test
    @DisplayName("project - update values with null attributes")
    void testUpdateValuesWithNullAttributes() {
        final Project project = new Project();
        project.setTitle("project title");

        final EditProjectDTO editProjectDTO = new EditProjectDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> project.updateValues(editProjectDTO));
        assertEquals("project title", project.getTitle());
    }

    @Test
    @DisplayName("project - update values with blank attributes")
    void testUpdateValuesWithBlankAttributes() {
        final Project project = new Project();
        project.setTitle("project title");
        project.setDescription("description");

        final EditProjectDTO editProjectDTO = new EditProjectDTO(
                "",
                "",
                null,
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> project.updateValues(editProjectDTO));
        assertEquals("project title", project.getTitle());
        assertEquals("description", project.getDescription());
    }

    @Test
    @DisplayName("project - update values with valid attributes")
    void testUpdateValuesWithValidAttributes() {
        final Project project = new Project(
                1L,
                "project title",
                "project description",
                2138,
                4,
                3,
                false,
                LocalDateTime.now().minusDays(10),
                LocalDate.now().minusDays(8),
                LocalDate.now().plusDays(5),
                null
        );

        final EditProjectDTO editProjectDTO = new EditProjectDTO(
                "new title",
                "new description",
                1L,
                3L,
                false,
                LocalDate.now().plusDays(2)
        );

        assertDoesNotThrow(() -> project.updateValues(editProjectDTO));

        assertAll("updated project values",
                () -> assertEquals("new title", project.getTitle()),
                () -> assertEquals("new description", project.getDescription()),
                () -> assertEquals(1L, project.getCategory_id()),
                () -> assertEquals(3L, project.getStatus_id()),
                () -> assertFalse(project.isNeed_to_follow_order()),
                () -> assertEquals(LocalDate.now().plusDays(2), project.getFinal_date())
        );
    }
}
