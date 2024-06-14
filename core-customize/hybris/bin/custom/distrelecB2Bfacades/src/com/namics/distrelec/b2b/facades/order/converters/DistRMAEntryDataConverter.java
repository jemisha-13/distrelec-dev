package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAList3;
import de.hybris.platform.commercefacades.order.data.DistRMAEntryData;
import de.hybris.platform.commercefacades.order.data.RMAData;
import de.hybris.platform.commercefacades.order.data.RMAStatus;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DistRMAEntryDataConverter extends AbstractPopulatingConverter<ItemList, DistRMAEntryData> {

    private static final Logger LOG = LoggerFactory.getLogger(DistRMAEntryDataConverter.class);

    @Override
    public void populate(final ItemList source, final DistRMAEntryData target) {

        target.setRemainingReturnQty(source.getRemainingQuantity());
        target.setNotAllowed(source.isNotAllowedReturnFlag());
        target.setNotAllowedDesc(source.getNotAllowedReturnDescription());
        final List<RMAList3> rmas = source.getRmas();
        final List<RMAData> rmaDataList = new ArrayList<RMAData>();

        rmas.stream().forEach(rma -> {
            final RMAData rmaData = new RMAData();
            rmaData.setRmaNumber(rma.getRmaNumber());
            rmaData.setRmaItemStatus(resolveStatus(rma.getRmaNumber(), rma.getRmaItemStatus()));
            rmaData.setOfficeAddress(rma.getOfficeAddress());
            rmaDataList.add(rmaData);
        });
        target.setRmas(rmaDataList);
    }

    private RMAStatus resolveStatus(String rmaNumber, String rmaStatusText) {
        switch (rmaStatusText.toLowerCase()) {
            case "in progress":
                return RMAStatus.IN_PROGRESS;
            case "approved":
                return RMAStatus.APPROVED;
            case "rejected":
                return RMAStatus.REJECTED;
            default:
                if (StringUtils.isNotEmpty(rmaStatusText)) {
                    LOG.error("Unresolvable RMA status received '{}' for RMA {}", rmaStatusText, rmaNumber);
                }
                return null;
        }
    }
}
