package capi.funding.api.services;

import capi.funding.api.entity.ProjectSearchLog;
import capi.funding.api.enums.ProjectSearchFields;
import capi.funding.api.repository.ProjectSearchLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectSearchLogServiceTest {

    private ProjectSearchLog searchLog;

    @InjectMocks
    private ProjectSearchLogService service;
    @Mock
    private ProjectSearchLogRepository repository;

    @BeforeEach
    void setUp() {
        searchLog = new ProjectSearchLog(
                null,
                1L,
                ProjectSearchFields.PROJECT_TITLE.getValue(),
                "project title",
                LocalDateTime.now().minusDays(5)
        );
    }

    @Test
    @DisplayName("save - should call jpa save")
    void testSaveShouldCallJpaSave() {
        service.save(searchLog);

        verify(repository).save(searchLog);
    }

    @Test
    @DisplayName("saveAll - should call jpa save all")
    void testSaveAllShouldCallJpaSaveAll() {
        final List<ProjectSearchLog> searchLogs = List.of(searchLog);

        service.saveAll(searchLogs);

        verify(repository).saveAll(searchLogs);
    }
}
