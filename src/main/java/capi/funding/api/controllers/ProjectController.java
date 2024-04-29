package capi.funding.api.controllers;

import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    private ResponseEntity<List<InterfacesSQL.ProjectsList>> getProjectsList() {
        return projectService.getProjectsList();
    }
}
