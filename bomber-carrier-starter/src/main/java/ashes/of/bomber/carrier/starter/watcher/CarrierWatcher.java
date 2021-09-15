package ashes.of.bomber.carrier.starter.watcher;

import ashes.of.bomber.carrier.starter.services.CarrierService;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.watcher.Watcher;
import org.springframework.stereotype.Component;

@Component
public class CarrierWatcher implements Watcher {

    private final CarrierService carrierService;

    public CarrierWatcher(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @Override
    public void watch(TestApp app) {

    }
}
