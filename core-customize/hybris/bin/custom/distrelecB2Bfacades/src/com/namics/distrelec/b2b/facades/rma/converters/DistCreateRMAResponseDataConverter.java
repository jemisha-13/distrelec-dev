package com.namics.distrelec.b2b.facades.rma.converters;

import com.distrelec.webservice.if19.v1.RMACreateRespItem;
import com.distrelec.webservice.if19.v1.RMACreateRespOrder;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseItemData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.ArrayList;
import java.util.List;

public class DistCreateRMAResponseDataConverter
		extends AbstractPopulatingConverter<RMACreateRespOrder, CreateRMAResponseData> {

	@Override
	public void populate(final RMACreateRespOrder source, final CreateRMAResponseData target) {
		target.setOfficeAddress(source.getOfficeAddress());
		target.setRmaHeaderStatus(source.getRmaHeaderStatus());
		target.setRmaNumber(source.getRmaNumber());

		final List<CreateRMAResponseItemData> createRMAResponseItemDataList = new ArrayList<CreateRMAResponseItemData>();
		final List<RMACreateRespItem> rmaCreateRespItemList = source.getItems();

		for (final RMACreateRespItem rmaCreateRespItem : rmaCreateRespItemList) {
			final CreateRMAResponseItemData rmaCreationResponseItemData = new CreateRMAResponseItemData();
			rmaCreationResponseItemData.setRmaItemNumber(rmaCreateRespItem.getRmaItemNumber());
			rmaCreationResponseItemData.setRmaItemStatus(rmaCreateRespItem.getRmaItemStatus());
			createRMAResponseItemDataList.add(rmaCreationResponseItemData);
		}
		target.setOrderItems(createRMAResponseItemDataList);
	}
}
