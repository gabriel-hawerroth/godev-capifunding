package capi.funding.api.dto;

@SuppressWarnings("unused")
public class InterfacesSQL {

    public interface ProjectsList {
        long getId();

        String getTitle();

        double getPercentage_raised();

        byte[] getProject_image();

        String getCreator_name();

        byte[] getCreator_image();
    }
}
