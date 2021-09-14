package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.ApplicationDto;

import java.util.List;

public class GetApplicationsResponse {
    private List<ApplicationDto> applications;

    public List<ApplicationDto> getApplications() {
        return applications;
    }

    public GetApplicationsResponse setApplications(List<ApplicationDto> applications) {
        this.applications = applications;
        return this;
    }
}
