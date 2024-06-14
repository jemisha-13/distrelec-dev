package com.namics.distrelec.b2b.core.service.url.impl;

import java.util.List;
import java.util.Locale;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

public class DefaultDistProductFamilyUrlResolver extends DefaultCategoryModelUrlResolver implements DistUrlResolver<CategoryModel> {

    private CommonI18NService commonI18NService;
    private DistCategoryService distCategoryService;

    @Override
    public String resolve(CategoryModel category, BaseSiteModel baseSite, String language) {
        return resolve(category, baseSite, language, false);
    }

    @Override
    public String resolve(CategoryModel category, BaseSiteModel baseSite, Locale locale) {
        return resolve(category, baseSite, locale.getLanguage(), false);
    }

    @Override
    public String resolve(CategoryModel productFamily, BaseSiteModel baseSite, String language, boolean isCanonical) {
        assertProductFamilyCategory(productFamily);
        final Locale locale = LocaleUtils.toLocale(language);
        String productFamilyName = productFamily.getNameSeo(locale);
        if (StringUtils.isBlank(productFamilyName)) {
            productFamilyName = UrlResolverUtils.normalize(productFamily.getName(locale));
            if(StringUtils.isBlank(productFamilyName)){
                //Use the English as failsafe as it is the master language in PIM
                productFamilyName = UrlResolverUtils.normalize(productFamily.getName(Locale.ENGLISH));
            }
        }
        final String productFamilyCode = productFamily.getCode();

        if (StringUtils.isEmpty(productFamilyName) || StringUtils.isEmpty(productFamilyCode)) {
            return "/noProductFamily";
        }
        // Replace pattern values
        String url = getPattern();
        if(isCanonical && null!=url && !url.isEmpty() ){
            url = url.replace("/{language}", "");
        }else{
            url = url.replace("{language}", locale.getLanguage());
        }

        url = url.replace("{product-family-name}", productFamilyName);
        url = url.replace("{product-family-code}", productFamilyCode);

        return UrlResolverUtils.normalize(url);

    }

    @Override
    protected String resolveInternal(final CategoryModel source) {
        final String language = getCommonI18NService().getCurrentLanguage().getIsocode();
        return resolve(source, getBaseSiteService().getCurrentBaseSite(), language);
    }

    protected void assertProductFamilyCategory(CategoryModel category) {
        if (!getDistCategoryService().isProductFamily(category)) {
            throw new IllegalArgumentException(String.format("Category %s is not a product family", category.getCode()));
        }
    }

    protected String buildPathString(final List<CategoryModel> path, final Locale locale) {
        final StringBuilder result = new StringBuilder();

        for (final CategoryModel currentCat : path) {
            if (currentCat.getLevel() != null && currentCat.getLevel().intValue() != 0 && StringUtils.isNotBlank(currentCat.getNameSeo(locale))) {
                result.append(currentCat.getNameSeo(locale)).append('/');
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    @Required
    public void setDistCategoryService(DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }
}
