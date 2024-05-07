package capi.funding.api.services;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.entity.Contribution;
import capi.funding.api.entity.Project;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ContributionRepository;
import capi.funding.api.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final Utils utils;
    private final ProjectService projectService;

    private final ContributionRepository contributionRepository;

    public List<Contribution> findByProject(long projectId) {
        if (!projectService.existsById(projectId)) {
            throw new InvalidParametersException("project doesn't exists");
        }

        return contributionRepository.findByProject(projectId);
    }

    public Contribution findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return contributionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("contribution not found"));
    }

    public Contribution createNew(CreateContributionDTO dto) {
        final Contribution contribution = dto.toContribution();

        final Project project = projectService.findById(contribution.getProject_id());
        validateProjectStatusForContribution(project.getStatus_id());

        if (contribution.getValue().compareTo(BigDecimal.valueOf(5)) < 0) {
            throw new InvalidParametersException("the minimum amount to contribute is $5");
        }

        contribution.setUser_id(
                utils.getAuthUser().getId()
        );

        contribution.setDate(
                LocalDateTime.now()
        );

        return contributionRepository.save(contribution);
    }

    private void validateProjectStatusForContribution(long projectStatus) {
        if (
                projectStatus == ProjectStatusEnum.PAUSED.getValue()
                        || projectStatus == ProjectStatusEnum.DONE.getValue()
                        || projectStatus == ProjectStatusEnum.CANCELED.getValue()
        ) {
            throw new InvalidParametersException("cannot contribute to a project with status 'paused', 'completed' or 'canceled'");
        }
    }
}
