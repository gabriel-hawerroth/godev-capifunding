package capi.funding.api.utils;

import capi.funding.api.entity.Project;
import capi.funding.api.enums.ProjectStatusEnum;
import capi.funding.api.infra.exceptions.ProjectEditabilityException;

public class ProjectUtils {

    private ProjectUtils() {
    }

    public static void checkProjectEditability(Project project) {
        final long projectStatusId = project.getStatus_id();

        if (projectStatusId == ProjectStatusEnum.DONE.getValue()) {
            throw new ProjectEditabilityException("this project has already been concluded and cannot be edited");
        } else if (projectStatusId == ProjectStatusEnum.CANCELED.getValue()) {
            throw new ProjectEditabilityException("this project has already been cancelled and cannot be edited");
        }
    }
}
