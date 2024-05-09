package capi.funding.api.services;

import capi.funding.api.dto.GeneralInfosReportDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectReportsServiceTest {

    @InjectMocks
    ProjectReportsService service;
    @Mock
    ProjectService projectService;
    @Mock
    ContributionService contributionService;

    @Test
    @DisplayName("getGeneralInfosReport - should return correct values")
    void testGetGeneralInfosReportShouldReturnCorrectValues() {
        final var generalInfos = assertDoesNotThrow(() ->
                service.getGeneralInfosReport());

        assertInstanceOf(GeneralInfosReportDTO.class, generalInfos);
    }

    @Test
    @DisplayName("getMostSearchedProjects - should fetch the database")
    void testGetMostSearchedProjectsShouldFetchTheDatabase() {
        final int pageNumber = 1;
        service.getMostSearchedProjects(pageNumber);

        verify(projectService).getMostSearchedProjects(pageNumber);
    }

    @Test
    @DisplayName("getTopDonatedProjects - should fetch the database")
    void testGetTopDonatedProjectsShouldFetchTheDatabase() {
        final int pageNumber = 1;
        service.getTopDonatedProjects(pageNumber);

        verify(projectService).getTopDonatedProjects(pageNumber);
    }

    @Test
    @DisplayName("getMostSearchedCategories - should fetch the database")
    void testGetMostSearchedCategoriesShouldFetchTheDatabase() {
        service.getMostSearchedCategories();

        verify(projectService).getMostSearchedCategories();
    }
}
