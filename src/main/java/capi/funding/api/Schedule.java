package capi.funding.api;

import capi.funding.api.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Schedule {

    private final SecurityFilter securityFilter;

    @Scheduled(cron = "0 30 3 * * *")
    public void clearUsersCache() {
        securityFilter.usersCache.clear();
    }
}
