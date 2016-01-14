package com.accordance.atlas.repository;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrientDbFactory {
    public static final String ORIENTDB_NAME = "orientdb-data";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDbFactory.class);

    private final AtomicReference<Credentials> credentialsRef;
    private final AtomicReference<OrientGraphFactory> dbPoolRef;

    private final OrientDbConfiguration defaultDbConfig;
    private final LoadBalancerClient loadBalancer;
    private final ConsulRepository consulRepository;

    @Autowired
    public OrientDbFactory(
            OrientDbConfiguration defaultDbConfig,
            LoadBalancerClient loadBalancer,
            ConsulRepository consulRepository) {

        this.defaultDbConfig = defaultDbConfig;
        this.loadBalancer = loadBalancer;
        this.consulRepository = consulRepository;

        this.credentialsRef = new AtomicReference<>(null);
        this.dbPoolRef = new AtomicReference<>(null);
    }

    private DbAddress getDbAddress() {
        String dbName = defaultDbConfig.getDbName();

        ServiceInstance instance = loadBalancer.choose(ORIENTDB_NAME);
        if (instance != null) {
            return new DbAddress(dbName, instance.getHost(), instance.getPort());
        }

        return new DbAddress(dbName, defaultDbConfig.getHost(), defaultDbConfig.getPort());
    }

    private Credentials getCredentials() {
        Credentials credentials = credentialsRef.get();
        if (credentials == null) {
            Map<String, String> credentialProps = consulRepository.getPropertyValuesRecursive("secrets/orientdb", new HashMap<>());
            if (credentialProps != null) {
                String userName = credentialProps.get("username");
                String password = credentialProps.get("password");
                credentials = new Credentials(
                        userName != null ? userName : defaultDbConfig.getUsername(),
                        password != null ? password : defaultDbConfig.getPassword());

                if (!credentialsRef.compareAndSet(null, credentials)) {
                    credentials = credentialsRef.get();
                }
            }
        }

        if (credentials != null) {
            return credentials;
        }

        return new Credentials(defaultDbConfig.getUsername(), defaultDbConfig.getPassword());
    }

    public OrientGraphFactory getGraph() {
        OrientGraphFactory result = dbPoolRef.get();
        if (result == null) {
            DbAddress dbAddress = getDbAddress();
            Credentials credentials = getCredentials();

            LOGGER.info("Creating OrientDb pool.");

            result = new OrientGraphFactory(
                    dbAddress.getConnectionString(),
                    credentials.getUserName(),
                    credentials.getPassword());
            result.setupPool(1, 10);

            if (!dbPoolRef.compareAndSet(null, result)) {
                result.close();
                result = dbPoolRef.get();
            }
        }
        return result;
    }

    @PreDestroy
    public void close() {
        OrientGraphFactory result = dbPoolRef.getAndSet(null);
        if (result != null) {
            LOGGER.info("Closing OrientDb pool.");
            result.close();
            LOGGER.info("OrientDb has been closed successfully.");
        }
    }

    public OrientGraph startTransaction() {
        return getGraph().getTx();
    }

    public OrientGraphNoTx startNoTransaction() {
        return getGraph().getNoTx();
    }

    private static final class Credentials {
        private final String userName;
        private final String password;

        public Credentials(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    private static final class DbAddress {
        private final String dbName;
        private final String hostName;
        private final int port;

        public DbAddress(String dbName, String hostName, int port) {
            this.dbName = dbName;
            this.hostName = hostName;
            this.port = port;
        }

        public String getConnectionString() {
            return "remote:" + hostName + ":" + port + "/" + dbName;
        }
    }
}
