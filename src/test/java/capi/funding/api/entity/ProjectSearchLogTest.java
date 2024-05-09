package capi.funding.api.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectSearchLogTest {

    @Test
    @DisplayName("project_search_log - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectSearchLog());
    }

    @Test
    @DisplayName("project_search_log - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new ProjectSearchLog(
                1L,
                1L,
                "filter_name",
                "filter_value",
                LocalDateTime.now()
        ));
    }

    @Test
    @DisplayName("project_search_log - getters and setters")
    void testGettersAndSetters() {
        final LocalDateTime now = LocalDateTime.now();
        final ProjectSearchLog searchLog = new ProjectSearchLog();

        assertDoesNotThrow(() -> {
            searchLog.setId(1L);
            searchLog.setUser_id(90L);
            searchLog.setFilter_name("filter_name");
            searchLog.setFilter_value("filter_value");
            searchLog.setSearch_date(now);
        });

        assertAll("test getters",
                () -> assertEquals(1L, searchLog.getId()),
                () -> assertEquals(90L, searchLog.getUser_id()),
                () -> assertEquals("filter_name", searchLog.getFilter_name()),
                () -> assertEquals("filter_value", searchLog.getFilter_value()),
                () -> assertEquals(now, searchLog.getSearch_date())
        );
    }
}
