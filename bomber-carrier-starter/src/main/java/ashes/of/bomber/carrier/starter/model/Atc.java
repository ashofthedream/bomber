package ashes.of.bomber.carrier.starter.model;

import org.springframework.cloud.client.ServiceInstance;

public class Atc {
    private final ServiceInstance instance;

    public Atc(ServiceInstance instance) {
        this.instance = instance;
    }


    public ServiceInstance getInstance() {
        return instance;
    }

    public String getId() {
        return instance.getInstanceId();
    }
}
