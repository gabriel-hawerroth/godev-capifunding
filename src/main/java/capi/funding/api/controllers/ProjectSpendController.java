package capi.funding.api.controllers;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.models.ProjectSpend;
import capi.funding.api.services.ProjectSpendService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/project-spend")
public class ProjectSpendController {

    private final ProjectSpendService service;

    @Autowired
    public ProjectSpendController(ProjectSpendService service) {
        this.service = service;
    }

    @GetMapping("/get-by-project/{id}")
    private ResponseEntity<List<ProjectSpend>> findByProject(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findByProject(id)
        );
    }

    @GetMapping("/{id}")
    private ResponseEntity<ProjectSpend> findById(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findById(id)
        );
    }

    @PostMapping
    private ResponseEntity<ProjectSpend> createNew(@RequestBody @Valid CreateProjectSpendDTO dto) {
        final ProjectSpend projectSpend = service.createNew(dto);

        final URI uri = URI.create("/project-spend/" + projectSpend.getId());

        return ResponseEntity.created(uri).body(
                projectSpend
        );
    }

    @PutMapping("/{id}")
    private ResponseEntity<ProjectSpend> edit(@PathVariable long id, @RequestBody @Valid EditProjectSpendDTO dto) {
        return ResponseEntity.ok(
                service.edit(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
