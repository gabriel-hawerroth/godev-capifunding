package capi.funding.api.infra.scheduling;

import capi.funding.api.security.SecurityFilter;
import capi.funding.api.services.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.verify;

@ActiveProfiles("default")
@ExtendWith(MockitoExtension.class)
public class ScheduleTest {

    @InjectMocks
    Schedule schedule;
    @Mock
    SecurityFilter securityFilter;
    @Mock
    ProjectService projectService;

    @Test
    @DisplayName("clearUsersCache - should clear cache")
    public void testClearUsersCacheShoulClearCache() {
        schedule.clearUsersCache();

        verify(securityFilter).clearUsersCache();
    }

    @Test
    @DisplayName("concludeAllProjectsEndingYesterdayNotCancelled - should call project service")
    public void testShouldCallProjectService() {
        schedule.concludeAllProjectsEndingYesterdayNotCancelled();

        verify(projectService).concludeAllProjectsEndingYesterdayNotCancelled();
    }
}
