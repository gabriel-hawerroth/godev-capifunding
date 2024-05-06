package capi.funding.api.dto;

@SuppressWarnings("unused")
public interface ProjectsListDTO {
    long getProjectId();

    String getProjectTitle();

    byte[] getCoverImage();

    String getCreatorName();

    byte[] getCreatorProfileImage();

    int getRemainingDays();

    double getPercentageRaised();
}
