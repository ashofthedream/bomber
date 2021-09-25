package ashes.of.bomber.carrier.config.properties;

import java.util.List;

public class BomberAppsProperties {
    private List<String> include;
    private List<String> build;

    public List<String> getInclude() {
        return include;
    }

    public BomberAppsProperties setInclude(List<String> include) {
        this.include = include;
        return this;
    }

    public List<String> getBuild() {
        return build;
    }

    public BomberAppsProperties setBuild(List<String> build) {
        this.build = build;
        return this;
    }

}
