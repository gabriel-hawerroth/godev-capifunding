package capi.funding.api.services;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Contribution;
import capi.funding.api.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final UtilsService utilsService;
    private final ProjectService projectService;

    private final ContributionRepository contributionRepository;

    public List<Contribution> getProjectContributions(long projectId) {
        if (!projectService.checkIfIdExists(projectId)) {
            throw new InvalidParametersException("project doenst exists");
        }

        return contributionRepository.getProjectContributions(projectId);
    }

    public Contribution getById(long id) {
        return contributionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("contribution not found"));
    }

    public Contribution createNew(CreateContributionDTO dto) {
        final Contribution contribution = dto.toContribution();

        contribution.setUser_id(
                utilsService.getAuthUser().getId()
        );

        contribution.setDate(
                LocalDateTime.now()
        );

        return contributionRepository.save(contribution);
    }
}
