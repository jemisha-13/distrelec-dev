package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.DistSapMissingOrdersReportEmailProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class DistMissingOrdersReportEmailContext extends AbstractDistEmailContext<DistSapMissingOrdersReportEmailProcessModel> {

    @Autowired
    private CMSSiteService cmsSiteService;

    private Set<OrderModel> matchedOrders;

    private Set<OrderModel> createdOrders;

    private Set<OrderModel> failedOrders;

    private Date currentTime;

    @Override
    public void init(DistSapMissingOrdersReportEmailProcessModel businessProcessModel, EmailPageModel emailPageModel) {
        matchedOrders = businessProcessModel.getMatchedOrders();
        createdOrders = businessProcessModel.getCreatedOrders();
        failedOrders = businessProcessModel.getFailedOrders();
        currentTime = new Date();

        super.init(businessProcessModel, emailPageModel);
    }

    @Override
    protected BaseSiteModel getSite(DistSapMissingOrdersReportEmailProcessModel businessProcessModel) {
        return cmsSiteService.getSites().stream()
                .filter(site -> site.getUid().equals("distrelec_CH"))
                .findFirst().get();
    }

    @Override
    protected LanguageModel getEmailLanguage(DistSapMissingOrdersReportEmailProcessModel businessProcessModel) {
        return getCommonI18NService().getLanguage(Locale.ENGLISH.getLanguage());
    }

    @Override
    protected CustomerModel getCustomer(DistSapMissingOrdersReportEmailProcessModel businessProcessModel) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return getConfigurationService().getConfiguration().getString("distrelec.missingorders.email.displayName");
    }

    @Override
    public String getToEmail() {
        return getConfigurationService().getConfiguration().getString("distrelec.missingorders.email.to");
    }

    @Override
    public String getFromEmail() {
        return getConfigurationService().getConfiguration().getString("distrelec.missingorders.email.from");
    }

    public Set<OrderModel> getMatchedOrders() {
        return matchedOrders;
    }

    public void setMatchedOrders(Set<OrderModel> matchedOrders) {
        this.matchedOrders = matchedOrders;
    }

    public Set<OrderModel> getCreatedOrders() {
        return createdOrders;
    }

    public void setCreatedOrders(Set<OrderModel> createdOrders) {
        this.createdOrders = createdOrders;
    }

    public Set<OrderModel> getFailedOrders() {
        return failedOrders;
    }

    public void setFailedOrders(Set<OrderModel> failedOrders) {
        this.failedOrders = failedOrders;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }
}
