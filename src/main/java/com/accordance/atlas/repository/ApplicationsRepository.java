package com.accordance.atlas.repository;

import com.accordance.atlas.model.DeployRecord;
import com.tinkerpop.blueprints.Vertex;

import java.util.List;

public interface ApplicationsRepository {
    List<Vertex> findApplications();
    List<Vertex> findApplications(ApplicationQueryBuilder query);

    Vertex getApplicationById(String s);
    
    boolean getAppDeploymentStatus(String id);
    boolean activateDeploymentLock(String id);
    boolean releaseDeploymentLock(DeployRecord app);

}
