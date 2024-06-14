package com.namics.distrelec.cms.rendering;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.rendering.PageRenderingService;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

public class DistPageRenderingService implements PageRenderingService {

    private static final Pattern MANUFACTURER_PATTERN = Pattern.compile("\\S*/manufacturer(/\\S*)?/(\\S+)");

    private static final Pattern CMS_ID_PATTERN = Pattern.compile("(/\\w{2})?(\\S*/cms)?(/[^\\?\\s]+).*");

    private static final Pattern PRODUCT_FAMILY_PATTERN = Pattern.compile("\\S*/pf/(\\S+)");

    private PageRenderingService defaultPageRenderingService;

    @Override
    public AbstractPageData getPageRenderingData(String pageTypeCode, String pageLabelOrId, String code) throws CMSItemNotFoundException {
        if (ContentPageModel._TYPECODE.equals(pageTypeCode)) {

            // check if it is manufacturer
            if (pageLabelOrId != null) {
                Matcher manufacturerMatcher = MANUFACTURER_PATTERN.matcher(pageLabelOrId);
                if (manufacturerMatcher.matches()) {
                    // search for manufacturer page in case of manufacturer label
                    pageTypeCode = DistManufacturerPageModel._TYPECODE;
                    code = manufacturerMatcher.group(2);
                    pageLabelOrId = null;
                }
            }

            // check if it is product family
            if (pageLabelOrId != null) {
                Matcher productFamilyMatcher = PRODUCT_FAMILY_PATTERN.matcher(pageLabelOrId);
                if (productFamilyMatcher.matches()) {
                    pageTypeCode = ProductFamilyPageModel._TYPECODE;
                    code = productFamilyMatcher.group(1);
                    pageLabelOrId = null;
                }
            }

            if (isNotBlank(pageLabelOrId)) {
                pageLabelOrId = normalize(pageLabelOrId);
            }
        }

        AbstractPageData pageData = defaultPageRenderingService.getPageRenderingData(pageTypeCode, pageLabelOrId, code);

        if (ProductPageModel._TYPECODE.equals(pageTypeCode) && code != null) {
            // remove robots tag if product code is supplied because it will be incorrectly cached
            // it should be returned only if code is null - check DistProductRobotsPopulator
            pageData.setRobotTag(null);
        }

        return pageData;
    }

    @Override
    public AbstractPageData getPageRenderingData(String pageId) throws CMSItemNotFoundException {
        return defaultPageRenderingService.getPageRenderingData(pageId);
    }

    @Override
    public SearchPageData<AbstractPageData> findAllRenderingPageData(String typeCode, SearchPageData searchPageData) {
        return defaultPageRenderingService.findAllRenderingPageData(typeCode, searchPageData);
    }

    protected String normalize(String pageLabelOrId) {
        Matcher matcher = CMS_ID_PATTERN.matcher(pageLabelOrId);
        if (!matcher.matches()) {
            return pageLabelOrId;
        }
        return matcher.group(3);
    }

    public void setDefaultPageRenderingService(PageRenderingService defaultPageRenderingService) {
        this.defaultPageRenderingService = defaultPageRenderingService;
    }
}
