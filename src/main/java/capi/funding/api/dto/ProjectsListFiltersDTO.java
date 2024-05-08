package capi.funding.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public final class ProjectsListFiltersDTO {
    private String projectTitle;
    private List<Integer> projectCategory;
    private List<Integer> projectStatus;
    private String creatorName;
    private Long pageNumber;
    private Long limit;
}
