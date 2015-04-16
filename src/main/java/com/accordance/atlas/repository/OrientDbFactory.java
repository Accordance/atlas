package com.accordance.atlas.repository;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrientDbFactory {

    @Autowired
    private OrientDbConfiguration dbProperties;

    @Autowired
    Environment env;

    @Autowired
    LoadBalancerClient loadBalancer;

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    ConsulRepository consulRepository;

    private static Map<String, String> credentials;

    public static final String ORIENTDB_NAME = "orientdb-data";

    public OrientGraphFactory getGraph() {
        ServiceInstance instance = loadBalancer.choose(ORIENTDB_NAME);
        List<ServiceInstance> instances = discoveryClient.getInstances(ORIENTDB_NAME);
        if (instances.size() > 0)
        {
            instance = instances.get(0);
        }

        if (instance != null) {
            dbProperties.setHost(instance.getHost());
            dbProperties.setPort(instance.getPort());
        }

        if (credentials == null) {
            credentials = consulRepository.getPropertyValuesRecursive("secrets/orientdb", new HashMap<>());
        }

        if (credentials.containsKey("username")) {
            dbProperties.setUsername(credentials.get("username"));
        }

        if (credentials.containsKey("password")) {
            dbProperties.setPassword(credentials.get("password"));
        }

        return new OrientGraphFactory(dbProperties.getConnectionString(),
                dbProperties.getUsername(), dbProperties.getPassword()).setupPool(1, 10);
    }

    public OrientGraph startTransaction() {
        return getGraph().getTx();
    }

    public OrientGraphNoTx startNoTransaction() {
        return getGraph().getNoTx();
    }
}
