package com.accordance.atlas.model;

import com.tinkerpop.blueprints.Vertex;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class JSONRecord extends JSONObject {
    @SuppressWarnings("unchecked")
    public JSONRecord(Vertex record) {
        if (record != null) {
            record.getPropertyKeys().forEach(property -> put(property, record.getProperty(property)));
        }

        // For backward compatibility and for short period of time adding _id
        String id = record.getProperty("id");
        if (id != null) {
            put("_id", id);
        }
    }
}