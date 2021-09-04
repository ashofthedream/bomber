package ashes.of.bomber.carrier.starter.watcher;

import ashes.of.bomber.carrier.starter.services.AtcService;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.watcher.Watcher;
import org.springframework.stereotype.Component;

@Component
public class CarrierWatcher implements Watcher {

    private final AtcService atcService;

    public CarrierWatcher(AtcService atcService) {
        this.atcService = atcService;
    }

    @Override
    public void watch(TestAppDescription app) {

    }
}
