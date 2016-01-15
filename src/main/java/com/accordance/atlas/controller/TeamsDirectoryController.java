package com.accordance.atlas.controller;

import com.accordance.atlas.model.JsonUtils;
import com.accordance.atlas.repository.TeamsRepository;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.json.simple.JSONArray;

@RestController
@RequestMapping("/teams")
public class TeamsDirectoryController {

    private static final Logger logger = LoggerFactory.getLogger(TeamsDirectoryController.class);

    @Autowired
    TeamsRepository teamsRepo;

    // Get all teams
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public JSONArray index() {
        List<Vertex> teams = teamsRepo.findTeams();
        return JsonUtils.fromVertexList(teams);
    }
}
