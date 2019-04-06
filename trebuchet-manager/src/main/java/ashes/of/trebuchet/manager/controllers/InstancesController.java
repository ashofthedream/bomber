package ashes.of.trebuchet.manager.controllers;

import ashes.of.trebuchet.manager.dto.ResponseEntities;
import ashes.of.trebuchet.manager.services.InstanceService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/instances")
public class InstancesController {
    private static final Logger log = LogManager.getLogger();

    private final CuratorFramework cf;
    private final InstanceService instanceService;

    public InstancesController(CuratorFramework cf, InstanceService instanceService) {
        this.cf = cf;
        this.instanceService = instanceService;
    }

    /**
     * @return all managed instances
     */
    @GetMapping
    public ResponseEntity<?> getInstances() {
        log.debug("get all instances");

        return ResponseEntities.ok(instanceService.getInstances());
    }


    @PostMapping("/temp/createInstance")
    public ResponseEntity<?> getInstances(@RequestBody String data) {
        log.warn("create instance: {}", data);

        String name = Long.toHexString(System.currentTimeMillis() % 0xFFFF);

        PersistentNode node = new PersistentNode(cf, CreateMode.EPHEMERAL, false, "/trebuchet/instances/" + name, data.getBytes());
        node.start();

        return ResponseEntities.ok();
    }

}
