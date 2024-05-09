package capi.funding.api.services;

import capi.funding.api.dto.GeneralInfosReportDTO;
import capi.funding.api.dto.MostSearchedCategoriesDTO;
import capi.funding.api.dto.ProjectsListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                contributionService.countTotalContributions()
        );
    }

    public ProjectsListDTO getMostSearchedProjects(int pageNumber) {
        return projectService.getMostSearchedProjects(pageNumber);
    }

    public ProjectsListDTO getTopDonatedProjects(int pageNumber) {
        return projectService.getTopDonatedProjects(pageNumber);
    }

    public List<MostSearchedCategoriesDTO> getMostSearchedCategories() {
        return projectService.getMostSearchedCategories();
    }
}
