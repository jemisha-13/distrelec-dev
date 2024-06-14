package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.DistStockNotificationProcessModel;
import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.distrelecB2BCore.stockNotification.data.StockNotificationProductResult;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Numbers.ZERO;

/**
 * {@code DistStockNotificationEmailContext}
 *
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public class DistStockNotificationEmailContext extends AbstractDistEmailContext<DistStockNotificationProcessModel> {

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ProductService productService;

    @Override
    public void init(final DistStockNotificationProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        if (businessProcessModel != null) {
            put(FROM_EMAIL, emailPageModel.getFromEmail());
            put(FROM_DISPLAY_NAME, getFromDisplayName(emailPageModel));
            put(DISPLAY_NAME, emailPageModel.getFromEmail());

            final List<DistStockNotificationModel> stockNotificationList = businessProcessModel.getStockNotificationList();

            put(EMAIL, stockNotificationList.get(ZERO).getCustomerEmailId());
            put(EMAIL_LANGUAGE, getEmailLanguage(businessProcessModel));

            final String language = getEmailLanguage(businessProcessModel).getIsocode();
            put("lang", language);

            final CMSSiteModel website = stockNotificationList.get(ZERO).getCurrentSite();
            put("country", website.getCountry().getIsocode().toLowerCase());
            put("footerAddress", website.getPrintFooterContent(new Locale(language)));
            put("site", website.getUid());

            put("StringEscapeUtils", StringEscapeUtils.class);

            put("stockNotificationProductResultList", createStockNotificationResult(stockNotificationList));
        }
    }

    @Override
    protected BaseSiteModel getSite(final DistStockNotificationProcessModel businessProcessModel) {
        final String uid = businessProcessModel.getStockNotificationList().get(ZERO).getCurrentSite().getUid();
        return baseSiteService.getBaseSiteForUID(uid);
    }

    @Override
    protected CustomerModel getCustomer(final DistStockNotificationProcessModel businessProcessModel) {
        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final DistStockNotificationProcessModel businessProcessModel) {
        if (businessProcessModel.getLanguage() != null) {
            return businessProcessModel.getLanguage();
        }
        final LanguageModel stockNotificationLanguage = businessProcessModel.getStockNotificationList().get(ZERO).getLanguage();
        return getCommonI18NService().getLanguage(stockNotificationLanguage.getIsocode());
    }

    private List<StockNotificationProductResult> createStockNotificationResult(final List<DistStockNotificationModel> articleNumberList) {

        final List<StockNotificationProductResult> resultList = new ArrayList<>();

        for (final DistStockNotificationModel stockNotificationModel : articleNumberList) {
            ProductModel product = productService.getProductForCode(stockNotificationModel.getArticleNumber());
            Locale locale = LocaleUtils.toLocale(getEmailLanguage().getIsocode());

            final StockNotificationProductResult stockNotificationProductResult = new StockNotificationProductResult();
            stockNotificationProductResult.setProductImageUrl(stockNotificationModel.getProductImageUrl());
            stockNotificationProductResult.setProductPageUrl(stockNotificationModel.getProductPageUrl());
            stockNotificationProductResult.setProductDescription(product.getName(locale));
            stockNotificationProductResult.setCurrency(stockNotificationModel.getCurrency());
            stockNotificationProductResult.setProductPrice(String.valueOf(stockNotificationModel.getProductPrice()));
            stockNotificationProductResult.setProductStock(String.valueOf(stockNotificationModel.getAvailableStocks()));
            stockNotificationProductResult.setProductCode(stockNotificationModel.getArticleNumber());
            stockNotificationProductResult.setCustomerEmail(stockNotificationModel.getCustomerEmailId());
            stockNotificationProductResult.setLanguage(stockNotificationModel.getLanguage().getIsocode());
            stockNotificationProductResult.setManufacturer(stockNotificationModel.getManufacturer());

            resultList.add(stockNotificationProductResult);

        }
        return resultList;
    }

    /**
     * to retain existing functionality for old non-D4 sites
     */
    private String getFromDisplayName(final EmailPageModel emailPageModel) {
        String fromName = emailPageModel.getFromName();
        if ("D4".equalsIgnoreCase(fromName)) {
            return fromName;
        } else {
            return emailPageModel.getFromEmail();
        }
    }
}
