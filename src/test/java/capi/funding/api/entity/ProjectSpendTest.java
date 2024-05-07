package capi.funding.api.entity;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProjectSpendTest {

    @Test
    @DisplayName("project spend - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectSpend());
    }

    @Test
    @DisplayName("project spend - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectSpend(
                1L,
                245,
                4205L,
                "spend description",
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(7)
        ));
    }

    @Test
    @DisplayName("project spend - CreateProjectSpendDTO constructor")
    void testCreateProjectSpendDTOConstructor() {
        final CreateProjectSpendDTO createSpendDTO = new CreateProjectSpendDTO(
                245,
                4205L,
                "spend description",
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(7)
        );

        assertDoesNotThrow(() -> new ProjectSpend(createSpendDTO));
    }

    @Test
    @DisplayName("project spend - getters and setters")
    void testGettersAndSetters() {
        final ProjectSpend spend = new ProjectSpend();

        assertDoesNotThrow(() -> {
            spend.setId(1L);
            spend.setProject_id(245);
            spend.setProject_milestone_id(4205L);
            spend.setDescription("spend description");
            spend.setValue(BigDecimal.valueOf(1000));
            spend.setDate(LocalDate.now().minusDays(7));
        });

        assertAll("test getters",
                () -> assertEquals(1L, spend.getId()),
                () -> assertEquals(245, spend.getProject_id()),
                () -> assertEquals(4205L, spend.getProject_milestone_id()),
                () -> assertEquals("spend description", spend.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(1000), spend.getValue()),
                () -> assertEquals(LocalDate.now().minusDays(7), spend.getDate())
        );
    }

    @Test
    @DisplayName("project spend - update values with null attributes")
    void testUpdateValuesWithNullAttributes() {
        final ProjectSpend spend = new ProjectSpend();
        spend.setDescription("spend description");

        final EditProjectSpendDTO editMilestoneDTO = new EditProjectSpendDTO(
                null,
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> spend.updateValues(editMilestoneDTO));
        assertEquals("spend description", spend.getDescription());
    }

    @Test
    @DisplayName("project spend - update values with blank attributes")
    void testUpdateValuesWithBlankAttributes() {
        final ProjectSpend spend = new ProjectSpend();
        spend.setDescription("spend description");

        final EditProjectSpendDTO editMilestoneDTO = new EditProjectSpendDTO(
                null,
                "",
                null,
                null
        );

        assertDoesNotThrow(() -> spend.updateValues(editMilestoneDTO));
        assertEquals("spend description", spend.getDescription());
    }

    @Test
    @DisplayName("project spend - update values with valid attributes")
    void testUpdateValuesWithValidAttributes() {
        final ProjectSpend spend = new ProjectSpend(
                1L,
                245,
                4205L,
                "spend description",
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(7)
        );

        final EditProjectSpendDTO editSpendDTO = new EditProjectSpendDTO(
                2000L,
                "new description",
                BigDecimal.valueOf(2000),
                LocalDate.now().minusDays(2)
        );

        assertDoesNotThrow(() -> spend.updateValues(editSpendDTO));

        assertAll("updated values",
                () -> assertEquals(2000L, spend.getProject_milestone_id()),
                () -> assertEquals("new description", spend.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(2000), spend.getValue()),
                () -> assertEquals(LocalDate.now().minusDays(2), spend.getDate())
        );
    }
}
