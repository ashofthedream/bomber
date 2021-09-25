package ashes.of.bomber.carrier.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("bomber")
public class BomberProperties {
    private List<String> sinks;
    private List<String> watchers;
    private BomberAppsProperties apps;

    public List<String> getSinks() {
        return sinks;
    }

    public BomberProperties setSinks(List<String> sinks) {
        this.sinks = sinks;
        return this;
    }

    public List<String> getWatchers() {
        return watchers;
    }

    public BomberProperties setWatchers(List<String> watchers) {
        this.watchers = watchers;
        return this;
    }

    public BomberAppsProperties getApps() {
        return apps;
    }

    public BomberProperties setApps(BomberAppsProperties apps) {
        this.apps = apps;
        return this;
    }

    @Override
    public String toString() {
        return "BomberProperties{" +
                "sinks=" + sinks +
                ", watchers=" + watchers +
                ", apps=" + apps +
                '}';
    }
}
