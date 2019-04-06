package ashes.of.trebuchet.manager.services;

import ashes.of.trebuchet.manager.model.Instance;
import ashes.of.trebuchet.manager.model.events.InstanceEvent;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;


@Service
public class InstanceService {
    private static final Logger log = LogManager.getLogger();

    private final DirectProcessor<InstanceEvent> events = DirectProcessor.create();
    private final CuratorFramework cf;
    private final PathChildrenCache ppc;

    public InstanceService(CuratorFramework cf) {
        this.cf = cf;
        this.ppc = new PathChildrenCache(cf, "/trebuchet/instances", true);
    }


    @PostConstruct
    public void init() throws Exception {

        Flux.interval(Duration.ofSeconds(5))
                .subscribe(tick -> {
                    events.onNext(new InstanceEvent("Oh shit"));
                });


        ppc.getListenable().addListener(this::handleInstances);
        ppc.start();
    }

    private void handleInstances(CuratorFramework cf, PathChildrenCacheEvent event) {
        if (!isChildEvent(event))
            return;

        String data = Optional.of(event)
                .map(PathChildrenCacheEvent::getData)
                .map(ChildData::getData)
                .map(String::new)
                .orElse("<null>");


        events.onNext(new InstanceEvent(data));

        log.warn("node event: {}, data: {}", event.getType(), data);
    }

    private boolean isChildEvent(PathChildrenCacheEvent event) {
        return event.getType() == CHILD_ADDED || event.getType() == CHILD_UPDATED || event.getType() == CHILD_REMOVED;
    }


    public List<Instance> getInstances() {
        return Collections.emptyList();
    }


    public Flux<InstanceEvent> events() {
        return events;
    }
}
