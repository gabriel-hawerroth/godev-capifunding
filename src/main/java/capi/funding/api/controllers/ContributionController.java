package capi.funding.api.controllers;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.entity.Contribution;
import capi.funding.api.services.ContributionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contribution")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<List<Contribution>> findByProject(@PathVariable long id) {
        return ResponseEntity.ok(
                contributionService.findByProject(id)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contribution> findById(@PathVariable long id) {
        return ResponseEntity.ok(
                contributionService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Contribution> createNew(@RequestBody @Valid CreateContributionDTO dto) {
        final Contribution contribution = contributionService.createNew(dto);

        final URI uri = URI.create("/contribution/" + contribution.getId());

        return ResponseEntity.created(uri).body(
                contribution
        );
    }
}
