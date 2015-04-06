package com.accordance.atlas.repository;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OrientDbFactory {

    @Autowired
    private OrientDbConfiguration dbProperties;

    @Autowired
    Environment env;

    public OrientGraphFactory getGraph() {
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
