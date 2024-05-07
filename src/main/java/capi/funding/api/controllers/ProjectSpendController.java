package capi.funding.api.controllers;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.entity.ProjectSpend;
import capi.funding.api.services.ProjectSpendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project-spend")
public class ProjectSpendController {

    private final ProjectSpendService service;

    @GetMapping("/get-by-project/{id}")
    public ResponseEntity<List<ProjectSpend>> findByProject(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findByProject(id)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSpend> findById(@PathVariable long id) {
        return ResponseEntity.ok(
                service.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProjectSpend> createNew(@RequestBody @Valid CreateProjectSpendDTO dto) {
        final ProjectSpend projectSpend = service.createNew(dto);

        final URI uri = URI.create("/project-spend/" + projectSpend.getId());

        return ResponseEntity.created(uri).body(
                projectSpend
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectSpend> edit(@PathVariable long id, @RequestBody @Valid EditProjectSpendDTO dto) {
        return ResponseEntity.ok(
                service.edit(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
