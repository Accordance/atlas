package com.accordance.atlas.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import java.util.function.Function;


public interface OrientDbFactory {
    public OrientGraphFactory getGraph();

    public default <R> R withGraphNoTx(Function<? super OrientGraphNoTx, ? extends R> action) {
        OrientGraphNoTx graph = getGraph().getNoTx();
        try {
            return action.apply(graph);
        } finally {
            graph.shutdown();
        }
    }

    public default <R> R withGraphTx(Function<? super OrientGraph, ? extends R> action) {
        OrientGraph graph = getGraph().getTx();
        try {
            return action.apply(graph);
        } finally {
            graph.shutdown();
        }
    }

    public default <R> R withDocumentDb(Function<? super ODatabaseDocumentTx, ? extends R> action) {
        try (ODatabaseDocumentTx documentDb = getGraph().getDatabase()) {
            return action.apply(documentDb);
        }
    }
}
