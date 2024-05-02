package capi.funding.api.services;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Contribution;
import capi.funding.api.models.Project;
import capi.funding.api.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContributionService {

    private final UtilsService utilsService;
    private final ProjectService projectService;

    private final ContributionRepository contributionRepository;

    @Autowired
    public ContributionService(UtilsService utilsService, ProjectService projectService, ContributionRepository contributionRepository) {
        this.utilsService = utilsService;
        this.projectService = projectService;
        this.contributionRepository = contributionRepository;
    }

    public List<Contribution> getProjectContributions(long projectId) {
        if (!projectService.checkIfExistsById(projectId)) {
            throw new InvalidParametersException("project doesnt exists");
        }

        return contributionRepository.getProjectContributions(projectId);
    }

    public Contribution getById(long id) {
        return contributionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("contribution not found"));
    }

    public Contribution createNew(CreateContributionDTO dto) {
        final Contribution contribution = dto.toContribution();

        final Project project = projectService.findById(contribution.getProject_id());
        validateProjectStatusForContribution(project.getStatus_id());

        contribution.setUser_id(
                utilsService.getAuthUser().getId()
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
