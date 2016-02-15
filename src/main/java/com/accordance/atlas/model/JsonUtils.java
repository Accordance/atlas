package com.accordance.atlas.model;

import com.tinkerpop.blueprints.Vertex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonUtils {
    public static JSONObject fromVertex(Vertex vertex) {
        JSONObject result = new JSONObject();
        if (vertex == null) {
            return result;
        }

        vertex.getPropertyKeys().forEach((String key) -> result.put(key, vertex.getProperty(key)));

        // For backward compatibility and for short period of time adding _id
        String id = vertex.getProperty("id");
        if (id != null) {
            result.put("_id", id);
        }
        return result;
    }

    public static JSONArray fromVertexList(Iterable<? extends Vertex> vertices) {
        JSONArray result = new JSONArray();
        vertices.forEach((Vertex vertex) -> result.add(fromVertex(vertex)));
        return result;
    }

    private JsonUtils() {
        throw new AssertionError();
    }
}
