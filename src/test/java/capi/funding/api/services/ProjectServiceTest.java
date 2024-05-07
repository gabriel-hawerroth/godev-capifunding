package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.entity.User;
import capi.funding.api.enums.ProjectCategoryEnum;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectRepository;
import capi.funding.api.utils.ProjectUtils;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private Project project;
    private EditProjectDTO editProjectDTO;
    private ProjectMilestone projectMilestone;
    private User user;

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private Utils utils;
    @Mock
    private ProjectUtils projectUtils;
    @Mock
    private ProjectMilestoneService milestoneService;
    @Mock
    private ProjectRepository projectRepository;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;
    @Captor
    private ArgumentCaptor<List<Project>> projectListCaptor;

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

        editProjectDTO = new EditProjectDTO(
                "new title",
                "new description",
                null,
                null,
                null,
                null
        );

        projectMilestone = new ProjectMilestone(
                1L,
                1,
                "project milestone title",
                "project milestone description",
                1,
                false,
                BigDecimal.valueOf(100)
        );

        user = new User(
                1L,
                "test@gmail.com",
                "Testing#01",
                "test",
                true,
                LocalDateTime.now().minusDays(40),
                null
        );
    }

    @Test
    @DisplayName("getProjectsList - should fetch projects list from database")
    void testShouldFetchProjectsListFromDatabase() {
        projectService.getProjectsList();

        verify(projectRepository).getProjectsList();
    }

    @DisplayName("findById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    void testFindByIdShoulAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                projectService.findById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when project is not found")
    void testShouldThrowNotFoundExceptionWhenProjectIsNotFound() {
        final long projectId = 1;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () ->
                projectService.findById(projectId));

        assertEquals("project not found", exception.getMessage());
    }

    @Test
    @DisplayName("createNew - if initial date is null should set to current date")
    void testIfInitialDateIsNullShouldSetToCurrentDate() {
        when(utils.getAuthUser()).thenReturn(user);

        final CreateProjectDTO createProjectDTO = new CreateProjectDTO(
                "project title",
                "project description",
                ProjectCategoryEnum.GAMES.getValue(),
                ProjectStatusEnum.IN_PLANNING.getValue(),
                false,
                null,
                LocalDate.now().plusDays(15)
        );

        projectService.createNew(createProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());

        assertEquals(LocalDate.now(), projectCaptor.getValue().getInitial_date());
    }

    @Test
    @DisplayName("createNew - if need_to_follow_order is null should set to false")
    void testIfNeedToFollowOrderIsNullShouldSetToFalse() {
        when(utils.getAuthUser()).thenReturn(user);

        final CreateProjectDTO createProjectDTO = new CreateProjectDTO(
                "project title",
                "project description",
                ProjectCategoryEnum.SPORTS.getValue(),
                ProjectStatusEnum.AWAITING_FUNDING.getValue(),
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(15)
        );

        projectService.createNew(createProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());

        assertFalse(projectCaptor.getValue().isNeed_to_follow_order());
    }

    @Test
    @DisplayName("createNew - should save the project")
    void testCreateNewShouldSaveTheProject() {
        when(utils.getAuthUser()).thenReturn(user);

        final CreateProjectDTO createProjectDTO = new CreateProjectDTO(
                "project title",
                "project description",
                ProjectCategoryEnum.NATURE.getValue(),
                ProjectStatusEnum.IN_REVIEW.getValue(),
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(15)
        );

        projectService.createNew(createProjectDTO);

        verify(projectRepository).save(projectCaptor.capture());

        assertNotNull(projectCaptor.getValue());
        assertEquals("project title", projectCaptor.getValue().getTitle());
    }

    @Test
    @DisplayName("edit - should check user permission")
    void testEditShouldCheckUserPermission() {
        final long projectId = 1;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.edit(projectId, editProjectDTO);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("edit - should save the project")
    void testEditShouldSaveTheProject() {
        final long projectId = 1;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.edit(projectId, editProjectDTO);

        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("edit - should do nothing when completed milestones is empty")
    void testShouldDoNothingWhenCompletedMilestonesIsEmpty() {
        final long projectId = 1;
        final EditProjectDTO editProjectDTO1 = new EditProjectDTO(
                "new title",
                "new description",
                null,
                null,
                true,
                null
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(milestoneService.findByProject(projectId)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> projectService.edit(projectId, editProjectDTO1));
    }

    @Test
    @DisplayName("edit - should check project editability")
    void testEditShouldCheckProjectEditability() {
        final long projectId = 1;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.edit(projectId, editProjectDTO);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("edit - should throw exception when setting need_to_follow_order to true and has milestones completed out of sequence")
    void testShouldThrowExceptionWhenSettingNeedToFollowOrderToTrueAndHasMilestonesCompletedOutOfSequence() {
        final long projectId = 1;

        final ProjectMilestone milestone2 = new ProjectMilestone(
                2L,
                1,
                "project milestone title",
                "project milestone description",
                2,
                true,
                BigDecimal.valueOf(100)
        );
        milestone2.setId(2L);
        milestone2.setSequence(2);
        milestone2.setCompleted(true);

        final EditProjectDTO editProjectDTO1 = new EditProjectDTO(
                "new title",
                "new description",
                null,
                null,
                true,
                null
        );

        final List<ProjectMilestone> projectMilestones = List.of(projectMilestone, milestone2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(milestoneService.findByProject(projectId)).thenReturn(projectMilestones);

        final MilestoneSequenceException exception = assertThrows(MilestoneSequenceException.class, () ->
                projectService.edit(projectId, editProjectDTO1));

        assertEquals("there are steps that have already been completed out of order", exception.getMessage());
    }

    @Test
    @DisplayName("edit - should pass when setting need_to_follow_order to true and doesn't has milestones completed out of sequence")
    void testShouldPassWhenSettingNeedToFollowOrderToTrueAndDoesntHasMilestonesCompletedOutOfSequence() {
        final long projectId = 1;

        projectMilestone.setCompleted(true);

        final ProjectMilestone milestone2 = new ProjectMilestone(
                2L,
                1,
                "project milestone title",
                "project milestone description",
                2,
                true,
                BigDecimal.valueOf(100)
        );
        milestone2.setId(2L);
        milestone2.setSequence(2);
        milestone2.setCompleted(true);

        final EditProjectDTO editProjectDTO1 = new EditProjectDTO(
                "new title",
                "new description",
                null,
                null,
                true,
                null
        );

        final List<ProjectMilestone> projectMilestones = List.of(projectMilestone, milestone2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(milestoneService.findByProject(projectId)).thenReturn(projectMilestones);

        assertDoesNotThrow(() -> projectService.edit(projectId, editProjectDTO1));

        projectMilestone.setCompleted(false);
        milestone2.setCompleted(false);

        assertDoesNotThrow(() -> projectService.edit(projectId, editProjectDTO1));
    }

    @Test
    @DisplayName("addCoverImage - should check user permission")
    void testAddCoverImageShouldCheckUserPermission() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.addCoverImage(projectId, mock(MockMultipartFile.class));

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("addCoverImage - should check project editability")
    void testAddCoverImageShouldCheckProjectEditability() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.addCoverImage(projectId, mock(MockMultipartFile.class));

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("addCoverImage - should compress the image")
    void testAddCoverImageShouldCompressTheImage() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        final var mockFile = mock(MockMultipartFile.class);

        projectService.addCoverImage(projectId, mockFile);

        verify(utils).checkImageValidityAndCompress(mockFile);
    }

    @Test
    @DisplayName("addCoverImage - should save the project")
    void testAddCoverImageShouldSaveTheProject() {
        final long projectId = 1;

        final var mockFile = mock(MockMultipartFile.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(utils.checkImageValidityAndCompress(mockFile)).thenReturn(new byte[100000]);

        projectService.addCoverImage(projectId, mockFile);

        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("removeCoverImage - should check user permission")
    void testRemoveCoverImageShouldCheckUserPermission() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.removeCoverImage(projectId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("removeCoverImage - should check project editability")
    void testRemoveCoverImageShouldCheckProjectEditability() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.removeCoverImage(projectId);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("removeCoverImage - should set project cover_image to null and save")
    void testShouldSetProjectCoverImageToNullAndSave() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.removeCoverImage(projectId);

        verify(projectRepository).save(projectCaptor.capture());

        assertNull(projectCaptor.getValue().getCover_image());
    }

    @Test
    @DisplayName("conclude - should check user permission")
    void testConcludeShouldCheckUserPermission() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.conclude(projectId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("conclude - should check project editability")
    void testConcludeShouldCheckProjectEditability() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.conclude(projectId);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("conclude - should set status to done and save")
    void testShouldSetStatusToDoneAndSave() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.conclude(projectId);

        verify(projectRepository).save(projectCaptor.capture());

        assertEquals(
                (long) ProjectStatusEnum.DONE.getValue(),
                projectCaptor.getValue().getStatus_id()
        );
    }

    @Test
    @DisplayName("cancel - should check user permission")
    void testCancelShouldCheckUserPermission() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.cancel(projectId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("cancel - should check project editability")
    void testCancelShouldCheckProjectEditability() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.cancel(projectId);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("cancel - should set status to canceled and save")
    void testShouldSetStatusToCanceledAndSave() {
        final long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.cancel(projectId);

        verify(projectRepository).save(projectCaptor.capture());

        assertEquals(
                (long) ProjectStatusEnum.CANCELED.getValue(),
                projectCaptor.getValue().getStatus_id()
        );
    }

    @DisplayName("checkIfExistsById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    void testExistsByIdAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                projectService.existsById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("checkIfExistsById - should fetch exists by id from database")
    void testExistsByIdFromDatabase() {
        final long id = 1;

        projectService.existsById(id);

        verify(projectRepository).existsById(id);
    }

    @Test
    @DisplayName("concludeAllProjectsEndingYesterdayNotCancelled - should conclude the projects")
    void testConcludeAllProjectsEndingYesterdayNotCancelledShouldConcludeTheProjects() {
        project.setStatus_id(ProjectStatusEnum.IN_PROGRESS.getValue());
        final List<Project> projects = List.of(project);

        when(projectRepository.findProjectsEndingYesterdayNotCancelled(any(LocalDate.class))).thenReturn(projects);

        projectService.concludeAllProjectsEndingYesterdayNotCancelled();

        verify(projectRepository).saveAll(projectListCaptor.capture());

        for (Project project1 : projectListCaptor.getValue()) {
            assertEquals(
                    (long) ProjectStatusEnum.DONE.getValue(),
                    project1.getStatus_id()
            );
        }
    }
}
