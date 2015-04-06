package com.accordance.atlas.model;

import com.tinkerpop.blueprints.Vertex;
import org.json.simple.JSONArray;

import java.util.List;

@SuppressWarnings("serial")
public class JSONResultSet extends JSONArray {
    @SuppressWarnings("unchecked")
    public JSONResultSet(List<Vertex> resultSet) {
        super();
        try {
            resultSet.forEach(v -> add(new JSONRecord(v)));
        } finally {
        }
    }
}
