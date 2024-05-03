package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectSpend;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectSpendRepository;
import capi.funding.api.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

import static capi.funding.api.utils.ProjectUtils.checkProjectEditability;

@Service
public class ProjectSpendService {

    private static final String NOT_FOUND_MESSAGE = "project spend not found";

    private final Utils utils;
    private final ProjectService projectService;

    private final ProjectSpendRepository repository;

    public ProjectSpendService(Utils utils, ProjectService projectService, ProjectSpendRepository repository) {
        this.utils = utils;
        this.projectService = projectService;
        this.repository = repository;
    }

    public List<ProjectSpend> findByProject(long projectId) {
        projectService.findById(projectId);

        return repository.findByProject(projectId);
    }

    public ProjectSpend findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    }

    public ProjectSpend createNew(CreateProjectSpendDTO dto) {
        final ProjectSpend projectSpend = dto.toProjectSpend();

        final Project project = projectService.findById(projectSpend.getProject_id());
        checkProjectEditability(project);

        return repository.save(projectSpend);
    }

    public ProjectSpend edit(long id, EditProjectSpendDTO dto) {
        final ProjectSpend projectSpend = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(projectSpend.getProject_id());
        utils.checkPermission(project.getCreator_id());
        checkProjectEditability(project);

        projectSpend.updateValues(dto);

        return repository.save(projectSpend);
    }

    public void delete(long id) {
        final ProjectSpend projectSpend = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        final Project project = projectService.findById(projectSpend.getProject_id());
        utils.checkPermission(project.getCreator_id());
        checkProjectEditability(project);

        repository.deleteById(id);
    }
}
