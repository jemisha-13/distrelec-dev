package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.MANUFACTURER_NAME_COLUMN;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.MANUFACTURER_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.PRICE_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.RELEVANCE_SORT;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.SORT_PARAM;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.SINGLE_MIN_PRICE_GROSS_COLUMN;
import static com.namics.hybris.ffsearch.data.paging.SortType.ASC;
import static com.namics.hybris.ffsearch.data.paging.SortType.DSC;

import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

import com.namics.distrelec.b2b.facades.backorder.impl.DefaultBackOrderFacadeImpl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class SortParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    static final Map<String, String> sortConversionMap = Map.of(MANUFACTURER_SORT + ":" + ASC.getValue(),
                                                                MANUFACTURER_NAME_COLUMN + " " + ASC.getValue(),
                                                                MANUFACTURER_SORT + ":" + DSC.getValue(),
                                                                MANUFACTURER_NAME_COLUMN + " " + DSC.getValue(),
                                                                PRICE_SORT + ":" + ASC.getValue(),
                                                                SINGLE_MIN_PRICE_GROSS_COLUMN + " " + ASC.getValue(),
                                                                PRICE_SORT + ":" + DSC.getValue(),
                                                                SINGLE_MIN_PRICE_GROSS_COLUMN + " " + DSC.getValue(),
                                                                DefaultBackOrderFacadeImpl.DESC, RELEVANCE_SORT);

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        PageableData pageableData = searchRequestTuple.getPageableData();
        String sort = pageableData.getSort();

        String convertedSort;
        if (StringUtils.isBlank(sort)) {
            // consider blank sort as relevance
            convertedSort = RELEVANCE_SORT;
        } else {
            if (sortConversionMap.containsKey(sort)) {
                convertedSort = sortConversionMap.get(sort);
            } else {
                throw new IllegalArgumentException("Unsupported sort: " + sort);
            }
        }

        params.put(SORT_PARAM, convertedSort);
    }
}
