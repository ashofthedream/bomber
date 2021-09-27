package ashes.of.bomber.atc.models;

import org.springframework.cloud.client.ServiceInstance;

public class Carrier {
    private ServiceInstance instance;

    public Carrier(ServiceInstance instance) {
        this.instance = instance;
    }

    public String getId() {
        return instance.getInstanceId();
    }

    public ServiceInstance getInstance() {
        return instance;
    }
}
