/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export;

import java.util.Map;

/**
 * Interface specifying methods to provide promotionLabels for a FlexibleSearch.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20
 * 
 */
public interface DistFlexibleSearchPromotionLabelProvider {

    /**
     * get all names and ranks for the promotion labels.<br/>
     * store them under the key "promotionLabel_{code}_name" resp. "promotionLabel_{code}_rank"
     */
    public Map<String, Object> getPromotionLabels();

}
