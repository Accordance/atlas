package com.accordance.atlas.repository;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamsRepositoryImpl implements TeamsRepository {

    private static final Logger logger = LoggerFactory.getLogger(TeamsRepositoryImpl.class);

    @Autowired
    OrientDbFactory orientDb;

    @Override
    public List<Vertex> findTeams() {
        String q = "select *, out(\"Owns\").id as applications from Team";
        OSQLSynchQuery<OrientVertex> qr = new OSQLSynchQuery<>(q);
        Iterable<OrientVertex> teams = orientDb.startNoTransaction().command(qr).execute();

        ArrayList<Vertex> foundVertices = new ArrayList<>();
        teams.forEach(team -> foundVertices.add(team));

        return foundVertices;
    }
}
