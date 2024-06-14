/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import com.namics.distrelec.b2b.core.enums.DistCarpetItemSize;

/**
 * {@code DistCarpetItemData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistCarpetItemData extends DistMinCarpetItemData {

    private DistCarpetItemSize size;

    public DistCarpetItemSize getSize() {
        return size;
    }

    public void setSize(final DistCarpetItemSize size) {
        this.size = size;
    }
}
