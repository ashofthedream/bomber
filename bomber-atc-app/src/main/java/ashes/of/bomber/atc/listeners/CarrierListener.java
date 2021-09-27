package ashes.of.bomber.atc.listeners;

import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.services.WebSocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.zookeeper.discovery.watcher.DependencyState;
import org.springframework.cloud.zookeeper.discovery.watcher.DependencyWatcherListener;
import org.springframework.stereotype.Component;

@Component
public class CarrierListener implements DependencyWatcherListener {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;
    private final WebSocketService webSocketService;

    public CarrierListener(CarrierService carrierService, WebSocketService webSocketService) {
        this.carrierService = carrierService;
        this.webSocketService = webSocketService;
    }


    @Override
    public void stateChanged(String dependencyName, DependencyState newState) {
        log.warn("dependency stateChanged: {} => {}", dependencyName, newState);

        carrierService.getCarriers()
                .flatMap(carrierService::status)
                .collectList()
                .subscribe(webSocketService::sendCarriers);
    }
}
