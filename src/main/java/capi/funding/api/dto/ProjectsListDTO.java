package capi.funding.api.dto;

import java.util.List;

public record ProjectsListDTO(
        Long totalRegisters,
        List<ProjectsList> projectsLists
) {
}
