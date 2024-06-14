
package com.namics.distrelec.b2b.core.bmecat.export.query;

import java.util.HashMap;
import java.util.Map;

import com.namics.distrelec.b2b.core.bmecat.export.DistBMECatConstants;

import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportChannelModel;

/**
 * 
 * 
 * @author Abhinay Jadhav, Datwyler IT
 * @since 10-Dec-2017
 * 
 */

public class DistBMECatProductExportParameterProvider implements DistBMECatParameterProvider<DistBMECatExportChannelModel> {

    /**
     * Create parameter map out of export channel*
     * 
     * @param channel
     */
    @Override
    public Map<String, Object> getParameters(final DistBMECatExportChannelModel channel) {
        final Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(DistBMECatQueryCreator.SALESORG_PARAM, Long.valueOf(channel.getCmsSite().getSalesOrg().getPk().getLongValue()));
        parameters.put(DistBMECatQueryCreator.DEFAULT_LANG_ISOCODE_PARAM, DistBMECatConstants.DEFAULT_LANGUAGE);
        parameters.put(DistBMECatQueryCreator.LANG_ISOCODE_PARAM, channel.getLanguage().getIsocode());
        parameters.put(DistBMECatQueryCreator.DATASHEET_PARAM, DistBMECatConstants.DATASHEET_TYPE);

        return parameters;
    }

}
