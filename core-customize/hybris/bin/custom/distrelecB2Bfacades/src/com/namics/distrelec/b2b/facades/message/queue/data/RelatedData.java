/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue.data;

import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code RelatedData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class RelatedData implements Serializable {

    private RowType type;
    private Map<RelatedDataType, List<CRelatedData>> relatedDataMap;


    /**
     * Create a new instance of {@code RelatedData}
     */
    public RelatedData() {
        super();
    }

    /**
     * Create a new instance of {@code RelatedData}
     *
     * @param relatedDataMap
     */
    public RelatedData(final Map<RelatedDataType, List<CRelatedData>> relatedDataMap) {
        this(null, relatedDataMap);
    }

    /**
     * Create a new instance of {@code RelatedData}
     *
     * @param type
     * @param relatedDataMap
     */
    public RelatedData(final RowType type, final Map<RelatedDataType, List<CRelatedData>> relatedDataMap) {
        this.type = type;
        setRelatedDataMap(relatedDataMap);
    }

    public RowType getType() {
        return type;
    }

    public void setType(final RowType type) {
        this.type = type;
    }

    public Map<RelatedDataType, List<CRelatedData>> getRelatedDataMap() {
        return relatedDataMap;
    }

    public void setRelatedDataMap(final Map<RelatedDataType, List<CRelatedData>> relatedDataMap) {
        // We use SortedMap to force the orders of the keys
        this.relatedDataMap = (relatedDataMap == null || (relatedDataMap instanceof SortedMap)) ? relatedDataMap : new TreeMap<>(relatedDataMap);
    }

    @Override
    public String toString() {
        final String mapTextContent = getRelatedDataMap().entrySet().stream().map((e) -> e.getKey() + Arrays.toString(e.getValue().toArray()))
                .collect(Collectors.joining(", "));
        return String.format("RelatedData [getRelatedDataMap()=%s]", mapTextContent);
    }

}
