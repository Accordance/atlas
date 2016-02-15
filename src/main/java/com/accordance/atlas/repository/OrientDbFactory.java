package com.accordance.atlas.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import java.util.function.Function;


public interface OrientDbFactory {
    public OrientGraphFactory getGraph();

    public OrientGraphNoTx startNoTransaction();

    public OrientGraph startTransaction();

    public <R> R withDocumentDb(Function<? super ODatabaseDocumentTx, ? extends R> action);

    public <R> R withGraphNoTx(Function<? super OrientGraphNoTx, ? extends R> action);

    public <R> R withGraphTx(Function<? super OrientGraph, ? extends R> action);
}
