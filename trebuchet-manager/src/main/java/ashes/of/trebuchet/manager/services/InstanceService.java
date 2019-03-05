package ashes.of.trebuchet.manager.services;

import ashes.of.trebuchet.manager.model.Instance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;


@Service
public class InstanceService {
    private static final Logger log = LogManager.getLogger();

    private final CuratorFramework cf;
    private final PathChildrenCache ppc;

    public InstanceService(CuratorFramework cf) {
        this.cf = cf;
        this.ppc = new PathChildrenCache(cf, "/trebuchet/instances", true);
    }


    @PostConstruct
    public void init() throws Exception {
        ppc.getListenable().addListener(this::handleInstances);
        ppc.start();
    }

    private void handleInstances(CuratorFramework cf, PathChildrenCacheEvent event) {
        if (event.getType() == CHILD_ADDED || event.getType() == CHILD_UPDATED || event.getType() == CHILD_REMOVED ) {
            String data = Optional.of(event)
                    .map(PathChildrenCacheEvent::getData)
                    .map(ChildData::getData)
                    .map(String::new)
                    .orElse("<null>");

            log.warn("node event: {}, data: {}", event.getType(), data);
        }
    }

    public List<Instance> getInstances() {
        return Collections.emptyList();
    }
}
