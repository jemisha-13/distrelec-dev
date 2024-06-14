package com.namics.distrelec.b2b.storefront.etag.impl;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistComponentGroupModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistMainNavigationComponentModel;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.storefront.etag.ContentPageETagData;
import com.namics.distrelec.b2b.storefront.etag.ETagCalculationService;
import com.namics.distrelec.b2b.storefront.etag.ProductPageETagData;
import com.namics.distrelec.b2b.core.version.GitVersionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ETagCalculationServiceImpl implements ETagCalculationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private DistSalesOrgProductService salesOrgProductService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private GitVersionService gitVersionService;

    @Autowired
    private CMSPageService cmsPageService;

    @Autowired
    private DistPriceService priceService;

    @Override
    public String calculateProductPageETag(String productCode) throws CMSItemNotFoundException {
        ProductModel productModel = productService.getProductForCode(productCode);
        String revision = gitVersionService.getRevision();

        AbstractPageModel productPage = cmsPageService.getPageForProduct(productModel);

        Date lastUpdated = productModel.getModifiedtime();

        DistSalesOrgProductModel salesOrgProduct = salesOrgProductService.getCurrentSalesOrgProduct(productModel);
        if (salesOrgProduct.getModifiedtime().compareTo(lastUpdated) > 0) {
            lastUpdated = salesOrgProduct.getModifiedtime();
        }

        List<PriceInformation> priceInformations = priceService.getPriceInformationsForProduct(productModel);
        Optional<Date> lastPriceUpdateTime = priceInformations.stream()
                .map(PriceInformation::getQualifiers)
                .map(qualifiers -> qualifiers.get("pricerow"))
                .map(DistPriceRow.class::cast)
                .map(DistPriceRow::getModificationTime)
                .max(Comparator.naturalOrder());

        if(lastPriceUpdateTime.isPresent() && lastPriceUpdateTime.get().compareTo(lastUpdated) > 0){
            lastUpdated = lastPriceUpdateTime.get();
        }

        Date pageLastUpdated = calculatePageLastModifiedDate(productPage);

        if (pageLastUpdated.compareTo(lastUpdated) > 0) {
            lastUpdated = pageLastUpdated;
        }

        ProductPageETagData eTagData = new ProductPageETagData(productCode,
                lastUpdated,
                getSiteUid(),
                getLanguage(),
                getChannelId(),
                revision);

        return encodeHashcode(eTagData);
    }

    @Override
    public String calculateContentPageETag(ContentPageModel contentPage) throws CMSItemNotFoundException {
        Date lastUpdated = calculatePageLastModifiedDate(contentPage);

        ContentPageETagData eTagData = new ContentPageETagData(contentPage.getLabel(),
                lastUpdated,
                getSiteUid(),
                getLanguage(),
                getChannelId(),
                getRevision());

        return encodeHashcode(eTagData);
    }

    private String getSiteUid() {
        return cmsSiteService.getCurrentSite().getUid();
    }

    private String getLanguage() {
        return i18NService.getCurrentLocale().toString();
    }

    private String getChannelId() {
        return baseStoreService.getCurrentBaseStore().getChannel().getCode();
    }

    private String encodeHashcode(Object data) {
        return Base64.encodeBase64String(String.valueOf(data.hashCode()).getBytes(StandardCharsets.UTF_8));
    }

    private String getRevision() {
        return gitVersionService.getRevision();
    }

    private Date calculatePageLastModifiedDate(AbstractPageModel page) {
        Date lastUpdated = page.getModifiedtime();

        Optional<Date> lastModifiedComponentDate = cmsPageService.getContentSlotsForPage(page).stream()
                .map(this::calculateContentSlotLastModifiedTime)
                .max(Comparator.naturalOrder());

        if (lastModifiedComponentDate.isPresent()) {
            if (lastModifiedComponentDate.get().compareTo(lastUpdated) > 0) {
                lastUpdated = lastModifiedComponentDate.get();
            }
        }

        return lastUpdated;
    }

    private Date calculateContentSlotLastModifiedTime(ContentSlotData contentSlotData) {
        ContentSlotModel contentSlot = contentSlotData.getContentSlot();
        Date lastUpdated = contentSlot.getModifiedtime();

        for (AbstractCMSComponentModel cmsComponent : contentSlot.getCmsComponents()) {
            Date cmsComponentLastModifiedTime = calculateCmsComponentLastModifiedDate(cmsComponent);
            if (cmsComponentLastModifiedTime.compareTo(lastUpdated) > 0) {
                lastUpdated = cmsComponentLastModifiedTime;
            }
        }

        return lastUpdated;
    }

    private Date calculateCmsComponentLastModifiedDate(AbstractCMSComponentModel component) {
        Date lastUpdated = component.getModifiedtime();

        if (component instanceof DistComponentGroupModel) {
            DistComponentGroupModel groupComponent = (DistComponentGroupModel) component;
            for (SimpleCMSComponentModel subComponent : groupComponent.getComponents()) {
                Date subComponentModifiedTime = calculateCmsComponentLastModifiedDate(subComponent);
                if (subComponentModifiedTime.compareTo(lastUpdated) > 0) {
                    lastUpdated = subComponentModifiedTime;
                }
            }
        }

        if (component instanceof DistMainNavigationComponentModel) {
            DistMainNavigationComponentModel navigationComponent = (DistMainNavigationComponentModel) component;
            Date navigationNodeModifiedTime = calculateNavigationNodeLastModifiedDate(navigationComponent.getRootNavigationNode());
            if (navigationNodeModifiedTime.compareTo(lastUpdated) > 0) {
                lastUpdated = navigationNodeModifiedTime;
            }
        }

        return lastUpdated;
    }

    private Date calculateNavigationNodeLastModifiedDate(CMSNavigationNodeModel node) {
        Date lastUpdated = node.getModifiedtime();

        for (CMSNavigationEntryModel navigationEntry : node.getEntries()) {
            Date entryLastModifiedTime = navigationEntry.getModifiedtime();
            if (entryLastModifiedTime.compareTo(lastUpdated) > 0) {
                lastUpdated = entryLastModifiedTime;
            }

            ItemModel navigationItem = navigationEntry.getItem();
            if(navigationItem != null) {
                Date navigationItemLastModifiedTime = navigationItem.getModifiedtime();
                if (navigationItemLastModifiedTime.compareTo(lastUpdated) > 0) {
                    lastUpdated = navigationItemLastModifiedTime;
                }
            }
        }

        for (CMSNavigationNodeModel subNode : node.getChildren()) {
            Date subNodeLastModifiedTime = calculateNavigationNodeLastModifiedDate(subNode);
            if (subNodeLastModifiedTime.compareTo(lastUpdated) > 0) {
                lastUpdated = subNodeLastModifiedTime;
            }
        }

        return lastUpdated;
    }

}
