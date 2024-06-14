/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.*;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.model.CategorySEOSectionModel;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

import de.hybris.platform.category.model.CategoryModel;

/**
 * Converts a "classification" or a "product" XML element into a hybris product category model.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultCategoryElementConverter extends AbstractCategoryElementConverter {

    private static final Logger LOG = LogManager.getLogger(DefaultCategoryElementConverter.class);

    @Override
    public void convertCategoryStructure(final Element source, final CategoryModel target, final ImportContext importContext) {
        CategoryModel superCategory = null;

        final String superCategoryCode = getSuperCategoryCode(source, importContext);
        if (StringUtils.isNotBlank(superCategoryCode)) {
            superCategory = importContext.getCategoryCache().get(superCategoryCode);
            if (superCategory == null) {
                LOG.error("Could not find super category for code [{}] of category with code [{}]", superCategoryCode, target.getCode());
            }
        }

        if (superCategory == null) {
            target.setSupercategories(Collections.<CategoryModel> emptyList());
        } else {
            target.setSupercategories(Collections.singletonList(superCategory));
        }

        setCategoryLevel(target, superCategory, importContext);
        // setPimCategoryType(target, source);

        // DISTRELEC-11130
        if (target.getLevel() != null && target.getLevel() == 1) {
            target.setPimSortingNumber(String.valueOf(importContext.getSortingIndex()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#convert(org.dom4j.Element,
     * java.lang.Object, com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext, java.lang.String)
     */
    @Override
    public void convert(final Element source, final CategoryModel target, final ImportContext importContext, final String hash) {

        final String mediaContainerQualifier = source.valueOf(XP_MEDIA_CONTAINER);
        if (StringUtils.isNotBlank(mediaContainerQualifier)) {
            try {
                target.setPrimaryImage(getMediaContainerService().getMediaContainerForQualifier(mediaContainerQualifier));
            } catch (UnknownIdentifierException e) {
                final String categoryCode = target.getCode();
                importContext.getClassificationMediaContainerReferences().put(categoryCode, mediaContainerQualifier);
            }
        }

        target.setAllowedPrincipals(getAllowedPrincipals());
        getProductLineCountryElementConverter().convert(source, target);
        // DISTRELEC-10821 Taxonomy > SEO Redirects & Fluctuation Handling
        populatePredecessorInfo(source, target);

        final List<Element> nameElements = source.selectNodes(XP_NAME);
        for (Element nameElement : nameElements) {
            final String name = truncate(nameElement.getTextTrim(), MAX_COLUMN_LENGTH_DEFAULT, getId(source), XP_NAME);

            Locale locale = getConverterLanguageUtil().getLocaleForElement(nameElement);
            if (StringUtils.isNotBlank(name)) {
                target.setName(replacingForFF(name), locale);
                target.setNameSeo(UrlResolverUtils.normalize(name, true), locale);
            } else {
                LOG.warn("Category with code [{}] has no name for Locale [{}]", target.getCode(), locale);
                target.setName("Unnamed " + target.getCode());
                target.setNameSeo("Unnamed " + target.getCode());
            }
        }
        importIntroText(source, target);
        importSeoData(source, target);
    }

    private void importIntroText(Element source, CategoryModel target) {
        importSingleValuedIntroText(source, target);
        importMultiValuedIntroText(source, target);
    }

    private void importSingleValuedIntroText(Element source, CategoryModel target) {
        List<Element> classificationIntroElements = source.selectNodes("MetaData/Value[@AttributeID='DIS_ClassificationIntro']");
        for (Element element : classificationIntroElements) {
            Locale locale = getConverterLanguageUtil().getLocaleForElement(element);
            String text = element.getText();
            target.setIntroText(text, locale);
        }
    }

    private void importMultiValuedIntroText(Element source, CategoryModel target) {
        List<Element> classificationIntroElements = source.selectNodes("MetaData/ValueGroup[@AttributeID='DIS_ClassificationIntro']/Value");
        for (Element element : classificationIntroElements) {
            Locale locale = getConverterLanguageUtil().getLocaleForElement(element);
            String text = element.getText();
            target.setIntroText(text, locale);
        }
    }

    private void importSeoData(Element source, CategoryModel target) {
        importSeoSections(source, target);
        importSeoMetaDescription(source, target);
        importSeoMetaTitle(source, target);
    }

    private void importSeoMetaTitle(Element source, CategoryModel target) {
        importSingleValuedSeoMetaTitle(source, target);
        importMultiValuedSeoMetaTitle(source, target);
    }

    private void importSingleValuedSeoMetaTitle(Element source, CategoryModel target) {
        try {
            List<Element> metaTitleElements = source.selectNodes("MetaData/Value[@AttributeID='DIS_MetaTitle_Class']");
            metaTitleElements.stream().forEach(metaTitleElement -> {
                Locale locale = getConverterLanguageUtil().getLocaleForElement(metaTitleElement);
                String value = metaTitleElement.getText();
                target.setSeoMetaTitle(value, locale);
            });
        } catch (Exception e) {
            LOG.error("Failed to import SEO Meta title for category {}. Error: {}", target.getCode(), e.getMessage());
        }
    }

    private void importMultiValuedSeoMetaTitle(Element source, CategoryModel target) {
        try {
            List<Element> metaTitleElements = source.selectNodes("MetaData/ValueGroup[@AttributeID='DIS_MetaTitle_Class']/Value");
            metaTitleElements.stream().forEach(metaTitleElement -> {
                Locale locale = getConverterLanguageUtil().getLocaleForElement(metaTitleElement);
                String value = metaTitleElement.getText();
                target.setSeoMetaTitle(value, locale);
            });
        } catch (Exception e) {
            LOG.error("Failed to import SEO Meta title for category {}. Error: {}", target.getCode(), e.getMessage());
        }
    }

    private void importSeoMetaDescription(Element source, CategoryModel target) {
        importSingleValuedSeoMetaDescription(source, target);
        importMultiValuedSeoMetaDescription(source, target);
    }

    private void importSingleValuedSeoMetaDescription(Element source, CategoryModel target) {
        try {
            List<Element> metaDescriptionElements = source.selectNodes("MetaData/Value[@AttributeID='DIS_MetaDescription_Class']");
            metaDescriptionElements.stream().forEach(metaDescriptionElement -> {
                Locale locale = getConverterLanguageUtil().getLocaleForElement(metaDescriptionElement);
                String value = metaDescriptionElement.getText();
                target.setSeoMetaDescription(truncate(value, MAX_COLUMN_LENGTH_DEFAULT), locale);
            });
        } catch (Exception e) {
            LOG.error("Failed to import SEO Meta description for category {}. Error: {}", target.getCode(), e.getMessage());
        }
    }

    private void importMultiValuedSeoMetaDescription(Element source, CategoryModel target) {
        try {
            List<Element> metaDescriptionElements = source.selectNodes("MetaData/ValueGroup[@AttributeID='DIS_MetaDescription_Class']/Value");
            metaDescriptionElements.stream().forEach(metaDescriptionElement -> {
                Locale locale = getConverterLanguageUtil().getLocaleForElement(metaDescriptionElement);
                String value = metaDescriptionElement.getText();
                target.setSeoMetaDescription(truncate(value, MAX_COLUMN_LENGTH_DEFAULT), locale);
            });
        } catch (Exception e) {
            LOG.error("Failed to import SEO Meta description for category {}. Error: {}", target.getCode(), e.getMessage());
        }
    }

    private void importSeoSections(Element source, CategoryModel target) {
        try {
            List<Element> subHeaderSections = source.selectNodes("MetaData/ValueGroup[starts-with(@AttributeID, 'DIS_SubHeader')]");
            List<Element> textSections = source.selectNodes("MetaData/ValueGroup[starts-with(@AttributeID, 'DIS_TextSection')]");

            boolean multiValuedHeaders;
            boolean multiValuedTexts;
            Map<Integer, List<Element>> subHeaderSectionsPerPosition;
            Map<Integer, List<Element>> textSectionsPerPosition;

            if (CollectionUtils.isNotEmpty(subHeaderSections)) {
                multiValuedHeaders = true;
            } else {
                multiValuedHeaders = false;
                subHeaderSections = source.selectNodes("MetaData/Value[starts-with(@AttributeID, 'DIS_SubHeader')]");
            }

            subHeaderSectionsPerPosition = subHeaderSections.stream()
                                                            .collect(Collectors.groupingBy(subHeaderSection -> {
                                                                String attributeId = subHeaderSection.attribute("AttributeID").getText();
                                                                String positionString = attributeId.substring("DIS_SubHeader".length());
                                                                return Integer.parseInt(positionString);
                                                            }));

            if (CollectionUtils.isNotEmpty(textSections)) {
                multiValuedTexts = true;
            } else {
                multiValuedTexts = false;
                textSections = source.selectNodes("MetaData/Value[starts-with(@AttributeID, 'DIS_TextSection')]");
            }

            textSectionsPerPosition = textSections.stream()
                                                  .collect(Collectors.groupingBy(subHeaderSection -> {
                                                      String attributeId = subHeaderSection.attribute("AttributeID").getText();
                                                      String positionString = attributeId.substring("DIS_TextSection".length());
                                                      return Integer.parseInt(positionString);
                                                  }));

            int maxPos = -1;
            maxPos = subHeaderSectionsPerPosition.keySet().stream().mapToInt(pos -> pos)
                                                 .max().orElse(maxPos);
            maxPos = textSectionsPerPosition.keySet().stream().mapToInt(pos -> pos)
                                            .max().orElse(maxPos);
            if (maxPos != -1) {
                ensureSeoSectionListCapacity(target, maxPos);
                importSeoSectionsHeaders(target, subHeaderSectionsPerPosition, multiValuedHeaders);
                importSeoSectionsTexts(target, textSectionsPerPosition, multiValuedTexts);
                getModelService().saveAll(target.getSeoSections());
            } else {
                ensureSeoSectionListCapacity(target, 0);
            }
        } catch (Exception e) {
            LOG.error("Failed to import SEO sections for category {}. Error: {}", target.getCode(), e.getMessage());
        }
    }

    private void importSeoSectionsTexts(CategoryModel target, Map<Integer, List<Element>> textSectionsPerPosition, boolean multiValued) {
        for (Map.Entry<Integer, List<Element>> subHeaderEntry : textSectionsPerPosition.entrySet()) {
            if (multiValued) {
                List<Element> valueGroupElements = subHeaderEntry.getValue();
                int position = subHeaderEntry.getKey() - 1;
                for (Element valueGroupElement : valueGroupElements) {
                    for (Element valueElement : (List<Element>) valueGroupElement.selectNodes("Value")) {
                        Locale locale = getConverterLanguageUtil().getLocaleForElement(valueElement);
                        String value = valueElement.getText();
                        target.getSeoSections().get(position).setText(value, locale);
                    }
                }
            } else {
                List<Element> valueElements = subHeaderEntry.getValue();
                int position = subHeaderEntry.getKey() - 1;
                for (Element valueElement : valueElements) {
                    Locale locale = getConverterLanguageUtil().getLocaleForElement(valueElement);
                    String value = valueElement.getText();
                    target.getSeoSections().get(position).setText(value, locale);
                }
            }
        }
    }

    private void importSeoSectionsHeaders(CategoryModel target, Map<Integer, List<Element>> subHeaderSectionsPerPosition, boolean multiValued) {
        for (Map.Entry<Integer, List<Element>> subHeaderEntry : subHeaderSectionsPerPosition.entrySet()) {
            if (multiValued) {
                List<Element> valueGroupElements = subHeaderEntry.getValue();
                int position = subHeaderEntry.getKey() - 1;
                for (Element valueGroupElement : valueGroupElements) {
                    for (Element valueElement : (List<Element>) valueGroupElement.selectNodes("Value")) {
                        Locale locale = getConverterLanguageUtil().getLocaleForElement(valueElement);
                        String value = valueElement.getText();
                        target.getSeoSections().get(position).setHeader(value, locale);
                    }
                }
            } else {
                List<Element> valueElements = subHeaderEntry.getValue();
                int position = subHeaderEntry.getKey() - 1;
                for (Element valueElement : valueElements) {
                    Locale locale = getConverterLanguageUtil().getLocaleForElement(valueElement);
                    String value = valueElement.getText();
                    target.getSeoSections().get(position).setHeader(value, locale);
                }
            }
        }
    }

    private void ensureSeoSectionListCapacity(CategoryModel target, int maxPos) {
        List<CategorySEOSectionModel> seoSections = new ArrayList<>(maxPos);
        for (int i = 0; i < maxPos; i++) {
            seoSections.add(getModelService().create(CategorySEOSectionModel.class));
        }
        target.setSeoSections(seoSections);
    }

    private String getSuperCategoryCode(final Element element, final ImportContext importContext) {
        String superCategoryCode = null;

        String parentId;
        // return super-category only if current category is not a root category
        if (ELEMENT_CLASSIFICATION.equals(element.getName())) {
            parentId = element.getParent().attributeValue(ATTRIBUTE_ID);
        } else {
            parentId = element.attributeValue(ATTRIBUTE_PARENT_ID);
        }

        if (parentId != null) {
            parentId = parentId.replace(CATEGORY_ID_SUFFIX, "");
        } else {
            // fallback to not crash
            parentId = "null";
        }

        if (!importContext.getRootCategoryParentIds().contains(parentId)) {
            superCategoryCode = importContext.getCategoryCodePrefix() + parentId;
        }

        return superCategoryCode;
    }

}
