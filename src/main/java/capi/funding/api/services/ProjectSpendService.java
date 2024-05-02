package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.Project;
import capi.funding.api.models.ProjectSpend;
import capi.funding.api.repository.ProjectSpendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectSpendService {

    private static final String NOT_FOUND_MESSAGE = "project spend not found";

    private final UtilsService utilsService;
    private final ProjectService projectService;

    private final ProjectSpendRepository repository;

    @Autowired
    public ProjectSpendService(UtilsService utilsService, ProjectService projectService, ProjectSpendRepository repository) {
        this.utilsService = utilsService;
        this.projectService = projectService;
        this.repository = repository;
    }

    public List<ProjectSpend> findByProject(long projectId) {
        return repository.findByProject(projectId);
    }

    public ProjectSpend findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public ProjectSpend createNew(CreateProjectSpendDTO dto) {
        final ProjectSpend projectSpend = dto.toProjectSpend();

        return repository.save(projectSpend);
    }

    public ProjectSpend edit(long id, EditProjectSpendDTO dto) {
        final ProjectSpend projectSpend = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(projectSpend.getProject_id());
        utilsService.checkPermission(project.getCreator_id());

        projectSpend.updateValues(dto);

        return repository.save(projectSpend);
    }

    public void delete(long id) {
        final ProjectSpend projectSpend = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(projectSpend.getProject_id());
        utilsService.checkPermission(project.getCreator_id());

        repository.deleteById(id);
    }
}
