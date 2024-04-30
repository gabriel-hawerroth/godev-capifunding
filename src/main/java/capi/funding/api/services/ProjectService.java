package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Project;
import capi.funding.api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UtilsService utilsService;

    private final ProjectRepository projectRepository;

    public List<InterfacesSQL.ProjectsList> getProjectsList() {
        return projectRepository.getProjectsList();
    }

    public Project getById(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("project not found"));
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
                .orElseThrow(() -> new NotFoundException("project not found"));

        utilsService.checkPermission(project.getCreator_id());

        project.updateValues(dto);

        return projectRepository.save(project);
    }

    public Project addAttachment(long projectId, MultipartFile file) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project not found"));

        utilsService.checkPermission(project.getCreator_id());

        project.setCover_image(
                utilsService.checkImageValidityAndCompress(file)
        );

        return projectRepository.save(project);
    }

    public Project removeAttachment(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project not found"));

        utilsService.checkPermission(project.getCreator_id());

        project.setCover_image(null);

        return projectRepository.save(project);
    }

    public Project conclude(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project not found"));

        utilsService.checkPermission(project.getCreator_id());

        project.setStatus_id(ProjectStatusEnum.DONE.getValue());

        return projectRepository.save(project);
    }

    public Project cancel(long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project not found"));

        utilsService.checkPermission(project.getCreator_id());

        project.setStatus_id(ProjectStatusEnum.CANCELED.getValue());

        return projectRepository.save(project);
    }

    public boolean checkIfIdExists(long projectId) {
        return projectRepository.existsById(
                projectId
        );
    }
}
