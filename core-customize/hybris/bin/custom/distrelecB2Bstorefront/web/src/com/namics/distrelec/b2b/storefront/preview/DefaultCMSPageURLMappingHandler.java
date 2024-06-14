/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.preview;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import com.namics.distrelec.b2b.storefront.servlets.util.FilterSpringUtil;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * Responsible for generating correct URL for given page.
 */
public class DefaultCMSPageURLMappingHandler implements URLMappingHandler
{
	private Map<String,String> pageMapping;

	/**
	 * Returns the relative URL for the specified page <code>page</code>.
	 * 
	 * @param httpRequest
	 *           HTTP request
	 * @return relative URL for the specified page
	 */
	@Override
	public String getPageUrl(final HttpServletRequest httpRequest, final PreviewDataModel previewDataModel)
	{
		if (previewDataModel != null)
		{
			final AbstractPageModel page = previewDataModel.getPage();

			final Map<String, String> pageMapping = getPageMapping();
			if (pageMapping != null && page != null)
			{
				// Lookup the page mapping by page UID
				final String pageUid = page.getUid();
				if (pageUid != null)
				{
					final String url = pageMapping.get(pageUid);
					if (url != null && !url.isEmpty())
					{
						return url;
					}
				}

				// For ContentPages also lookup by label
				if (page instanceof ContentPageModel)
				{
					final String pageLabel = ((ContentPageModel) page).getLabel();
					if (pageLabel != null)
					{
						final String url = pageMapping.get(pageLabel);
						if (url != null && !url.isEmpty())
						{
							return url;
						}
					}
				}
			}

			if (page instanceof ContentPageModel)
			{
				// Construct URL to preview the Page by UID
				return "/preview-content?uid=" + page.getUid();
			}

			if (page instanceof CategoryPageModel)
			{
				return getCategoryModelUrlResolver(httpRequest).resolve(getPreviewValueForCategoryPage(previewDataModel));
			}

			if (page instanceof ProductPageModel)
			{
				return getProductModelUrlResolver(httpRequest).resolve(getPreviewValueForProductPage(previewDataModel));
			}

			if(page instanceof DistManufacturerPageModel){
				return getManufacturerModelUrlResolver(httpRequest).resolve(getPreviewValueForManufacturerPage(previewDataModel));
			}
		}

		return "/";
	}

	protected DistManufacturerModel getPreviewValueForManufacturerPage(PreviewDataModel previewCtx){
		CMSSiteModel currentSite = previewCtx.getActiveSite();
		DistManufacturerModel ret = previewCtx.getPreviewManufacturer();

		if (ret == null) {
			if (currentSite != null) {
				ret = currentSite.getDefaultPreviewManufacturer();
			}
		}
		return ret;
	}

	protected CategoryModel getPreviewValueForCategoryPage(final PreviewDataModel previewCtx)
	{
		final CMSSiteModel currentSite = previewCtx.getActiveSite();
		CategoryModel ret = previewCtx.getPreviewCategory();

		if (ret == null)
		{
			if (currentSite != null)
			{
				ret = currentSite.getDefaultPreviewCategory();
			}
		}
		return ret;
	}

	protected ProductModel getPreviewValueForProductPage(final PreviewDataModel previewCtx)
	{
		final CMSSiteModel currentSite = previewCtx.getActiveSite();
		ProductModel ret = previewCtx.getPreviewProduct();

		if (ret == null)
		{
			if (currentSite != null)
			{
				ret = currentSite.getDefaultPreviewProduct();
			}
		}
		return ret;
	}

	protected UrlResolver<ProductModel> getProductModelUrlResolver(final HttpServletRequest httpRequest)
	{
		return FilterSpringUtil.getSpringBean(httpRequest, "productModelUrlResolver", UrlResolver.class);
	}

	protected UrlResolver<CategoryModel> getCategoryModelUrlResolver(final HttpServletRequest httpRequest)
	{
		return FilterSpringUtil.getSpringBean(httpRequest, "categoryModelUrlResolver", UrlResolver.class);
	}

	protected UrlResolver<DistManufacturerModel> getManufacturerModelUrlResolver(final HttpServletRequest httpRequest){
		return FilterSpringUtil.getSpringBean(httpRequest, "distManufacturerModelUrlResolver", UrlResolver.class);
	}

	// Optional
	public void setPageMapping(final Map<String,String> pageMapping)
	{
		this.pageMapping = pageMapping;
	}

	protected Map<String,String> getPageMapping()
	{
		return pageMapping;
	}
}
