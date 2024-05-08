package capi.funding.api.controllers;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.dto.ProjectsListDTO;
import capi.funding.api.dto.ProjectsListFiltersDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.services.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ProjectsListDTO> getProjectsList(
            @RequestParam(required = false, defaultValue = "") String projectTitle,
            @RequestParam(required = false) List<Integer> projectCategory,
            @RequestParam(required = false) List<Integer> projectStatus,
            @RequestParam(required = false, defaultValue = "") String creatorName,
            @RequestParam(required = false, defaultValue = "1") @Positive Long pageNumber
    ) {
        final ProjectsListFiltersDTO filtersDTO = new ProjectsListFiltersDTO(
                projectTitle,
                projectCategory,
                projectStatus,
                creatorName,
                pageNumber,
                10L
        );

        return ResponseEntity.ok(
                projectService.getProjectsList(filtersDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(@PathVariable long id) {
        return ResponseEntity.ok(
                projectService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Project> createNew(@RequestBody @Valid CreateProjectDTO createProjectDTO) {
        final Project savedProject = projectService.createNew(createProjectDTO);

        final URI uri = URI.create(
                "/project/" + savedProject.getId()
        );

        return ResponseEntity.created(uri).body(savedProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> edit(@PathVariable long id, @RequestBody @Valid EditProjectDTO editProjectDTO) {
        return ResponseEntity.ok(
                projectService.edit(id, editProjectDTO)
        );
    }

    @PatchMapping("/{id}/add-cover-image")
    public ResponseEntity<Project> addCoverImage(@PathVariable long id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(
                projectService.addCoverImage(id, file)
        );
    }

    @PatchMapping("/{id}/remove-cover-image")
    public ResponseEntity<Project> removeCoverImage(@PathVariable long id) {
        return ResponseEntity.ok(
                projectService.removeCoverImage(id)
        );
    }

    @PatchMapping("/{id}/conclude")
    public ResponseEntity<Project> conclude(@PathVariable long id) {
        return ResponseEntity.ok(
                projectService.conclude(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Project> cancel(@PathVariable long id) {
        return ResponseEntity.ok(
                projectService.cancel(id)
        );
    }
}
