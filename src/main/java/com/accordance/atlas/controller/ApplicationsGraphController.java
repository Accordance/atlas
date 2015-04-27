package com.accordance.atlas.controller;

import com.accordance.atlas.repository.ApplicationQueryBuilder;
import com.accordance.atlas.repository.ApplicationsRepository;
import com.tinkerpop.blueprints.Vertex;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/apps_graph")
@SuppressWarnings("unchecked")
public class ApplicationsGraphController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationsGraphController.class);

    @Autowired
    ApplicationsRepository appsRepo;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    private JSONObject get_all_graph(
            @RequestParam(value = "raw", required = false, defaultValue = "false") boolean rawJson,
            @RequestParam(value = "up", required = false, defaultValue = "false") boolean upGraph,
            @RequestParam(value = "down", required = false, defaultValue = "false") boolean downGraph,
//            @RequestParam(value = "exclude", required = false) String excludeNodes,
//            @RequestParam(value = "include", required = false) String includeNodes,
            @RequestParam(value = "hops", required = false) Integer nomOfHops) {

//        graph = GraphTraverser.new ApplicationInfo.applications.all


//        tree    = graph.get_all_dependencies params[:selector], params['hops'], down, up || 'false',
//                exclude.split(','), include.split(',')
//
//        return JSON.pretty_generate(tree) if (params['raw'] == 'true')
//
//        graph = (TreeToGraph.new).tree_to_graph(tree)

        List<Vertex> vertices = appsRepo.findApplications();

        return treeToGraph(vertices);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    private JSONObject get_part_graph(
            @PathVariable("id") String start_node,
            @RequestParam(value = "raw", required = false, defaultValue = "false") boolean rawJson,
            @RequestParam(value = "up", required = false, defaultValue = "false") boolean upGraph,
            @RequestParam(value = "down", required = false, defaultValue = "false") boolean downGraph,
//            @RequestParam(value = "exclude", required = false) String excludeNodes,
//            @RequestParam(value = "include", required = false) String includeNodes,
            @RequestParam(value = "hops", required = false) Integer nomOfHops) {

//        graph = GraphTraverser.new ApplicationInfo.applications.all


//        tree    = graph.get_all_dependencies params[:selector], params['hops'], down, up || 'false',
//                exclude.split(','), include.split(',')
//
//        return JSON.pretty_generate(tree) if (params['raw'] == 'true')
//
//        graph = (TreeToGraph.new).tree_to_graph(tree)

        ApplicationQueryBuilder query = new ApplicationQueryBuilder()
                .fromStartNode(start_node)
                .traverseUp(upGraph)
                .traverseDown(downGraph)
                .withDepth(nomOfHops);

        List<Vertex> vertices = appsRepo.findApplications(query);

        return treeToGraph(vertices);
    }

    private JSONObject treeToGraph(List<Vertex> vertices) {

        HashMap<String, Integer> nodeNames = new HashMap<>();
        ArrayList<JSONObject> resultNodesSet = new ArrayList<>();
        ArrayList<JSONObject> linksSet = new ArrayList<>();

        // Give each node an ID
        vertices.forEach(item -> {
                    JSONObject node = new JSONObject();
                    String nodeName = item.getProperty("id");
                    node.put("name", item.getProperty("id"));
                    node.put("group", get_group_id(item.getProperty("type")));

                    Integer nodeId = resultNodesSet.size();
                    nodeNames.put(nodeName, nodeId);
                    resultNodesSet.add(node);
                });

        // Build list of links
        vertices.forEach(item -> {
            String nodeName = item.getProperty("id");
            Integer nodeId = nodeNames.get(nodeName);

            List<String> dependencies = item.getProperty("uses");
            if (dependencies != null) {
                dependencies.forEach(dependency -> {
                    Integer nodeIdz = nodeNames.get(dependency);
                    if (nodeIdz == null) {
//                        JSONObject node_dep = new JSONObject(ArrayUtils.toMap(new String[][]{{"name", dependency}, {"group", get_group_id(null).toString()}}));
//
//                        nodeIdz = resultNodesSet.size();
//                        nodeNames.put(nodeName, nodeIdz);
//                        resultNodesSet.add(node_dep);
                    }
                    else {
                        JSONObject link = new JSONObject();
                        link.put("source", nodeId);
                        link.put("target", nodeIdz);
                        link.put("value", 1);
                        linksSet.add(link);
                    }
                });
            }
        });

        JSONObject result = new JSONObject();
        result.put("nodes", resultNodesSet);
        result.put("links", linksSet);
        return result;
    }

    private Integer get_group_id(String group) {
        // TODO: make sure these numbers are common across all the modules
        if (group == null)
            return 1;

        switch (group) {
            case "ui":
                return 4;
            case "hybrid":
                return 5;
            case "pubsvc":
                return 3;
            case "group":
                return 2;
            default:
                return 1;
        }
    }
}
