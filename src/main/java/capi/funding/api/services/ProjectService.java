package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.NeedToFollowOrderException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Project;
import capi.funding.api.models.ProjectMilestone;
import capi.funding.api.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private static final String NOT_FOUND_MESSAGE = "project not found";

    private final UtilsService utilsService;
    private final ProjectMilestoneService milestoneService;

    private final ProjectRepository projectRepository;

    @Lazy
    @Autowired
    public ProjectService(ProjectRepository projectRepository, ProjectMilestoneService milestoneService, UtilsService utilsService) {
        this.projectRepository = projectRepository;
        this.milestoneService = milestoneService;
        this.utilsService = utilsService;
    }

    public List<InterfacesSQL.ProjectsList> getProjectsList() {
        return projectRepository.getProjectsList();
    }

    public Project findById(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public Project createNew(CreateProjectDTO dto) {
        final long userId = utilsService.getAuthUser().getId();

        final Project project = dto.toProject();

        project.setCreator_id(userId);
        project.setCreation_date(LocalDateTime.now());

        if (project.getInitial_date() == null) {
            project.setInitial_date(LocalDate.now());
        }

        return projectRepository.save(project);
    }

    public Project edit(long projectId, EditProjectDTO dto) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utilsService.checkPermission(project.getCreator_id());

        checkProjectEditability(project.getStatus_id());

        project.updateValues(dto);

        if (project.isNeed_to_follow_order()) {
            final List<ProjectMilestone> completedMilestones = milestoneService.findByProjectAndCompleted(projectId);

            if (!completedMilestones.isEmpty()) {
                Integer previousSequence = null;

                for (ProjectMilestone milestone : completedMilestones) {
                    if (previousSequence != null && milestone.getSequence() < previousSequence) {
                        throw new NeedToFollowOrderException("there are steps that have already been completed out of order");
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

        utilsService.checkPermission(project.getCreator_id());

        checkProjectEditability(project.getStatus_id());

        project.setCover_image(utilsService.checkImageValidityAndCompress(file));

        return projectRepository.save(project);
    }

    public Project removeCoverImage(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utilsService.checkPermission(project.getCreator_id());

        checkProjectEditability(project.getStatus_id());

        project.setCover_image(null);

        return projectRepository.save(project);
    }

    public Project conclude(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utilsService.checkPermission(project.getCreator_id());

        checkProjectEditability(project.getStatus_id());

        project.setStatus_id(ProjectStatusEnum.DONE.getValue());

        return projectRepository.save(project);
    }

    public Project cancel(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        utilsService.checkPermission(project.getCreator_id());

        checkProjectEditability(project.getStatus_id());

        project.setStatus_id(ProjectStatusEnum.CANCELED.getValue());

        return projectRepository.save(project);
    }

    public boolean checkIfExistsById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public void checkProjectEditability(long projectStatusId) {
        if (projectStatusId == ProjectStatusEnum.DONE.getValue()) {
            throw new InvalidParametersException("this project has already been concluded and cannot be edited");
        } else if (projectStatusId == ProjectStatusEnum.CANCELED.getValue()) {
            throw new InvalidParametersException("this project has already been cancelled and cannot be edited");
        }
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
