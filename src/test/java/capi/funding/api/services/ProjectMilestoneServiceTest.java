package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.enums.ProjectCategoryEnum;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.DataIntegrityException;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectMilestoneRepository;
import capi.funding.api.utils.ProjectUtils;
import capi.funding.api.utils.Utils;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectMilestoneServiceTest {

    final long milestoneId = 1;
    final long projectId = 1;

    final CreateProjectMilestoneDTO createProjectMilestoneDTO = new CreateProjectMilestoneDTO(
            1,
            "project milestone title",
            "project milestone description",
            1,
            false,
            BigDecimal.valueOf(100)
    );

    final EditProjectMilestoneDTO editProjectMilestoneDTO = new EditProjectMilestoneDTO(
            "new title",
            "new description",
            2,
            null,
            null
    );

    final ProjectMilestone projectMilestone = new ProjectMilestone(
            1L,
            1,
            "milestone title",
            "mileston description",
            1,
            false,
            BigDecimal.valueOf(100)
    );

    final Project project = new Project(
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

    @InjectMocks
    ProjectMilestoneService milestoneService;
    @Mock
    Utils utils;
    @Mock
    ProjectUtils projectUtils;
    @Mock
    ProjectService projectService;
    @Mock
    ProjectMilestoneRepository repository;
    @Captor
    ArgumentCaptor<ProjectMilestone> milestoneCaptor;

    @Test
    @DisplayName("findByProject - should validate that the project exists")
    public void testShouldValidateThatTheProjectExists() {
        when(projectService.existsById(projectId)).thenReturn(false);

        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                milestoneService.findByProject(projectId));

        assertEquals("project doesn't exists", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("findByProject - should fetch milestones list from database")
    public void testShouldFetchMilestonesListFromDatabase() {
        when(projectService.existsById(projectId)).thenReturn(true);

        milestoneService.findByProject(projectId);

        verify(repository).findByProject(projectId);
    }

    @DisplayName("findById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    public void testFindByIdShouldAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                milestoneService.findById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when milestone is not found")
    public void testShouldThrowNotFoundExceptionWhenMilestoneIsNotFound() {
        when(repository.findById(projectId)).thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class, () ->
                milestoneService.findById(projectId));

        assertEquals("project milestone not found", ex.getMessage());
    }

    @Test
    @DisplayName("createNew - should validate milestone sequence number")
    public void testCreateNewShouldValidateMilestoneSequenceNumber() {
        milestoneService.createNew(createProjectMilestoneDTO);

        verify(projectUtils).validateMilestoneSequenceNumber(
                createProjectMilestoneDTO.sequence(),
                createProjectMilestoneDTO.toMilestone()
        );
    }

    @Test
    @DisplayName("createNew - should check project editability")
    public void testCreateNewShouldCheckProjectEditability() {
        final Project project = new Project();

        when(projectService.findById(createProjectMilestoneDTO.project_id())).thenReturn(project);

        milestoneService.createNew(createProjectMilestoneDTO);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("createNew - should validate need to follow order")
    public void testCreateNewShouldValidateNeedToFollowOrder() {
        final Project project = new Project();

        when(projectService.findById(createProjectMilestoneDTO.project_id())).thenReturn(project);

        milestoneService.createNew(createProjectMilestoneDTO);

        verify(projectUtils).validateNeedToFollowOrder(
                project,
                createProjectMilestoneDTO.toMilestone()
        );
    }

    @Test
    @DisplayName("createNew - should save the project milestone")
    public void testShouldSaveTheProjectMilestone() {
        milestoneService.createNew(createProjectMilestoneDTO);

        verify(repository).save(createProjectMilestoneDTO.toMilestone());
    }

    @Test
    @DisplayName("edit - should check user permission")
    public void testEditShouldCheckUserPermission() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.edit(milestoneId, editProjectMilestoneDTO);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("edit - should check project editability")
    public void testEditShouldCheckProjectEditability() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.edit(milestoneId, editProjectMilestoneDTO);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("edit - should validate milestone sequence number")
    public void testEditShouldValidateMilestoneSequenceNumber() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.edit(milestoneId, editProjectMilestoneDTO);

        verify(projectUtils).validateMilestoneSequenceNumber(
                projectMilestone.getSequence(),
                projectMilestone
        );
    }

    @Test
    @DisplayName("edit - should validate need to follow order")
    public void testEditShouldValidateNeedToFollowOrder() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.edit(milestoneId, editProjectMilestoneDTO);

        verify(projectUtils).validateNeedToFollowOrder(
                project,
                projectMilestone
        );
    }

    @Test
    @DisplayName("edit - should save the project milestone")
    public void testEditShouldSaveTheProjectMilestone() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.edit(milestoneId, editProjectMilestoneDTO);

        verify(repository).save(projectMilestone);
    }

    @Test
    @DisplayName("delete - should check user permission")
    public void testDeleteShouldCheckUserPermission() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.delete(milestoneId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("delete - should check project editability")
    public void testDeleteShouldCheckProjectEditability() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.delete(milestoneId);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("delete - should throw exception when has linked registers")
    public void testShouldThrowExceptionWhenHasLinkedRegisters() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(milestoneId);

        final DataIntegrityException exception = assertThrows(DataIntegrityException.class, () ->
                milestoneService.delete(milestoneId));

        assertEquals("this milestone of the project has a linked expense, impossible to exclude", exception.getMessage());
    }

    @Test
    @DisplayName("conclude - should check user permission")
    public void testConcludeShouldCheckUserPermission() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.conclude(milestoneId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("conclude - should validate need to follow order")
    public void testConcludeShouldValidateNeedToFollowOrder() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.conclude(milestoneId);

        verify(projectUtils).validateNeedToFollowOrder(
                project,
                projectMilestone
        );
    }

    @Test
    @DisplayName("conclude - should save completed milestone")
    public void testConcludeShouldSaveCompletedMilestone() {
        when(repository.findById(milestoneId)).thenReturn(Optional.of(projectMilestone));
        when(projectService.findById(projectId)).thenReturn(project);

        milestoneService.conclude(milestoneId);

        verify(repository).save(milestoneCaptor.capture());

        assertTrue(milestoneCaptor.getValue().isCompleted());
    }

    @Test
    @DisplayName("findLastProjectSequence - should fetch from database")
    public void testFindLastProjectSequenceShouldFetchFromDatabase() {
        milestoneService.findLastProjectSequence(projectId);

        verify(repository).findLastProjectSequence(projectId);
    }

    @Test
    @DisplayName("findByProjectAndSequence - should fetch from database")
    public void testFindByProjectAndSequenceShouldFetchFromDatabase() {
        milestoneService.findByProjectAndSequence(projectId, projectMilestone.getSequence(), milestoneId);

        verify(repository).findByProjectAndSequence(projectId, projectMilestone.getSequence(), milestoneId);
    }

    @Test
    @DisplayName("findByProjectAndMinorSequence - should fetch from database")
    public void testFindByProjectAndMinorSequenceShouldFetchFromDatabase() {
        milestoneService.findByProjectAndMinorSequence(projectId, projectMilestone.getSequence());

        verify(repository).findByProjectAndMinorSequence(projectId, projectMilestone.getSequence());
    }
}
