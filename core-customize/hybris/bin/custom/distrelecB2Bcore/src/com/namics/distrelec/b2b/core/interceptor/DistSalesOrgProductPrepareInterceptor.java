package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;
import java.util.Set;

public class DistSalesOrgProductPrepareInterceptor implements PrepareInterceptor<DistSalesOrgProductModel> {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MailSender mailSender;

    @Override
    public void onPrepare(DistSalesOrgProductModel distSalesOrgProduct, InterceptorContext interceptorContext) {
        if (distSalesOrgProduct.getSalesStatus() != null && distSalesOrgProduct.getSalesStatus().isEndOfLifeInShop() && distSalesOrgProduct.getEndOfLifeDate() == null) {
            distSalesOrgProduct.setEndOfLifeDate(new Date());
        }
        else if (distSalesOrgProduct.getSalesStatus() != null && !distSalesOrgProduct.getSalesStatus().isEndOfLifeInShop() && distSalesOrgProduct.getEndOfLifeDate() != null) {
            distSalesOrgProduct.setEndOfLifeDate(null);
        }

        if(isAlreadySaved(distSalesOrgProduct)){
            Set<String> listOfModifiedFields = distSalesOrgProduct.getItemModelContext().getDirtyAttributes();

            boolean isErpUpdateBlockedModified = listOfModifiedFields
                    .stream()
                    .anyMatch(field -> StringUtils.equals(field, "erpUpdateBlocked"));

            if (isErpUpdateBlockedModified) {
                send(distSalesOrgProduct);
            }
        }
    }

    private boolean isAlreadySaved(DistSalesOrgProductModel distSalesOrgProduct) {
        return distSalesOrgProduct.getPk() != null;
    }

    private void send(DistSalesOrgProductModel distSalesOrgProduct) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(configurationService.getConfiguration().getString("erp.product.status.block.from.mail.id"));
        message.setTo(configurationService.getConfiguration().getString("erp.product.status.block.to.mail.id").split(DistConstants.Punctuation.COMMA));
        message.setSubject("ERP Status Block Updated");
        message.setText(String.format("Product with code %s was updated for Sales Group %s!", distSalesOrgProduct.getProduct().getCode(), distSalesOrgProduct.getSalesOrg().getCode()));
        mailSender.send(message);
    }
}