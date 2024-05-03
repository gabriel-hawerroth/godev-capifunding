package capi.funding.api;

import capi.funding.api.security.SecurityFilter;
import capi.funding.api.services.ProjectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedule {

    private final SecurityFilter securityFilter;

    private final ProjectService projectService;

    public Schedule(SecurityFilter securityFilter, ProjectService projectService) {
        this.securityFilter = securityFilter;
        this.projectService = projectService;
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void clearUsersCache() {
        securityFilter.usersCache.clear();
    }

    @Scheduled(cron = "0 1 0 * * *")
    public void concludeAllProjectsEndingYesterdayNotCancelled() {
        projectService.concludeAllProjectsEndingYesterdayNotCancelled();
    }
}
