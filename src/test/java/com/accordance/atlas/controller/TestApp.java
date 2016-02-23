package com.accordance.atlas.controller;

import com.accordance.atlas.repository.ApplicationsRepository;
import com.accordance.atlas.repository.ApplicationsRepositoryImpl;
import com.accordance.atlas.repository.DataCenterRepository;
import com.accordance.atlas.repository.OrientDataCenterRepository;
import com.accordance.atlas.repository.OrientDbFactory;
import com.accordance.atlas.repository.TeamsRepository;
import com.accordance.atlas.repository.TeamsRepositoryImpl;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class TestApp {
    @Bean
    @Autowired
    public TeamsRepository teamsRepository(OrientDbFactory dbFactory) {
        return new TeamsRepositoryImpl(dbFactory);
    }

    @Bean
    @Autowired
    public DataCenterRepository dataCenterRepository(OrientDbFactory dbFactory) {
        return new OrientDataCenterRepository(dbFactory);
    }

    @Bean
    @Autowired
    public ApplicationsRepository applicationsRepository(OrientDbFactory dbFactory) {
        return new ApplicationsRepositoryImpl(dbFactory);
    }

    @Bean
    public OrientDbFactory orientDbFactory() {
        OrientGraphFactory graphFactory = new OrientGraphFactory("memory:test");
        OrientGraph db = graphFactory.getTx();
        try {
            db.createVertexType("DataCenter", "V");
            db.createVertexType("Application", "V");
        } catch (OSchemaException ex) {
            // Already created.
        }finally {
            db.shutdown();
        }

        return () -> graphFactory;
    }
}
