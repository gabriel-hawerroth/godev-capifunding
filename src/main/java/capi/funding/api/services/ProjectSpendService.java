package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectSpend;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectSpendRepository;
import capi.funding.api.utils.ProjectUtils;
import capi.funding.api.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectSpendService {

    private final Utils utils;
    private final ProjectUtils projectUtils;
    private final ProjectService projectService;

    private final ProjectSpendRepository repository;

    public ProjectSpendService(Utils utils, ProjectUtils projectUtils, ProjectService projectService, ProjectSpendRepository repository) {
        this.utils = utils;
        this.projectUtils = projectUtils;
        this.projectService = projectService;
        this.repository = repository;
    }

    public List<ProjectSpend> findByProject(long projectId) {
        projectService.findById(projectId);

        return repository.findByProject(projectId);
    }

    public ProjectSpend findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("project spend not found"));
    }

    public ProjectSpend createNew(CreateProjectSpendDTO dto) {
        final ProjectSpend projectSpend = dto.toProjectSpend();

        final Project project = projectService.findById(projectSpend.getProject_id());
        projectUtils.checkProjectEditability(project);

        return repository.save(projectSpend);
    }

    public ProjectSpend edit(long id, EditProjectSpendDTO dto) {
        final ProjectSpend projectSpend = findById(id);

        final Project project = projectService.findById(projectSpend.getProject_id());
        utils.checkPermission(project.getCreator_id());
        projectUtils.checkProjectEditability(project);

        projectSpend.updateValues(dto);

        return repository.save(projectSpend);
    }

    public void delete(long id) {
        final ProjectSpend projectSpend = findById(id);

        final Project project = projectService.findById(projectSpend.getProject_id());
        utils.checkPermission(project.getCreator_id());
        projectUtils.checkProjectEditability(project);

        repository.deleteById(id);
    }
}
