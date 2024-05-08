package capi.funding.api.entity;

public record MostSearchedCategoriesDTO(
        String categoryName,
        long totalSearchs
) {
}
