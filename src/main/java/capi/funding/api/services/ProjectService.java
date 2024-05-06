package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.ProjectsListDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectRepository;
import capi.funding.api.utils.ProjectUtils;
import capi.funding.api.utils.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final Utils utils;
    private final ProjectUtils projectUtils;
    private final ProjectMilestoneService milestoneService;

    private final ProjectRepository projectRepository;

    @Lazy
    public ProjectService(ProjectRepository projectRepository, ProjectMilestoneService milestoneService, Utils utils, ProjectUtils projectUtils) {
        this.projectRepository = projectRepository;
        this.milestoneService = milestoneService;
        this.utils = utils;
        this.projectUtils = projectUtils;
    }

    public List<ProjectsListDTO> getProjectsList() {
        return projectRepository.getProjectsList();
    }

    public Project findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("project not found"));
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
        final Project project = findById(projectId);

        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        project.updateValues(dto);

        if (project.isNeed_to_follow_order()) {
            final List<ProjectMilestone> completedMilestones = milestoneService.findByProject(projectId);

            if (!completedMilestones.isEmpty()) {
                for (int i = 0; i < completedMilestones.size(); i++) {
                    if (i != 0 && completedMilestones.get(i).isCompleted() && !completedMilestones.get(i - 1).isCompleted()) {
                        throw new MilestoneSequenceException("there are steps that have already been completed out of order");
                    }
                }
            }
        }

        return projectRepository.save(project);
    }

    public Project addCoverImage(long projectId, MultipartFile file) {
        final Project project = findById(projectId);

        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        project.setCover_image(utils.checkImageValidityAndCompress(file));

        return projectRepository.save(project);
    }

    public Project removeCoverImage(long projectId) {
        final Project project = findById(projectId);

        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        project.setCover_image(null);

        return projectRepository.save(project);
    }

    public Project conclude(long projectId) {
        final Project project = findById(projectId);

        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        project.setStatus_id(ProjectStatusEnum.DONE.getValue());

        return projectRepository.save(project);
    }

    public Project cancel(long projectId) {
        final Project project = findById(projectId);

        utils.checkPermission(project.getCreator_id());

        projectUtils.checkProjectEditability(project);

        project.setStatus_id(ProjectStatusEnum.CANCELED.getValue());

        return projectRepository.save(project);
    }

    public boolean existsById(long projectId) {
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
