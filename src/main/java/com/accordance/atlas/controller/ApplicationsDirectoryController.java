package com.accordance.atlas.controller;

import com.accordance.atlas.model.JsonUtils;
import com.accordance.atlas.repository.ApplicationQueryBuilder;
import com.accordance.atlas.repository.ApplicationsRepository;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@RestController
@RequestMapping("/apps")
public class ApplicationsDirectoryController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationsDirectoryController.class);

    @Autowired
    ApplicationsRepository appsRepo;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public JSONArray index() {
        List<Vertex> vertices = appsRepo.findApplications();
        return JsonUtils.fromVertexList(vertices);
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getAppNames() {
        List<Vertex> vertices = appsRepo.findApplications(new ApplicationQueryBuilder().onlyFields(new String[]{"id", "name"}));

        return JsonUtils.fromVertexList(vertices);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getAppById(@PathVariable("id") String id) {
        Vertex v = appsRepo.getApplicationById(id);
        return JsonUtils.fromVertex(v);
    }
}
