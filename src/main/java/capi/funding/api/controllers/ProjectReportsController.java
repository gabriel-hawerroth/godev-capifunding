package capi.funding.api.controllers;

import capi.funding.api.dto.GeneralInfosReportDTO;
import capi.funding.api.dto.MostSearchedCategoriesDTO;
import capi.funding.api.dto.ProjectsListDTO;
import capi.funding.api.services.ProjectReportsService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project-reports")
public class ProjectReportsController {

    private final ProjectReportsService service;

    @GetMapping("/generic-infos")
    public ResponseEntity<GeneralInfosReportDTO> getGeneralInfosReport() {
        return ResponseEntity.ok(
                service.getGeneralInfosReport()
        );
    }

    @GetMapping("/most-searched")
    public ResponseEntity<ProjectsListDTO> getMostSearchedProjects(
            @RequestParam(defaultValue = "1") @Positive int pageNumber
    ) {
        return ResponseEntity.ok(
                service.getMostSearchedProjects(pageNumber)
        );
    }

    @GetMapping("/top-donated")
    public ResponseEntity<ProjectsListDTO> getTopDonatedProjects(
            @RequestParam(defaultValue = "1") @Positive int pageNumber
    ) {
        return ResponseEntity.ok(
                service.getTopDonatedProjects(pageNumber)
        );
    }

    @GetMapping("/most-searched-categories")
    public ResponseEntity<List<MostSearchedCategoriesDTO>> getMostSearchedCategories() {
        return ResponseEntity.ok(
                service.getMostSearchedCategories()
        );
    }
}
