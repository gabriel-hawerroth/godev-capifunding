package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.infra.exceptions.DataIntegrityException;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectMilestoneRepository;
import capi.funding.api.utils.ProjectUtils;
import capi.funding.api.utils.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectMilestoneService {

    private final Utils utils;
    private final ProjectUtils projectUtils;
    private final ProjectService projectService;

    private final ProjectMilestoneRepository repository;

    @Lazy
    public ProjectMilestoneService(Utils utils, ProjectUtils projectUtils, ProjectService projectService, ProjectMilestoneRepository repository) {
        this.utils = utils;
        this.projectUtils = projectUtils;
        this.projectService = projectService;
        this.repository = repository;
    }

    public List<ProjectMilestone> findByProject(long projectId) {
        if (!projectService.existsById(projectId)) {
            throw new InvalidParametersException("project doesn't exists");
        }

        return repository.findByProject(projectId);
    }

    public ProjectMilestone findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("project milestone not found"));
    }

    public ProjectMilestone createNew(CreateProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = dto.toMilestone();

        projectUtils.validateMilestoneSequenceNumber(dto.sequence(), milestone);

        final Project project = projectService.findById(milestone.getProject_id());
        projectUtils.checkProjectEditability(project);
        projectUtils.validateNeedToFollowOrder(project, milestone);

        return repository.save(milestone);
    }

    public ProjectMilestone edit(long milestoneId, EditProjectMilestoneDTO dto) {
        final ProjectMilestone milestone = findById(milestoneId);

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        milestone.updateValues(dto);

        projectUtils.validateMilestoneSequenceNumber(dto.sequence(), milestone);
        projectUtils.validateNeedToFollowOrder(project, milestone);

        return repository.save(milestone);
    }

    public void delete(long milestoneId) {
        final ProjectMilestone milestone = findById(milestoneId);

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        try {
            repository.deleteById(milestoneId);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityException("this milestone of the project has a linked expense, impossible to exclude");
        }
    }

    public ProjectMilestone conclude(long milestoneId) {
        final ProjectMilestone milestone = findById(milestoneId);

        final Project project = projectService.findById(milestone.getProject_id());
        utils.checkPermission(project.getCreator_id());

        projectUtils.validateNeedToFollowOrder(project, milestone);

        milestone.setCompleted(true);

        return repository.save(milestone);
    }

    public Optional<Integer> findLastProjectSequence(long projectId) {
        return repository.findLastProjectSequence(projectId);
    }

    public Optional<ProjectMilestone> findByProjectAndSequence(long projectId, int sequence, Long id) {
        return repository.findByProjectAndSequence(projectId, sequence, id);
    }

    public List<ProjectMilestone> findByProjectAndMinorSequence(long projectId, int sequence) {
        return repository.findByProjectAndMinorSequence(projectId, sequence);
    }
}
