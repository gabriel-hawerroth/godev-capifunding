package capi.funding.api.services;

import capi.funding.api.dto.GeneralInfosReportDTO;
import capi.funding.api.dto.ProjectsListDTO;
import capi.funding.api.entity.MostSearchedCategoriesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectReportsService {

    private final ProjectService projectService;
    private final ContributionService contributionService;

    public GeneralInfosReportDTO getGeneralInfosReport() {
        return new GeneralInfosReportDTO(
                projectService.countTotalProjects(),
                contributionService.getTotalRaised(),
                contributionService.getTotalContributions()
        );
    }

    public ProjectsListDTO getMostSearchedProjects() {
        return new ProjectsListDTO(
                1L,
                projectService.getByMostSearchedProjects()
        );
    }

    public ProjectsListDTO getTopDonatedProjects() {
        return null;
    }

    public List<MostSearchedCategoriesDTO> getMostSearchedCategories() {
        return Collections.emptyList();
    }
}
