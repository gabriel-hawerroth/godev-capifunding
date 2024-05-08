package capi.funding.api.services;

import capi.funding.api.entity.ProjectSearchLog;
import capi.funding.api.repository.ProjectSearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectSearchLogService {

    private final ProjectSearchLogRepository repository;

    public void save(ProjectSearchLog searchLog) {
        repository.save(searchLog);
    }

    public void saveAll(List<ProjectSearchLog> searchLog) {
        repository.saveAll(searchLog);
    }
}
