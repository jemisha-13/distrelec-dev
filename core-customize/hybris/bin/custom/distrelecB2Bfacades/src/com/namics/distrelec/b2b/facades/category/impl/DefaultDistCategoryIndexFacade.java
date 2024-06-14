package com.namics.distrelec.b2b.facades.category.impl;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.EMPTY;
import static java.util.Comparator.comparing;

public class DefaultDistCategoryIndexFacade implements DistCategoryIndexFacade {

	private static final String CATEGORY_INDEX_LANG = "categoryIndex_lang";
	private static final String CATEGORY_INDEX = "categoryIndex";
	private static final String CATEGORY_NAV_ROOT_NODE = "MainCategoryNavNode";

	@Autowired
	private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

	@Autowired
	private CMSNavigationService cmsNavigationService;

	@Autowired
	private I18NService i18nService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private Converter<MediaModel, ImageData> imageConverter;

	@Autowired
	private DistCategoryService distCategoryService;

	@Override
	public List getCategoryIndexData() {

		final String currentLang = getI18nService().getCurrentLocale().getLanguage();
		if (!currentLang.equals(getSessionService().getAttribute(CATEGORY_INDEX_LANG))) {
			getSessionService().removeAttribute(CATEGORY_INDEX);
		}

		final List<DistCategoryIndexData> categoryIndexData = getSessionService().getOrLoadAttribute(CATEGORY_INDEX,
				createSessionAttributeLoader(currentLang));
		if (categoryIndexData == null) {
			return Collections.EMPTY_LIST;
		} else {
			return categoryIndexData.stream().filter(Objects::nonNull).filter(DistCategoryIndexData::hasChildren)
					.filter(DistCategoryIndexData::urlIsNotNull).filter(DistCategoryIndexData::isNotASpecialShop)
					.collect(Collectors.toList());
		}
	}

	/**
	 * @return the list of top category data to be displayed on the Category
	 * Index Page.
	 */
	@Override
	public List getTopCategoryIndexData() {

		final String currentLang = getI18nService().getCurrentLocale().getLanguage();
		if (!currentLang.equals(getSessionService().getAttribute(CATEGORY_INDEX_LANG))) {
			getSessionService().removeAttribute(CATEGORY_INDEX);
		}

		final List<DistCategoryIndexData> categoryIndexData = getSessionService().getOrLoadAttribute(CATEGORY_INDEX,
				createSessionAttributeLoader(currentLang));
		if (categoryIndexData == null) {
			return Collections.EMPTY_LIST;
		} else {
			return categoryIndexData.stream().filter(Objects::nonNull).filter(DistCategoryIndexData::hasChildren)
					.filter(DistCategoryIndexData::urlIsNotNull).filter(DistCategoryIndexData::isNotASpecialShop)
					.filter(this::isTopCategory).collect(Collectors.toList());
		}
	}

	/**
	 * @return the list of top category data to be displayed on the Category - OCC only
	 */
	@Override
	public List<DistCategoryIndexData> getTopCategoryDataForOCC()
	{
		final Configuration configuration = getConfigurationService().getConfiguration();
		final String eligibleCategories = configuration.getString("homepage.top.categories");

		final List<String> eligibleCategoryCodes = Arrays.stream(eligibleCategories.split(","))
				.map(String::trim).collect(Collectors.toList());
		List<CategoryModel> categories = distCategoryService.getCategoriesForCode(eligibleCategoryCodes);
		List<DistCategoryIndexData> filteredCategories = categories.stream().filter(c -> c != null && CollectionUtils.isNotEmpty(c.getCategories())).map(cat -> populateCatIndexData(cat)).collect(Collectors.toList());
		return filteredCategories;
	}

	private DistCategoryIndexData populateCatIndexData(final CategoryModel category)
	{

			DistCategoryIndexData categoryIndexData = new DistCategoryIndexData();
			categoryIndexData.setUrl(categoryModelUrlResolver.resolve(category));
			categoryIndexData.setCode(category.getCode());
			categoryIndexData.setNameEN(category.getName(Locale.ENGLISH));
			categoryIndexData.setName(category.getName());
			return categoryIndexData;

	}

	private boolean isTopCategory(final DistCategoryIndexData data) {
		final Configuration configuration = getConfigurationService().getConfiguration();
		final String eligibleCategories = configuration.getString("homepage.top.categories");

		final Set<String> eligibleCategoryCodes = Arrays.stream(eligibleCategories.split(","))
				.map(String::trim).collect(Collectors.toSet());
		return eligibleCategoryCodes.contains(data.getCode());
	}

	private SessionService.SessionAttributeLoader<List<DistCategoryIndexData>> createSessionAttributeLoader(
			final String currentLang) {
		return () -> {
			getSessionService().setAttribute(CATEGORY_INDEX_LANG, currentLang);
			try {
				final CMSNavigationNodeModel categoryRootNode = cmsNavigationService
						.getNavigationNodeForId(CATEGORY_NAV_ROOT_NODE);
				if (categoryRootNode == null) {
					return Collections.emptyList();
				}

				return convert(categoryRootNode.getChildren());
			} catch (final CMSItemNotFoundException e) {
				return Collections.emptyList();
			}
		};
	}

	/**
	 * Convert a category CMS navigation node to a category index data
	 *
	 * @param node the source CMS navigation node
	 * @return an instance of {@link DistCategoryIndexData}
	 */
	private DistCategoryIndexData convert(final CMSNavigationNodeModel node) {
		if (node == null) {
			return null;
		}

		final DistCategoryIndexData categoryData = new DistCategoryIndexData();
		final String title = StringUtils.isEmpty(node.getTitle()) ? EMPTY : node.getTitle();
		categoryData.setName(title);
		if (CollectionUtils.isNotEmpty(node.getEntries())
				&& node.getEntries().get(0).getItem() instanceof CategoryModel) {
			final CategoryModel category = (CategoryModel) node.getEntries().get(0).getItem();
			categoryData.setUrl(categoryModelUrlResolver.resolve(category));
			categoryData.setCode(category.getCode());
			final Collection<MediaModel> images = category.getPrimaryImage() !=null?category.getPrimaryImage().getMedias():Collections.EMPTY_LIST;
			if (CollectionUtils.isNotEmpty(images)) {
				categoryData.setImage(imageConverter.convert(images.iterator().next()));
			}
		}
		categoryData.setNameEN(node.getTitle(Locale.ENGLISH));
		categoryData.setChildren(convert(node.getChildren()));

		return categoryData;
	}

	private List<DistCategoryIndexData> convert(final List<CMSNavigationNodeModel> nodes) {
		if (CollectionUtils.isEmpty(nodes)) {
			return Collections.emptyList();
		}

		final List<DistCategoryIndexData> categories = new ArrayList<>();
		for (final CMSNavigationNodeModel node : nodes) {
			categories.add(convert(node));
		}

		categories.sort(comparing(CategoryData::getName));

		return categories;
	}

	public I18NService getI18nService() {
		return i18nService;
	}

	public void setI18nService(final I18NService i18nService) {
		this.i18nService = i18nService;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}

