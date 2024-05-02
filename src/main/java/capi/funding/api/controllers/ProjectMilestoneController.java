package capi.funding.api.controllers;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import capi.funding.api.models.ProjectMilestone;
import capi.funding.api.services.ProjectMilestoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/project-milestone")
public class ProjectMilestoneController {

    private final ProjectMilestoneService service;

    @Autowired
    public ProjectMilestoneController(ProjectMilestoneService service) {
        this.service = service;
    }

    @GetMapping("/get-by-project/{id}")
    private ResponseEntity<List<ProjectMilestone>> findByProject(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findByProject(id)
        );
    }

    @GetMapping("/{id}")
    private ResponseEntity<ProjectMilestone> getById(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findById(id)
        );
    }

    @PostMapping
    private ResponseEntity<ProjectMilestone> createNew(@RequestBody @Valid CreateProjectMilestoneDTO dto) {
        final ProjectMilestone projectMilestone = service.createNew(dto);

        final URI uri = URI.create("/project-milestone/" + projectMilestone.getId());

        return ResponseEntity.created(uri).body(projectMilestone);
    }

    @PutMapping("/{id}")
    private ResponseEntity<ProjectMilestone> edit(@PathVariable long id, @RequestBody @Valid EditProjectMilestoneDTO dto) {
        return ResponseEntity.ok(
                service.edit(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/conclude")
    private ResponseEntity<ProjectMilestone> conclude(@PathVariable long id) {
        return ResponseEntity.ok(
                service.conclude(id)
        );
    }
}
