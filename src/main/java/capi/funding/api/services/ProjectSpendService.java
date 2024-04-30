package capi.funding.api.services;

import capi.funding.api.repository.ProjectSpendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSpendService {

    private final ProjectSpendRepository repository;
}
