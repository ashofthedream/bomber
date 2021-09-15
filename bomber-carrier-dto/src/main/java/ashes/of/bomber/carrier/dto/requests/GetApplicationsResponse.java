package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.flight.TestAppDto;

import java.util.List;

public class GetApplicationsResponse {
    private List<TestAppDto> testApps;

    public List<TestAppDto> getTestApps() {
        return testApps;
    }

    public GetApplicationsResponse setTestApps(List<TestAppDto> testApps) {
        this.testApps = testApps;
        return this;
    }
}
