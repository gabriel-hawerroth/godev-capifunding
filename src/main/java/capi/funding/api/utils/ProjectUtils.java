package capi.funding.api.utils;

import capi.funding.api.dto.ProjectsListFiltersDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.ProjectEditabilityException;
import capi.funding.api.services.ProjectMilestoneService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectUtils {

    private final ProjectMilestoneService projectMilestoneService;

    public void checkProjectEditability(@NonNull Project project) {
        final long projectStatusId = project.getStatus_id();

        if (projectStatusId == ProjectStatusEnum.DONE.getValue()) {
            throw new ProjectEditabilityException("this project has already been concluded and cannot be edited");
        } else if (projectStatusId == ProjectStatusEnum.CANCELED.getValue()) {
            throw new ProjectEditabilityException("this project has already been cancelled and cannot be edited");
        }
    }

    public void validateMilestoneSequenceNumber(Integer sequence, @NonNull ProjectMilestone milestone) {
        if (sequence == null && milestone.getId() != null) return;

        if (sequence == null) {
            milestone.setSequence(
                    projectMilestoneService.findLastProjectSequence(milestone.getProject_id())
                            .orElse(1)
            );
        } else {
            Optional<ProjectMilestone> milestoneOptional = projectMilestoneService.findByProjectAndSequence(
                    milestone.getProject_id(), milestone.getSequence(), milestone.getId()
            );

            if (milestoneOptional.isPresent()) {
                throw new InvalidParametersException("this sequence number already exists in this project");
            }
        }
    }

    public void validateNeedToFollowOrder(@NonNull Project project, @NonNull ProjectMilestone milestone) {
        if (project.isNeed_to_follow_order() && milestone.isCompleted()) {
            final List<ProjectMilestone> projectMilestoneList = projectMilestoneService
                    .findByProjectAndMinorSequence(milestone.getProject_id(), milestone.getSequence());

            if (!projectMilestoneList.isEmpty()) {
                throw new MilestoneSequenceException("the milestones of this project need to be completed in the sequence");
            }
        }
    }

    public ProjectsListFiltersDTO buildFilters(ProjectsListFiltersDTO filters) {
        if (filters.getProjectTitle() == null || filters.getProjectTitle().isBlank()) {
            filters.setProjectTitle("");
        } else {
            filters.setProjectTitle(
                    "%".concat(filters.getProjectTitle().trim().toLowerCase()).concat("%")
            );
        }

        if (filters.getProjectStatus() == null) {
            filters.setProjectStatus(Collections.emptyList());
        }

        if (filters.getCreatorName() == null || filters.getCreatorName().isBlank()) {
            filters.setCreatorName("");
        } else {
            filters.setCreatorName(
                    "%".concat(filters.getCreatorName().trim().toLowerCase()).concat("%")
            );
        }

        return filters;
    }
}
