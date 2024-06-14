package com.namics.hybris.ffsearch.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.service.category.dao.DistCategoryDao;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.service.DistFactFinderChannelDao;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DefaultUpdateCategoryTraversalDataService implements UpdateCategoryTraversalDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpdateCategoryTraversalDataService.class);

    private static final Set<String> IGNORE_PIM_CATEGORY_CODES = new HashSet<>(Arrays.asList("Familie", "Serie", "SerieSpezial"));

    private static final int CATEGORY_LEVEL_1 = 1;

    private static final int CATEGORY_LEVEL_2 = 2;

    private static final int CATEGORY_LEVEL_3 = 3;

    private static final int CATEGORY_LEVEL_4 = 4;

    private static final int CATEGORY_LEVEL_5 = 5;

    private static final int CATEGORY_LEVEL_6 = 6;

    private static final int CATEGORY_LEVEL_7 = 7;

    private static final int MAX_CATEGORY_LEVEL = 7;

    private CommonI18NService commonI18NService;

    private DistCategoryDao distCategoryDao;

    private DistFactFinderChannelDao distFactFinderChannelDao;

    private I18NService i18NService;

    private ModelService modelService;

    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    private Gson gson = new Gson();

    @Override
    public void updateCategoryTraversalData() {
        LOG.info("Start updating category traversal data");
        Set<Locale> languages = getAllLanguages();
        getI18NService().setLocalizationFallbackEnabled(true);

        for (int categoryLevelIt = 1; categoryLevelIt <= MAX_CATEGORY_LEVEL; categoryLevelIt++) {
            processCategoryLevel(categoryLevelIt, languages);
        }

        LOG.info("Category traversal data was updated");
    }

    private void processCategoryLevel(int categoryLevel, Set<Locale> languages) {
        LOG.info("Update category level: {}", categoryLevel);
        List<CategoryModel> categories = getDistCategoryDao().getCategoryByLevelRange(categoryLevel, categoryLevel + 1);
        for (CategoryModel category : categories) {
            processCategory(category, categoryLevel, languages);
        }
    }

    private void processCategory(CategoryModel category, int categoryLevel, Set<Locale> languages) {
        clearCatLevelFields(category, languages);
        setCategoryCode(category, category.getCode(), categoryLevel);
        boolean ignoreCategoryForPath = isCategoryIgnoredForPath(category);

        for (Locale language : languages) {
            setCategoryName(category, category.getName(language), language, categoryLevel);
            setCategoryNameSeo(category, category.getNameSeo(language), language, categoryLevel);
        }

        if (categoryLevel > 1) {
            processPathsForHigherLevelCategory(category, categoryLevel, ignoreCategoryForPath, languages);
        } else {
            processPathsForCategoryLevel1(category, ignoreCategoryForPath, languages);
        }

        getModelService().save(category);
    }

    private void processPathsForHigherLevelCategory(CategoryModel category, int categoryLevel, boolean ignoreCategoryForPath, Set<Locale> languages) {
        Optional<CategoryModel> superCategoryOptional = category.getSupercategories().stream()
                                                                .filter(c -> c.getLevel() != null && c.getLevel() == categoryLevel - 1)
                                                                .findAny();
        if (superCategoryOptional.isEmpty()) {
            return;
        }
        CategoryModel superCategory = superCategoryOptional.get();
        for (int subCategoryLevel = 1; subCategoryLevel < categoryLevel; subCategoryLevel++) {
            fillCategoryDataFromSuperCategory(category, superCategory, subCategoryLevel, languages);
        }

        fillCatPathSelectCodeFromSuperCategory(category, superCategory, ignoreCategoryForPath);
        fillCatPathSelectNameFromSuperCategory(category, superCategory, ignoreCategoryForPath, languages);
        fillCatPathExtensionFromSuperCategory(category, superCategory, ignoreCategoryForPath, languages);
    }

    private void fillCategoryDataFromSuperCategory(CategoryModel category, CategoryModel superCategory, int subCategoryLevel, Set<Locale> languages) {
        String subCategoryCode = getCategoryCode(superCategory, subCategoryLevel);
        setCategoryCode(category, subCategoryCode, subCategoryLevel);

        for (Locale language : languages) {
            String subCategoryName = getCategoryName(superCategory, language, subCategoryLevel);
            setCategoryName(category, subCategoryName, language, subCategoryLevel);

            String subCategoryNameSeo = getCategoryNameSeo(superCategory, language, subCategoryLevel);
            setCategoryNameSeo(category, subCategoryNameSeo, language, subCategoryLevel);
        }
    }

    private void fillCatPathSelectCodeFromSuperCategory(CategoryModel category, CategoryModel superCategory, boolean ignoreCategoryForPath) {
        String catPathSelectCode = superCategory.getCatPathSelectCode();
        if (!ignoreCategoryForPath) {
            if (isNotBlank(catPathSelectCode)) {
                catPathSelectCode += "/";
            }
            catPathSelectCode += category.getCode();
        }
        category.setCatPathSelectCode(catPathSelectCode);
    }

    private void fillCatPathSelectNameFromSuperCategory(CategoryModel category,
                                                        CategoryModel superCategory,
                                                        boolean ignoreCategoryForPath,
                                                        Set<Locale> languages) {
        for (Locale language : languages) {
            String catPathSelectName = superCategory.getCatPathSelectName(language);
            if (!ignoreCategoryForPath) {
                if (isNotBlank(catPathSelectName)) {
                    catPathSelectName += "/";
                }
                catPathSelectName += category.getNameSeo(language);
            }
            category.setCatPathSelectName(catPathSelectName, language);
        }
    }

    private void fillCatPathExtensionFromSuperCategory(CategoryModel category,
                                                       CategoryModel superCategory,
                                                       boolean ignoreCategoryForPath,
                                                       Set<Locale> languages) {
        languages.forEach(language -> calculateCatPathExtension(category, superCategory, language, ignoreCategoryForPath));
    }

    private void processPathsForCategoryLevel1(CategoryModel category, boolean ignoreCategoryForPath, Set<Locale> languages) {
        String catPathSelectCode = "";
        if (!ignoreCategoryForPath) {
            catPathSelectCode = category.getCode();
        }
        category.setCatPathSelectCode(catPathSelectCode);

        for (Locale language : languages) {
            String catPathSelectName = "";
            if (!ignoreCategoryForPath) {
                catPathSelectName = category.getNameSeo(language);
            }
            category.setCatPathSelectName(catPathSelectName, language);

            calculateCatPathExtension(category, language, ignoreCategoryForPath);
        }
    }

    private void clearCatLevelFields(CategoryModel category, Set<Locale> languages) {
        category.setCat1code(null);
        category.setCat2code(null);
        category.setCat3code(null);
        category.setCat4code(null);
        category.setCat5code(null);
        category.setCat6code(null);
        category.setCat7code(null);

        languages.forEach(lang -> {
            category.setCat1name(null, lang);
            category.setCat1nameSeo(null, lang);
            category.setCat2name(null, lang);
            category.setCat2nameSeo(null, lang);
            category.setCat3name(null, lang);
            category.setCat3nameSeo(null, lang);
            category.setCat4name(null, lang);
            category.setCat4nameSeo(null, lang);
            category.setCat5name(null, lang);
            category.setCat5nameSeo(null, lang);
            category.setCat6name(null, lang);
            category.setCat6nameSeo(null, lang);
            category.setCat7name(null, lang);
            category.setCat7nameSeo(null, lang);
        });
    }

    private boolean isCategoryIgnoredForPath(CategoryModel category) {
        DistPimCategoryTypeModel pimCategoryTypeModel = category.getPimCategoryType();
        boolean ignoreCategoryForPath;
        if (pimCategoryTypeModel != null) {
            String pimCategoryTypeCode = pimCategoryTypeModel.getCode();
            ignoreCategoryForPath = IGNORE_PIM_CATEGORY_CODES.contains(pimCategoryTypeCode);
        } else {
            ignoreCategoryForPath = false;
            LOG.warn("PIM category type is not assigned for category {}", category.getCode());
        }
        return ignoreCategoryForPath;
    }

    protected void calculateCatPathExtension(CategoryModel categoryModel, Locale language, boolean ignoreCategoryForPath) {
        calculateCatPathExtension(categoryModel, null, language, ignoreCategoryForPath);
    }

    protected void calculateCatPathExtension(CategoryModel categoryModel, CategoryModel parentCategoryModel, Locale language,
                                             boolean ignoreCategoryForPath) {
        List<CategoryUrl> categoryUrls = getInitialCatPathExtension(parentCategoryModel, language);

        if (!ignoreCategoryForPath) {
            CategoryUrl categoryUrl = new CategoryUrl();

            String imageUrl = getImageUrl(categoryModel);
            categoryUrl.setImageUrl(imageUrl);

            String url = getCategoryUrl(categoryModel, language);
            categoryUrl.setUrl(url);

            categoryUrls.add(categoryUrl);
        }

        String catPathExtensions = gson.toJson(categoryUrls);
        categoryModel.setCatPathExtensions(catPathExtensions, language);
    }

    private List<CategoryUrl> getInitialCatPathExtension(CategoryModel parentCategoryModel, Locale language) {
        if (parentCategoryModel != null && isNotBlank(parentCategoryModel.getCatPathExtensions(language))) {
            return gson.fromJson(parentCategoryModel.getCatPathExtensions(language),
                                 new TypeToken<Collection<CategoryUrl>>() { // empty constructor
                                 }.getType());
        }
        return new ArrayList<>();
    }

    private String getImageUrl(CategoryModel category) {
        MediaContainerModel primaryImage = category.getPrimaryImage();
        if (primaryImage != null) {
            Optional<MediaModel> mediaModelOptional = primaryImage.getMedias()
                                                                  .stream()
                                                                  .filter(mediaModel -> MediaFormat.LANDSCAPE_SMALL.equals(
                                                                    mediaModel.getMediaFormat().getQualifier()))
                                                                  .findAny();
            if (mediaModelOptional.isPresent()) {
                MediaModel mediaModel = mediaModelOptional.get();
                return mediaModel.getInternalURL();
            }
        }
        return null;
    }

    private String getCategoryUrl(CategoryModel category, Locale language) {
        LanguageModel lang = this.commonI18NService.getLanguage(language.getLanguage());
        this.commonI18NService.setCurrentLanguage(lang);
        String resolvedURL = categoryModelUrlResolver.resolve(category);
        LOG.debug("Resolved category-url for category[{}] is:{}", category.getCode(), resolvedURL);
        return resolvedURL;
    }

    protected Set<Locale> getAllLanguages() {
        List<DistFactFinderExportChannelModel> allChannels = getDistFactFinderChannelDao().getAllChannels();
        return allChannels.stream()
                          .map(DistFactFinderExportChannelModel::getLanguage)
                          .map(LanguageModel::getIsocode)
                          .map(isocode -> getCommonI18NService().getLocaleForIsoCode(isocode))
                          .collect(Collectors.toSet());
    }

    protected String getCategoryCode(CategoryModel categoryModel, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                return categoryModel.getCat1code();
            case CATEGORY_LEVEL_2:
                return categoryModel.getCat2code();
            case CATEGORY_LEVEL_3:
                return categoryModel.getCat3code();
            case CATEGORY_LEVEL_4:
                return categoryModel.getCat4code();
            case CATEGORY_LEVEL_5:
                return categoryModel.getCat5code();
            case CATEGORY_LEVEL_6:
                return categoryModel.getCat6code();
            case CATEGORY_LEVEL_7:
                return categoryModel.getCat7code();
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void setCategoryCode(CategoryModel categoryModel, String code, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                categoryModel.setCat1code(code);
                break;
            case CATEGORY_LEVEL_2:
                categoryModel.setCat2code(code);
                break;
            case CATEGORY_LEVEL_3:
                categoryModel.setCat3code(code);
                break;
            case CATEGORY_LEVEL_4:
                categoryModel.setCat4code(code);
                break;
            case CATEGORY_LEVEL_5:
                categoryModel.setCat5code(code);
                break;
            case CATEGORY_LEVEL_6:
                categoryModel.setCat6code(code);
                break;
            case CATEGORY_LEVEL_7:
                categoryModel.setCat7code(code);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected String getCategoryName(CategoryModel categoryModel, Locale language, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                return categoryModel.getCat1name(language);
            case CATEGORY_LEVEL_2:
                return categoryModel.getCat2name(language);
            case CATEGORY_LEVEL_3:
                return categoryModel.getCat3name(language);
            case CATEGORY_LEVEL_4:
                return categoryModel.getCat4name(language);
            case CATEGORY_LEVEL_5:
                return categoryModel.getCat5name(language);
            case CATEGORY_LEVEL_6:
                return categoryModel.getCat6name(language);
            case CATEGORY_LEVEL_7:
                return categoryModel.getCat7name(language);
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void setCategoryName(CategoryModel categoryModel, String name, Locale language, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                categoryModel.setCat1name(name, language);
                break;
            case CATEGORY_LEVEL_2:
                categoryModel.setCat2name(name, language);
                break;
            case CATEGORY_LEVEL_3:
                categoryModel.setCat3name(name, language);
                break;
            case CATEGORY_LEVEL_4:
                categoryModel.setCat4name(name, language);
                break;
            case CATEGORY_LEVEL_5:
                categoryModel.setCat5name(name, language);
                break;
            case CATEGORY_LEVEL_6:
                categoryModel.setCat6name(name, language);
                break;
            case CATEGORY_LEVEL_7:
                categoryModel.setCat7name(name, language);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected String getCategoryNameSeo(CategoryModel categoryModel, Locale language, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                return categoryModel.getCat1nameSeo(language);
            case CATEGORY_LEVEL_2:
                return categoryModel.getCat2nameSeo(language);
            case CATEGORY_LEVEL_3:
                return categoryModel.getCat3nameSeo(language);
            case CATEGORY_LEVEL_4:
                return categoryModel.getCat4nameSeo(language);
            case CATEGORY_LEVEL_5:
                return categoryModel.getCat5nameSeo(language);
            case CATEGORY_LEVEL_6:
                return categoryModel.getCat6nameSeo(language);
            case CATEGORY_LEVEL_7:
                return categoryModel.getCat7nameSeo(language);
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void setCategoryNameSeo(CategoryModel categoryModel, String name, Locale language, int level) {
        switch (level) {
            case CATEGORY_LEVEL_1:
                categoryModel.setCat1nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_2:
                categoryModel.setCat2nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_3:
                categoryModel.setCat3nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_4:
                categoryModel.setCat4nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_5:
                categoryModel.setCat5nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_6:
                categoryModel.setCat6nameSeo(name, language);
                break;
            case CATEGORY_LEVEL_7:
                categoryModel.setCat7nameSeo(name, language);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static class CategoryUrl {
        private String url;

        private String imageUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistCategoryDao getDistCategoryDao() {
        return distCategoryDao;
    }

    public void setDistCategoryDao(DistCategoryDao distCategoryDao) {
        this.distCategoryDao = distCategoryDao;
    }

    public DistFactFinderChannelDao getDistFactFinderChannelDao() {
        return distFactFinderChannelDao;
    }

    public void setDistFactFinderChannelDao(DistFactFinderChannelDao distFactFinderChannelDao) {
        this.distFactFinderChannelDao = distFactFinderChannelDao;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }
}
