package capi.funding.api.services;

import capi.funding.api.dto.CreateContributionDTO;
import capi.funding.api.entity.Contribution;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.User;
import capi.funding.api.enums.ProjectCategoryEnum;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ContributionRepository;
import capi.funding.api.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {

    private static final long projectId = 1;

    private Project project;
    private User user;

    @InjectMocks
    private ContributionService service;
    @Mock
    private Utils utils;
    @Mock
    private ProjectService projectService;
    @Mock
    private ContributionRepository repository;
    @Captor
    private ArgumentCaptor<Contribution> contributionCaptor;

    @BeforeEach
    void setUp() {
        project = new Project(
                1L,
                "test project",
                "project description",
                1,
                ProjectCategoryEnum.TECHNOLOGY.getValue(),
                ProjectStatusEnum.IN_PROGRESS.getValue(),
                false,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(20),
                null
        );

        user = new User(
                1L,
                "test@gmail.com",
                "encryptedPassword",
                "testing",
                true,
                LocalDateTime.now().minusDays(82),
                null
        );
    }

    @Test
    @DisplayName("findByProject - should validate that the project exists")
    void testShouldValidateThatTheProjectExists() {
        when(projectService.existsById(projectId)).thenReturn(false);

        final InvalidParametersException exception =
                assertThrows(InvalidParametersException.class, () ->
                        service.findByProject(projectId));

        assertEquals("project doesn't exists", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("findByProject - should fetch contributions list from database")
    void testShouldFetchMilestonesListFromDatabase() {
        when(projectService.existsById(projectId)).thenReturn(true);

        service.findByProject(projectId);

        verify(repository).findByProject(projectId);
    }

    @DisplayName("findById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    void testFindByIdShouldAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception =
                assertThrows(InvalidParametersException.class, () ->
                        service.findById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when contribution is not found")
    void testShouldThrowNotFoundExceptionWhenContributionIsNotFound() {
        when(repository.findById(projectId)).thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class, () ->
                service.findById(projectId));

        assertEquals("contribution not found", ex.getMessage());
    }

    @DisplayName("createNew - should validate project status")
    @ParameterizedTest
    @CsvSource({
            "PAUSED",
            "DONE",
            "CANCELED"
    })
    void testCreateNewShouldValidateProjectStatus(String projectStatus) {
        final CreateContributionDTO createContributionDTO = new CreateContributionDTO(
                1,
                BigDecimal.valueOf(150)
        );

        project.setStatus_id(ProjectStatusEnum.valueOf(projectStatus).getValue());

        when(projectService.findById(projectId)).thenReturn(project);

        final InvalidParametersException ex = assertThrows(InvalidParametersException.class, () ->
                service.createNew(createContributionDTO));

        assertEquals(
                "cannot contribute to a project with status 'paused', 'completed' or 'canceled'",
                ex.getMessage()
        );
    }

    @DisplayName("createNew - should validate minimal contribution value")
    @ParameterizedTest
    @CsvSource({
            "4.99",
            "0",
            "-150"
    })
    void testShouldValidateMinimalContributionValue(BigDecimal contributionValue) {
        final CreateContributionDTO contributionDTO = new CreateContributionDTO(
                1, contributionValue
        );

        when(projectService.findById(projectId)).thenReturn(project);

        final InvalidParametersException ex = assertThrows(InvalidParametersException.class, () ->
                service.createNew(contributionDTO));

        assertEquals("the minimum amount to contribute is $5", ex.getMessage());
    }

    @Test
    @DisplayName("createNew - should save the new contribution")
    void testShouldSaveTheNewContribution() {
        final BigDecimal contributionValue = BigDecimal.valueOf(5.0);
        final CreateContributionDTO createContributionDTO = new CreateContributionDTO(
                1, contributionValue
        );

        when(projectService.findById(projectId)).thenReturn(project);
        when(utils.getAuthUser()).thenReturn(user);

        service.createNew(createContributionDTO);

        verify(repository).save(contributionCaptor.capture());

        assertEquals(contributionValue, contributionCaptor.getValue().getValue());
    }

    @Test
    @DisplayName("createNew - should set project contributor with the auth user id")
    void testShouldSetProjectContributorWithTheAuthUserId() {
        final CreateContributionDTO createContributionDTO = new CreateContributionDTO(
                1, BigDecimal.valueOf(80)
        );

        final long userId = 10;
        user.setId(userId);

        when(projectService.findById(projectId)).thenReturn(project);
        when(utils.getAuthUser()).thenReturn(user);

        service.createNew(createContributionDTO);

        verify(utils).getAuthUser();
        verify(repository).save(contributionCaptor.capture());

        assertEquals(userId, contributionCaptor.getValue().getUser_id());
    }

    @Test
    @DisplayName("getTotalRaised - should fetch the database")
    void testGetTotalRaisedShouldFetchTheDatabase() {
        service.getTotalRaised();

        verify(repository).countTotalRaised();
    }

    @Test
    @DisplayName("countTotalContributions - should fetch the database")
    void testCountTotalContributionsShouldFetchTheDatabase() {
        service.countTotalContributions();

        verify(repository).count();
    }
}
