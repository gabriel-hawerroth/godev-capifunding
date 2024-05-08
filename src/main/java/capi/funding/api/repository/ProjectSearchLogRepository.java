package capi.funding.api.repository;

import capi.funding.api.entity.ProjectSearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSearchLogRepository extends JpaRepository<ProjectSearchLog, Long> {
}
