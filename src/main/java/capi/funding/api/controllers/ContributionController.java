package capi.funding.api.controllers;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.models.Contribution;
import capi.funding.api.services.ContributionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contribution")
public class ContributionController {

    private final ContributionService contributionService;

    @Autowired
    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @GetMapping("/project/{id}")
    private ResponseEntity<List<Contribution>> getProjectContributions(@PathVariable long id) {
        return ResponseEntity.ok(
                contributionService.getProjectContributions(id)
        );
    }

    @GetMapping("/{id}")
    private ResponseEntity<Contribution> getById(@PathVariable long id) {
        return ResponseEntity.ok(
                contributionService.getById(id)
        );
    }

    @PostMapping
    private ResponseEntity<Contribution> createNew(@RequestBody @Valid CreateContributionDTO dto) {
        final Contribution contribution = contributionService.createNew(dto);

        final URI uri = URI.create("/contribution/" + contribution.getId());

        return ResponseEntity.created(uri).body(
                contribution
        );
    }
}
