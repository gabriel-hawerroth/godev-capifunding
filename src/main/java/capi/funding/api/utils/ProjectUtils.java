package capi.funding.api.utils;

import capi.funding.api.dto.ProjectsListFiltersDTO;
import capi.funding.api.entity.Project;
import capi.funding.api.entity.ProjectMilestone;
import capi.funding.api.entity.ProjectSearchLog;
import capi.funding.api.enums.ProjectSearchFields;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.MilestoneSequenceException;
import capi.funding.api.infra.exceptions.ProjectEditabilityException;
import capi.funding.api.services.ProjectMilestoneService;
import capi.funding.api.services.ProjectSearchLogService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectUtils {

    private final Utils utils;
    private final ProjectMilestoneService projectMilestoneService;
    private final ProjectSearchLogService searchLogService;

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

    public void buildFilters(ProjectsListFiltersDTO filters) {
        if (filters.getProjectTitle() == null || filters.getProjectTitle().isBlank()) {
            filters.setProjectTitle("");
        } else {
            filters.setProjectTitle(
                    "%".concat(filters.getProjectTitle().trim().toLowerCase()).concat("%")
            );
        }

        if (filters.getProjectCategory() == null) {
            filters.setProjectCategory(Collections.emptyList());
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

        filters.setPageNumber(
                (filters.getPageNumber() - 1) * filters.getLimit()
        );
    }

    public void logProjectSearch(ProjectsListFiltersDTO filters) {
        List<ProjectSearchLog> searchLogs = new LinkedList<>();

        final long userId = utils.getAuthUser().getId();
        final LocalDateTime now = LocalDateTime.now();

        if (filters.getProjectTitle() != null && !filters.getProjectTitle().isBlank()) {
            addFilterLog(searchLogs, userId, ProjectSearchFields.PROJECT_TITLE.getValue(), filters.getProjectTitle(), now);
        }

        if (!filters.getProjectCategory().isEmpty()) {
            for (Integer categoryId : filters.getProjectCategory()) {
                addFilterLog(searchLogs, userId, ProjectSearchFields.PROJECT_CATEGORY.getValue(), categoryId.toString(), now);
            }
        }

        if (!filters.getProjectStatus().isEmpty()) {
            for (Integer statusId : filters.getProjectStatus()) {
                addFilterLog(searchLogs, userId, ProjectSearchFields.PROJECT_STATUS.getValue(), statusId.toString(), now);
            }
        }

        if (filters.getCreatorName() != null && !filters.getCreatorName().isBlank()) {
            addFilterLog(searchLogs, userId, ProjectSearchFields.CREATOR_NAME.getValue(), filters.getCreatorName(), now);
        }

        searchLogService.saveAll(searchLogs);
    }

    private void addFilterLog(List<ProjectSearchLog> searchLogs, long userId, String filterName, String filterValue, LocalDateTime now) {
        searchLogs.add(new ProjectSearchLog(
                userId,
                filterName,
                filterValue,
                now
        ));
    }
}
