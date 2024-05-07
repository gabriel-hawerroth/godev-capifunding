package capi.funding.api.infra.scheduling;

import capi.funding.api.security.SecurityFilter;
import capi.funding.api.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Schedule {

    private final SecurityFilter securityFilter;

    private final ProjectService projectService;

    @Scheduled(cron = "0 30 03 * * *") // every day at 03:30AM
    public void clearUsersCache() {
        securityFilter.clearUsersCache();
    }

    @Scheduled(cron = "0 01 0 * * *") // every day at 00:01AM
    public void concludeAllProjectsEndingYesterdayNotCancelled() {
        projectService.concludeAllProjectsEndingYesterdayNotCancelled();
    }
}
