package com.namics.distrelec.b2b.facades.feedback.converter;

import com.namics.distrelec.b2b.core.event.FeedbackDataDto;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DistFeedbackReverseConverter extends AbstractPopulatingConverter<FeedbackDataDto, DistFeedbackModel> {

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private UserService userService;

    @Override
    public void populate(FeedbackDataDto source, DistFeedbackModel target) throws ConversionException {
        target.setManufacturer(source.getManufacturer());
        target.setOtherManufacturerName(source.getOtherManufacturerName());
        target.setManufacturerType(source.getManufacturerType());
        target.setEmail(StringUtils.isEmpty(source.getEmail()) ? getCurrentSessionCustomer().getEmail() : source.getEmail());
        target.setSearchTerm(source.getSearchTerm());
        target.setTellUsMore(source.getTellUsMore());
        populateCurrentSalesOrgData(target);
    }

    private B2BCustomerModel getCurrentSessionCustomer() {
        return (B2BCustomerModel) getUserService().getCurrentUser();
    }

    private void populateCurrentSalesOrgData(DistFeedbackModel target) {
        DistSalesOrgModel currentSalesOrg = getDistSalesOrgService().getCurrentSalesOrg();
        if (currentSalesOrg != null) {
            target.setCountry(currentSalesOrg.getCountry().getName());
            target.setSalesOrg(currentSalesOrg.getCode());
        }
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
