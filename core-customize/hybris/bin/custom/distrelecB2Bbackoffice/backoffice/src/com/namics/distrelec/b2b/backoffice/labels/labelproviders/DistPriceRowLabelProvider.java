package com.namics.distrelec.b2b.backoffice.labels.labelproviders;

import com.hybris.backoffice.labels.labelproviders.PriceRowLabelProvider;
import com.namics.distrelec.b2b.core.model.DistErpPriceConditionTypeModel;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import de.hybris.platform.europe1.model.PriceRowModel;

public class DistPriceRowLabelProvider extends PriceRowLabelProvider {

    @Override
    public String getLabel(PriceRowModel priceRow) {
        if (priceRow == null) {
            return "";
        } else {
            DistPriceRowModel distPriceRow = (DistPriceRowModel) priceRow;
            StringBuilder sb = new StringBuilder();
            sb.append(getUserLabel(distPriceRow));
            sb.append(" - ");
            sb.append(getPriceConditionType(distPriceRow));
            sb.append(" - ");
            sb.append(distPriceRow.getMinqtd());
            sb.append(" - ");
            sb.append(getPriceLabel(distPriceRow));

            return sb.toString();
        }
    }

    protected String getPriceConditionType(DistPriceRowModel distPriceRow) {
        DistErpPriceConditionTypeModel erpPriceConditionTypeModel = distPriceRow.getErpPriceConditionType();
        String priceConditionTypeLabel = getLabelService().getObjectLabel(erpPriceConditionTypeModel);
        return  priceConditionTypeLabel;
    }
}
