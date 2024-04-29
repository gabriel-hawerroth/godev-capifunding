package capi.funding.api.repository;

import capi.funding.api.models.ProjectSpend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSpendRepository extends JpaRepository<ProjectSpend, Long> {
}
