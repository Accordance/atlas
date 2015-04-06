package com.accordance.atlas.repository;

import com.google.common.base.Joiner;

public class ApplicationQueryBuilder {
    String startNode;
    boolean up = false;
    boolean down = false;
//    List<String> includeNodes;
//    List<String> excludeNodes;
    Integer depth = null;
    String[] selectedFields = {};

    public ApplicationQueryBuilder fromStartNode(String startNode) {
        this.startNode = startNode;
        return this;
    }

    public ApplicationQueryBuilder onlyFields(String[] fields) {
        selectedFields = fields;
        return this;
    }

    public ApplicationQueryBuilder traverseUp(boolean up) {
        this.up = up;
        return this;
    }

    public ApplicationQueryBuilder traverseDown(boolean down) {
        this.down = down;
        return this;
    }

    public ApplicationQueryBuilder withDepth(Integer depth) {
        this.depth = depth;
        return this;
    }

    public String fromNode() {
        return startNode;
    }

    public String getDirection() {
        String direction = "out";
        if (up) {
            if (down)
                direction = "both";
            else
                direction = "in";
        }

        return direction;
    }

    public String getHopsQuery() {
        String hops = "";
        if (depth != null) {
            hops = String.format("where $depth <= %d", depth);
        }
        return hops;
    }

    public String filterFields() {
        if (selectedFields.length == 0)
            return "*, out('Uses').id as \"uses\"";

        return Joiner.on(',').join(selectedFields);
    }
}
