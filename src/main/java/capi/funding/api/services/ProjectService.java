package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Project;
import capi.funding.api.models.User;
import capi.funding.api.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Project createNew(@Valid CreateProjectDTO dto) {
        final User user = utilsService.getAuthUser();

        final Project project = dto.toProject();

        project.setCreator_id(user.getId());
        project.setCreation_date(LocalDateTime.now());

        if (project.getInitial_date() == null) {
            project.setInitial_date(LocalDate.now());
        }

        return projectRepository.save(project);
    }

    public Project edit(long projectId, @Valid EditProjectDTO dto) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("project not found"));

        project.updateValues(dto);

        return projectRepository.save(project);
    }
}
