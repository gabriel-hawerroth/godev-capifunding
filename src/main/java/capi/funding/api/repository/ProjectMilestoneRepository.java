package capi.funding.api.repository;

import capi.funding.api.models.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {
}
