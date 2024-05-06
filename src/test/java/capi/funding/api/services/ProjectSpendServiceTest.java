package capi.funding.api.services;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectSpend;
import capi.funding.api.enums.ProjectCategoryEnum;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.ProjectSpendRepository;
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
public class ProjectSpendServiceTest {

    final long spendId = 1;
    final long projectId = 1;

    final CreateProjectSpendDTO createProjectMilestoneDTO = new CreateProjectSpendDTO(
            1,
            1L,
            "spend description",
            BigDecimal.valueOf(2300),
            LocalDate.now().minusDays(12)
    );

    final EditProjectSpendDTO editProjectSpendDTO = new EditProjectSpendDTO(
            null,
            "new description",
            BigDecimal.valueOf(940),
            LocalDate.now().minusDays(4)
    );

    final ProjectSpend projectSpend = new ProjectSpend(
            1L,
            1,
            1L,
            "spend description",
            BigDecimal.valueOf(2000),
            LocalDate.now().minusDays(1)
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
    ProjectSpendService service;
    @Mock
    Utils utils;
    @Mock
    ProjectUtils projectUtils;
    @Mock
    ProjectService projectService;
    @Mock
    ProjectSpendRepository repository;
    @Captor
    ArgumentCaptor<ProjectSpend> spendCaptor;

    @Test
    @DisplayName("findByProject - should validate that the project exists")
    public void testShouldValidateThatTheProjectExists() {
        when(projectService.existsById(projectId)).thenReturn(false);

        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                service.findByProject(projectId));

        assertEquals("project doesn't exists", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("findByProject - should fetch spends list from database")
    public void testShouldFetchSpendsListFromDatabase() {
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
    public void testFindByIdShouldAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                service.findById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when milestone is not found")
    public void testShouldThrowNotFoundExceptionWhenMilestoneIsNotFound() {
        when(repository.findById(projectId)).thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class, () ->
                service.findById(projectId));

        assertEquals("project spend not found", ex.getMessage());
    }

    @Test
    @DisplayName("createNew - should check project editability")
    public void testCreateNewShouldCheckProjectEditability() {
        when(projectService.findById(projectId)).thenReturn(project);

        service.createNew(createProjectMilestoneDTO);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("createNew - should save the project spend")
    public void testCreateNewShouldSaveTheProjectSpend() {
        service.createNew(createProjectMilestoneDTO);

        verify(repository).save(any(ProjectSpend.class));
    }

    @Test
    @DisplayName("edit - should check user permission")
    public void testEditShouldCheckUserPermission() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.edit(spendId, editProjectSpendDTO);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("edit - should check project editability")
    public void testEditShouldCheckProjectEditability() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.edit(spendId, editProjectSpendDTO);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("edit - should save the updated spend")
    public void testEditShouldSaveTheUpdatedSpend() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.edit(spendId, editProjectSpendDTO);

        verify(repository).save(projectSpend);
        verify(repository).save(spendCaptor.capture());

        assertEquals("new description", spendCaptor.getValue().getDescription());
    }

    @Test
    @DisplayName("delete - should check user permission")
    public void testDeleteShouldCheckUserPermission() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.delete(spendId);

        verify(utils).checkPermission(project.getCreator_id());
    }

    @Test
    @DisplayName("delete - should check project editability")
    public void testDeleteShouldCheckProjectEditability() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.delete(spendId);

        verify(projectUtils).checkProjectEditability(project);
    }

    @Test
    @DisplayName("delete - should delete from database")
    public void testShouldDeleteFromDatabase() {
        when(repository.findById(spendId)).thenReturn(Optional.of(projectSpend));
        when(projectService.findById(projectSpend.getProject_id())).thenReturn(project);

        service.delete(spendId);

        verify(repository).deleteById(spendId);
    }
}
