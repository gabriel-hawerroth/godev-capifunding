package capi.funding.api.utils;

import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.ProjectEditabilityException;
import capi.funding.api.services.ProjectMilestoneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectUtilsTest {

    final Map<String, String> NON_EDITABLE_STATUS_MESSAGE = Map.of(
            "DONE", "this project has already been concluded and cannot be edited",
            "CANCELED", "this project has already been cancelled and cannot be edited"
    );

    final ProjectMilestone milestone = new ProjectMilestone(
            1L,
            1,
            "milestone title",
            "mileston description",
            1,
            false,
            BigDecimal.valueOf(100)
    );

    @InjectMocks
    ProjectUtils projectUtils;
    @Mock
    ProjectMilestoneService milestoneService;

    @Test
    @DisplayName("checkProjectEditability - shouldn't accept null parameters")
    public void testShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                projectUtils.checkProjectEditability(null));
    }

    @DisplayName("checkProjectEditability - should throw exception with a non-editable project status")
    @ParameterizedTest
    @CsvSource({
            "DONE",
            "CANCELED"
    })
    public void testShouldThrowExceptionWithNonEditableProjectStatus(String projectStatus) {
        final Project project = new Project();
        project.setStatus_id(ProjectStatusEnum.valueOf(projectStatus).getValue());

        ProjectEditabilityException exception = assertThrows(ProjectEditabilityException.class, () ->
                projectUtils.checkProjectEditability(project));

        assertEquals(NON_EDITABLE_STATUS_MESSAGE.get(projectStatus), exception.getMessage());
    }

    @DisplayName("checkProjectEditability - should pass with a editable project status")
    @ParameterizedTest
    @CsvSource({
            "IN_PLANNING",
            "AWAITING_FUNDING",
            "IN_PROGRESS",
            "PAUSED",
            "IN_REVIEW"
    })
    public void testShouldPassWithEditableProjectStatus(String projectStatus) {
        final Project project = new Project();
        project.setStatus_id(ProjectStatusEnum.valueOf(projectStatus).getValue());

        assertDoesNotThrow(() -> projectUtils.checkProjectEditability(project));
    }

    @Test
    @DisplayName("validateMilestoneSequenceNumber - shouldn't accept null milestone")
    public void testValidateMilestoneSequenceNumberShouldntAcceptNullMilestone() {
        assertThrows(NullPointerException.class, () ->
                projectUtils.validateMilestoneSequenceNumber(2, null));
    }

    @Test
    @DisplayName("validateMilestoneSequenceNumber - should throw exception when a milestone with the same sequence and project already exists")
    public void testShouldThrowExceptionWhenAMilestoneWithTheSameSequenceAndProjectAlreadyExists() {
        when(milestoneService.findByProjectAndSequence(milestone.getProject_id(), milestone.getSequence(), milestone.getId()))
                .thenReturn(Optional.of(milestone));

        final InvalidParametersException ex =
                assertThrows(InvalidParametersException.class, () ->
                        projectUtils.validateMilestoneSequenceNumber(milestone.getSequence() + 1, milestone));

        assertEquals("this sequence number already exists in this project", ex.getMessage());
    }

    @Test
    @DisplayName("validateMilestoneSequenceNumber - should do nothing with an existing milestone and null sequence")
    public void testShouldDoNothingWithAnExistingMilestoneAndNullSequence() {
        assertDoesNotThrow(() -> projectUtils.validateMilestoneSequenceNumber(null, milestone));

        verifyNoInteractions(milestoneService);
    }

    @Test
    @DisplayName("validateMilestoneSequenceNumber - should call findLastProjectSequence from database")
    public void testShoudCallFindLastProjectSequenceFromDatabase() {
        milestone.setId(null);

        projectUtils.validateMilestoneSequenceNumber(null, milestone);

        verify(milestoneService).findLastProjectSequence(milestone.getProject_id());
    }

    @Test
    @DisplayName("validateMilestoneSequenceNumber - should be valid")
    public void testShouldBeValid() {
        when(milestoneService.findByProjectAndSequence(milestone.getProject_id(), milestone.getSequence(), milestone.getId()))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> projectUtils.validateMilestoneSequenceNumber(5, milestone));
    }

    @Test
    @DisplayName("validateNeedToFollowOrder - shouldn't accept null parameters")
    public void testValidateNeedToFollowOrderShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                projectUtils.validateNeedToFollowOrder(null, new ProjectMilestone()));

        assertThrows(NullPointerException.class, () ->
                projectUtils.validateNeedToFollowOrder(new Project(), null));
    }

    @Test
    @DisplayName("validateNeedToFollowOrder - should pass")
    public void testValidateNeedToFollowOrderShouldPass() {
        when(milestoneService.findByProjectAndMinorSequence(milestone.getProject_id(), milestone.getSequence()))
                .thenReturn(Collections.emptyList());

        final Project project = new Project();
        project.setNeed_to_follow_order(false);

        assertDoesNotThrow(() -> projectUtils.validateNeedToFollowOrder(project, milestone));
        verifyNoInteractions(milestoneService);

        project.setNeed_to_follow_order(true);
        milestone.setCompleted(false);

        assertDoesNotThrow(() -> projectUtils.validateNeedToFollowOrder(project, milestone));
        verifyNoInteractions(milestoneService);

        milestone.setCompleted(true);
        assertDoesNotThrow(() -> projectUtils.validateNeedToFollowOrder(project, milestone));
    }

    @Test
    @DisplayName("validateNeedToFollowOrder - should throw exception when project need to be completed in order")
    public void testValidateNeedToFollowOrderShouldThrowExceptionWhenProjectNeedToBeCompletedInOrder() {
        when(milestoneService.findByProjectAndMinorSequence(milestone.getProject_id(), milestone.getSequence()))
                .thenReturn(List.of(milestone, milestone));

        final Project project = new Project();
        project.setNeed_to_follow_order(true);

        milestone.setCompleted(true);

        final MilestoneSequenceException ex = assertThrows(MilestoneSequenceException.class, () ->
                projectUtils.validateNeedToFollowOrder(project, milestone));

        assertEquals("the milestones of this project need to be completed in the sequence", ex.getMessage());
    }
}