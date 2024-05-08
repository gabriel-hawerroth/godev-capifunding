package capi.funding.api.dto;

@SuppressWarnings("unused")
public interface ProjectsList {
    long getProjectId();

    String getProjectTitle();

    byte[] getCoverImage();

    String getCreatorName();

    byte[] getCreatorProfileImage();

    int getRemainingDays();

    double getPercentageRaised();

    String getCategory();

    String getStatus();
}
