package capi.funding.api.controllers;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.models.Project;
import capi.funding.api.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    private ResponseEntity<List<InterfacesSQL.ProjectsList>> getProjectsList() {
        return ResponseEntity.ok(
                projectService.getProjectsList()
        );
    }

    @GetMapping("/{id}")
    private ResponseEntity<Project> getById(@PathVariable long id) {
        return ResponseEntity.ok(
                projectService.getById(id)
        );
    }

    @PostMapping
    private ResponseEntity<Project> createNew(@RequestBody CreateProjectDTO createProjectDTO) {
        final Project savedProject = projectService.createNew(createProjectDTO);

        final URI uri = URI.create(
                "/project/" + savedProject.getId()
        );

        return ResponseEntity.created(uri).body(savedProject);
    }

    @PutMapping("/{id}")
    private ResponseEntity<Project> edit(@PathVariable long id, @RequestBody EditProjectDTO editProjectDTO) {
        return ResponseEntity.ok(
                projectService.edit(id, editProjectDTO)
        );
    }
}
