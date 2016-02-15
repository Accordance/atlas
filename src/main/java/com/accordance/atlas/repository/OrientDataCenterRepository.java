package com.accordance.atlas.repository;

import com.accordance.atlas.model.DataCenter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public final class OrientDataCenterRepository implements DataCenterRepository {
    private static final String DATA_CENTER_CLASS_NAME = "DataCenter";
    private static final String DATA_CENTER_CLASS_NAME_REF = "class:" + DATA_CENTER_CLASS_NAME;

    private static final String KEY_ID = "userId";
    private static final String KEY_DESCRIPTION = "descr";

    private final OrientDbFactory dbFactory;

    @Autowired
    public OrientDataCenterRepository(OrientDbFactory db) {
        this.dbFactory = Objects.requireNonNull(db, "db");
    }

    public static DataCenter fromVertex(Vertex vertex) {
        String id = vertex.getProperty(KEY_ID);
        DataCenter.Builder result = new DataCenter.Builder(id);

        String descr = vertex.getProperty(KEY_DESCRIPTION);
        if (descr != null) {
            result.setDescription(descr);
        }

        return result.create();
    }

    @Override
    public void getAllDataCenters(Consumer<? super DataCenter> elementProcessor) throws IOException {
        dbFactory.withGraphNoTx((OrientGraphNoTx db) -> {
            for (Vertex vertex: db.getVerticesOfClass(DATA_CENTER_CLASS_NAME)) {
                elementProcessor.accept(fromVertex(vertex));
            }
            return null;
        });
    }

    @Override
    public void addDataCenter(DataCenter dataCenter) throws IOException {
        Objects.requireNonNull(dataCenter, "dataCenter");

        dbFactory.withGraphNoTx((OrientGraphNoTx db) -> {
            db.addVertex(DATA_CENTER_CLASS_NAME_REF,
                    KEY_ID, dataCenter.getUserId(),
                    KEY_DESCRIPTION, dataCenter.getDescription());
            return null;
        });
    }
}
