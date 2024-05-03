package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.infra.exceptions.DataIntegrityException;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectMilestoneRepository;
import capi.funding.api.utils.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static capi.funding.api.utils.ProjectUtils.checkProjectEditability;

@Service
public class ProjectMilestoneService {

    private static final String NOT_FOUND_MESSAGE = "project milestone not found";

    private final Utils utils;
    private final ProjectService projectService;

    private final ProjectMilestoneRepository repository;

    @Lazy
    public ProjectMilestoneService(Utils utils, ProjectService projectService, ProjectMilestoneRepository repository) {
        this.utils = utils;
        this.projectService = projectService;
        this.repository = repository;
    }

    public List<ProjectMilestone> findByProject(long projectId) {
        projectService.findById(projectId);

        return repository.findByProject(projectId);
    }

    public ProjectMilestone findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public ProjectMilestone createNew(CreateProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = dto.toMilestone();

        validateSequenceNumber(dto.sequence(), milestone);

        final Project project = projectService.findById(milestone.getProject_id());
        checkProjectEditability(project);
        validateNeedToFollowOrder(project, milestone);

        return repository.save(milestone);
    }

    public ProjectMilestone edit(long milestoneId, EditProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        milestone.updateValues(dto);

        validateSequenceNumber(dto.sequence(), milestone);
        validateNeedToFollowOrder(project, milestone);

        return repository.save(milestone);
    }

    public void delete(long milestoneId) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        try {
            repository.deleteById(milestoneId);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityException("this milestone of the project has a linked expense, impossible to exclude");
        }
    }

    public ProjectMilestone conclude(long milestoneId) {
        final ProjectMilestone milestone = repository.findById(milestoneId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        validateNeedToFollowOrder(project, milestone);

        milestone.setCompleted(true);

        return repository.save(milestone);
    }

    private void validateSequenceNumber(Integer sequence, ProjectMilestone milestone) {
        if (milestone.getId() != null && sequence == null) return;

        if (sequence == null) {
            milestone.setSequence(
                    repository.findLastProjectSequence(milestone.getProject_id())
                            .orElse(1)
            );
        } else {
            Optional<ProjectMilestone> milestoneOptional = repository.findByProjectAndSequence(
                    milestone.getProject_id(), milestone.getSequence(), milestone.getId()
            );

            if (milestoneOptional.isPresent()) {
                throw new InvalidParametersException("this sequence number already exists in this project");
            }
        }
    }

    private void validateNeedToFollowOrder(Project project, ProjectMilestone milestone) {
        if (project.isNeed_to_follow_order() && milestone.isCompleted()) {
            final List<ProjectMilestone> projectMilestoneList = repository
                    .findByProjectAndMinorSequence(milestone.getProject_id(), milestone.getSequence());

            if (!projectMilestoneList.isEmpty()) {
                throw new MilestoneSequenceException("the milestones of this project need to be completed in the sequence");
            }
        }
    }

    public List<ProjectMilestone> findByProjectAndCompleted(long projectId) {
        return repository.findByProjectAndCompleted(projectId);
    }
}
