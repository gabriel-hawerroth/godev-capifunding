package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectRepository;
import capi.funding.api.utils.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static capi.funding.api.utils.ProjectUtils.checkProjectEditability;

@Service
public class ProjectService {

    private static final String NOT_FOUND_MESSAGE = "project not found";

    private final Utils utils;
    private final ProjectMilestoneService milestoneService;

    private final ProjectRepository projectRepository;

    @Lazy
    public ProjectService(ProjectRepository projectRepository, ProjectMilestoneService milestoneService, Utils utils) {
        this.projectRepository = projectRepository;
        this.milestoneService = milestoneService;
        this.utils = utils;
    }

    public List<InterfacesSQL.ProjectsList> getProjectsList() {
        return projectRepository.getProjectsList();
    }

    public Project findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public Project createNew(CreateProjectDTO dto) {
        final long userId = utils.getAuthUser().getId();

        final Project project = dto.toProject();

        project.setCreator_id(userId);
        project.setCreation_date(LocalDateTime.now());

        if (project.getInitial_date() == null) {
            project.setInitial_date(LocalDate.now());
        }

        if (dto.need_to_follow_order() == null) {
            project.setNeed_to_follow_order(false);
        }

        return projectRepository.save(project);
    }

    public Project edit(long projectId, EditProjectDTO dto) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        project.updateValues(dto);

        if (project.isNeed_to_follow_order()) {
            final List<ProjectMilestone> completedMilestones = milestoneService.findByProjectAndCompleted(projectId);

            if (!completedMilestones.isEmpty()) {
                Integer previousSequence = null;

                for (ProjectMilestone milestone : completedMilestones) {
                    if (previousSequence != null && milestone.getSequence() < previousSequence) {
                        throw new MilestoneSequenceException("there are steps that have already been completed out of order");
                    }
                    previousSequence = milestone.getSequence();
                }
            }
        }

        return projectRepository.save(project);
    }

    public Project addCoverImage(long projectId, MultipartFile file) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        project.setCover_image(utils.checkImageValidityAndCompress(file));

        return projectRepository.save(project);
    }

    public Project removeCoverImage(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        project.setCover_image(null);

        return projectRepository.save(project);
    }

    public Project conclude(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        project.setStatus_id(ProjectStatusEnum.DONE.getValue());

        return projectRepository.save(project);
    }

    public Project cancel(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utils.checkPermission(project.getCreator_id());

        checkProjectEditability(project);

        project.setStatus_id(ProjectStatusEnum.CANCELED.getValue());

        return projectRepository.save(project);
    }

    public boolean checkIfExistsById(long projectId) {
        if (projectId < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return projectRepository.existsById(projectId);
    }

    public void concludeAllProjectsEndingYesterdayNotCancelled() {
        final List<Project> projects = projectRepository.findProjectsEndingYesterdayNotCancelled(
                LocalDate.now().minusDays(1)
        );

        projects.forEach((project -> project.setStatus_id(
                ProjectStatusEnum.DONE.getValue()
        )));

        projectRepository.saveAll(projects);
    }
}
