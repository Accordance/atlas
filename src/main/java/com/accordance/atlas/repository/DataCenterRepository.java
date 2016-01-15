package com.accordance.atlas.repository;

import com.accordance.atlas.model.DataCenter;
import java.io.IOException;
import java.util.function.Consumer;

public interface DataCenterRepository {
    public void getAllDataCenters(Consumer<? super DataCenter> elementProcessor) throws IOException;
    public void addDataCenter(DataCenter dataCenter) throws IOException;
}
