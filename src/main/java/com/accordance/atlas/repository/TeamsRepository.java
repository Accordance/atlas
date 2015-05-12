package com.accordance.atlas.repository;

import com.tinkerpop.blueprints.Vertex;

import java.util.List;

public interface TeamsRepository {
    List<Vertex> findTeams();
}
