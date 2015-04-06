package com.accordance.atlas.repository;

import com.tinkerpop.blueprints.Vertex;

import java.util.List;

public interface ApplicationsRepository {
    List<Vertex> findApplications();
    List<Vertex> findApplications(ApplicationQueryBuilder query);

    Vertex getApplicationById(String s);
}
