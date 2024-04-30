package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import capi.funding.api.exceptions.DataIntegrityException;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Project;
import capi.funding.api.models.ProjectMilestone;
import capi.funding.api.repository.ProjectMilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectMilestoneService {

    private final UtilsService utilsService;
    private final ProjectService projectService;

    private final ProjectMilestoneRepository repository;

    public List<ProjectMilestone> findByProject(long projectId) {
        return repository.findByProject(projectId);
    }

    public ProjectMilestone findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("project milestone not found"));
    }

    public ProjectMilestone createNew(CreateProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = dto.toMilestone();

        if (dto.completed() == null) {
            milestone.setCompleted(false);
        }

        if (dto.contribution_goal() == null) {
            milestone.setContribution_goal(BigDecimal.ZERO);
        }

        validitySequenceNumber(dto.sequence(), milestone);

        return repository.save(milestone);
    }

    public ProjectMilestone edit(long milestoneId, EditProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException("project milestone not found"));

        final Project project = projectService.getById(milestone.getProject_id());
        utilsService.checkPermission(project.getCreator_id());

        milestone.updateValues(dto);

        validitySequenceNumber(dto.sequence(), milestone);

        return repository.save(milestone);
    }

    public void delete(long milestoneId) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException("project milestone not found"));

        final Project project = projectService.getById(milestone.getProject_id());
        utilsService.checkPermission(project.getCreator_id());

        try {
            repository.deleteById(milestoneId);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityException("this milestone of the project has a linked expense, impossible to exclude");
        }
    }

    public ProjectMilestone conclude(long milestoneId) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException("project milestone not found"));

        final Project project = projectService.getById(milestone.getProject_id());
        utilsService.checkPermission(project.getCreator_id());

        milestone.setCompleted(true);

        return repository.save(milestone);
    }

    private void validitySequenceNumber(Integer sequence, ProjectMilestone milestone) {
        if (sequence == null) {
            milestone.setSequence(
                    repository.findLastProjectSequence(milestone.getProject_id())
                            .orElse(1)
            );
        } else {
            Optional<ProjectMilestone> milestoneOptional = repository.findByProjectAndSequence(
                    milestone.getProject_id(), milestone.getSequence()
            );

            if (milestoneOptional.isPresent()) {
                throw new InvalidParametersException("this sequence number already exists in this project");
            }
        }
    }
}
