package capi.funding.api.services;

import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ResponseEntity<List<InterfacesSQL.ProjectsList>> getProjectsList() {
        return ResponseEntity.ok(
                projectRepository.getProjectsList()
        );
    }
}
