/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.model.category.DistOrderedMediaElementModel;
import com.namics.distrelec.b2b.core.model.category.DistOrderedTextElementModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class ProductFamilyElementConverter extends AbstractCategoryElementConverter {

    private static final String XP_VALUE_ATTR_TEMPLATE = "Value[@AttributeID='%s']";

    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyElementConverter.class);

    private static final String XP_SUPERCATEGORY_ELEMENT = String.format(XP_VALUE_ATTR_TEMPLATE,"DIS_FamilyTaxonomyID");

    private static final String XP_META_TEMPLATE = String.format("MetaData/%s", XP_VALUE_ATTR_TEMPLATE);

    private static final String XP_ORDER = String.format(XP_META_TEMPLATE, "DIS_FamilyMediaRanking");

    private static final String XP_MEDIA_TYPE = String.format(XP_META_TEMPLATE, "DIS_FamilyMediaType");

    private static final String XP_FAMILY_DESCRIPTION = String.format(XP_VALUE_ATTR_TEMPLATE, "5321");

    private static final String XP_FAMILY_DESCRIPTION_LOC = "ValueGroup[@AttributeID='5321']";

    private static final String XP_ASSET_X_REF = "AssetCrossReference";

    private static final String ASSET_ID_ATTR = "AssetID";

    private static final String TYPE_ATTR = "Type";

    private static final String XP_BULLET_PARTIAL = "Value[contains(@AttributeID,'DIS_FamilyBullet')]";

    private static final String XP_BULLET_PARTIAL_LOC = "ValueGroup[contains(@AttributeID,'DIS_FamilyBullet')]";

    private static final String ATTR_ID_ATTR = "AttributeID";

    protected static final String XP_VALUE = "Value";

    private static final String XP_APPLICATIONS = "MultiValue[@AttributeID='DIS_FamilyApplications']";

    private static final String XP_VALUE_GROUP = "ValueGroup";

    private static final String XP_META_DESC_GROUP = String.format("%s[@AttributeID='DIS_FamilyPageMetaDesc']",XP_VALUE_GROUP);

    private static final String XP_META_TITLE_GROUP = String.format("%s[@AttributeID='DIS_FamilyMetaTitle']",XP_VALUE_GROUP);

    private static final Pattern numberPattern = Pattern.compile("\\d+");

    @Autowired
    private DistCategoryService distCategoryService;

    @Override
    public void convert(Element source, CategoryModel target, ImportContext importContext, String hash) {
        convertAndPopulateName(source, target);
        setPimCategoryType(source, target);
        convertAndPopulateAssetReferences(source, target, importContext);
        convertAndPopulateValues(source, target, importContext);
        target.setPimXmlHashMaster(hash);
        target.setPimHashTimestamp(new Date());
    }

    @Override
    public void convertCategoryStructure(final Element source, final CategoryModel target, final ImportContext importContext) {
        Optional<String> superCategoryCode = getSupercategoryCode(source);

        if (!superCategoryCode.isPresent()) {
            LOG.error("Could not find supercategory code on family element");
            return;
        }

        Optional<CategoryModel> superCategory = distCategoryService.getCategoriesForCode(superCategoryCode.get())
                .stream()
                .findFirst();

        if (!superCategory.isPresent()) {
            LOG.error("Could not find supercategory for code {}", superCategoryCode.get());
            return;
        }

        target.setSupercategories(singletonList(superCategory.get()));
        setCategoryLevel(target, superCategory.get(), importContext);
    }

    private void convertAndPopulateValues(Element source, CategoryModel target, ImportContext importContext) {
        Element values = (Element) source.selectSingleNode("Values");
        convertCategoryStructure(values, target, importContext);
        convertAndPopulateBullets(values, target);
        convertAndPopulateIntroText(values, target);
        convertAndPopulateApplications(values, target);
        convertAndPopulateMetadata(values, target);
    }

    private void convertAndPopulateMetadata(Element values, CategoryModel target) {
        convertAndPopulateMetaTitle(values, target);
        convertAndPopulateMetaDescription(values, target);
    }

    private void convertAndPopulateMetaTitle(Element values, CategoryModel target) {
        getNodesForXPath(values, XP_META_TITLE_GROUP).filter(Element.class::isInstance).map(Element.class::cast).forEach( t -> {
            getConverterLanguageUtil().getLocalizedValues(t, XP_VALUE).forEach( (k, v) -> target.setSeoMetaTitle(v, k));
        });
    }

    private void convertAndPopulateMetaDescription(Element values, CategoryModel target) {
        getNodesForXPath(values, XP_META_DESC_GROUP).filter(Element.class::isInstance).map(Element.class::cast).forEach( t -> {
            getConverterLanguageUtil().getLocalizedValues(t, XP_VALUE).forEach( (k, v) -> target.setSeoMetaDescription(v, k));
        });
    }

    private void convertAndPopulateApplications(Element source, CategoryModel target) {
        Set<DistOrderedTextElementModel> oldApplications = target.getApplications();
        if (isNotEmpty(oldApplications)) {
            getModelService().removeAll(oldApplications);
        }
        target.setApplications(new LinkedHashSet<>());
        getNodesForXPath(source, XP_APPLICATIONS).findAny().ifPresent(mv -> {
            getNodesForXPath((Element) mv, XP_VALUE)
                    .filter(Element.class::isInstance)
                    .map(Element.class::cast)
                    .forEach(e -> convertApplicationValue(e, target));
            getNodesForXPath((Element) mv, XP_VALUE_GROUP)
                    .filter(Element.class::isInstance)
                    .map(Element.class::cast)
                    .forEach(e -> convertApplicationValueGroup(e, target));
        });
    }

    private void convertApplicationValue(Element element, CategoryModel target) {
        DistOrderedTextElementModel application = getModelService().create(DistOrderedTextElementModel.class);
        application.setName(getId(element));
        application.setText(element.getTextTrim(), getConverterLanguageUtil().getLocaleForElement(element));
        getModelService().save(application);
        target.getApplications().add(application);
    }

    private void convertApplicationValueGroup(Element element, CategoryModel target) {
        DistOrderedTextElementModel application = getModelService().create(DistOrderedTextElementModel.class);
        Map<Locale, String> localised = getConverterLanguageUtil().getLocalizedValues(element, XP_VALUE);
        application.setName(localised.get(Locale.ENGLISH));
        localised.forEach((k, v) -> application.setText(v, k));
        getModelService().save(application);
        target.getApplications().add(application);
    }

    private void convertAndPopulateIntroText(Element source, CategoryModel target) {
        Optional.ofNullable(getSingleDescription(source).orElse(getValueGroupDescription(source))).ifPresent(n -> {
            if (XP_VALUE.equals(n.getName())) {
                target.setIntroText(n.getText(), getConverterLanguageUtil().getLocaleForElement((Element) n));
            } else {
                getConverterLanguageUtil().getLocalizedValues((Element) n, XP_VALUE).forEach((k, v) -> target.setIntroText(v, k));
            }
        });
    }

    private Optional<Node> getSingleDescription(Element element) {
        return getNodesForXPath(element, XP_FAMILY_DESCRIPTION).findAny();
    }

    private Node getValueGroupDescription(Element element) {
        return getNodesForXPath(element, XP_FAMILY_DESCRIPTION_LOC).findAny().orElse(null);
    }

    private void convertAndPopulateBullets(Element source, CategoryModel target) {
        Set<DistOrderedTextElementModel> oldBullets = target.getBullets();
        if (isNotEmpty(oldBullets)) {
            getModelService().removeAll(oldBullets);
        }
        target.setBullets(new LinkedHashSet<>());
        //non-localized bullets
        getNodesForXPath(source, XP_BULLET_PARTIAL)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .forEach(e -> convertBulletValue(e, target));
        // localized
        getNodesForXPath(source, XP_BULLET_PARTIAL_LOC)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .forEach(e -> convertBulletValueGroup(e, target));
    }

    private void convertBulletValue(Element element, CategoryModel target) {
        DistOrderedTextElementModel bullet = getBullet(element);
        bullet.setText(element.getTextTrim(), getConverterLanguageUtil().getLocaleForElement(element));
        getModelService().save(bullet);
        addBulletToList(bullet, target);
    }

    private void convertBulletValueGroup(Element element, CategoryModel target) {
        DistOrderedTextElementModel bullet = getBullet(element);
        getConverterLanguageUtil().getLocalizedValues(element, XP_VALUE).forEach((k, v) -> bullet.setText(v, k));
        getModelService().save(bullet);
        addBulletToList(bullet, target);
    }

    private void addBulletToList(DistOrderedTextElementModel bullet, CategoryModel target) {
        Set<DistOrderedTextElementModel> bullets = new LinkedHashSet<>(target.getBullets());
        bullets.add(bullet);
        target.setBullets(bullets);
    }

    private DistOrderedTextElementModel getBullet(Element element) {
        String name = getAttributeFromNode(element, ATTR_ID_ATTR);
        int order = 0;
        Matcher matcher = numberPattern.matcher(name);
        if (matcher.find()) {
            order = Integer.parseInt(matcher.group());
        }
        DistOrderedTextElementModel bullet = getModelService().create(DistOrderedTextElementModel.class);
        bullet.setName(name);
        bullet.setOrder(order);
        return bullet;
    }

    private void convertAndPopulateAssetReferences(Element source, CategoryModel target, ImportContext importContext) {
        Map<String, List<Node>> mediaMap = getMediaMap(source);
        mediaMap.forEach((k, v) -> assignMediaToFamily(k, v, target, importContext));
    }

    private Map<String, List<Node>> getMediaMap(Element source) {
        return getNodesForXPath(source, XP_ASSET_X_REF).collect(Collectors.groupingBy(this::getTypeFromNode, Collectors.toList()));
    }

    private String getTypeFromNode(Node node) {
        return getAttributeFromNode(node, TYPE_ATTR);
    }

    private void assignMediaToFamily(String pimMediaType, List<Node> nodes, CategoryModel target, ImportContext importContext) {
        switch (pimMediaType) {
            case "DIS_FamilyPageMedia":
                Set<DistOrderedMediaElementModel> oldMedias = target.getFamilyMedia();
                if (isNotEmpty(oldMedias)) {
                    getModelService().removeAll(oldMedias);
                }
                target.setFamilyMedia(new LinkedHashSet<>());
                nodes.forEach(n -> assignFamilyPageMedia(n, target));
                break;

            case "DIS_FamilyPageDatasheet":
                nodes.forEach(n -> {
                    String qualifier = getAttributeFromNode(n, ASSET_ID_ATTR);
                    MediaContainerModel container = getMediaContainerService().getMediaContainerForQualifier(qualifier);
                    Set<Locale> locales = importContext.getLocalesForMedia(qualifier);
                    locales.forEach(l -> target.setFamilyDatasheet(container, l));
                    if (locales.isEmpty()) {
                        target.setFamilyDatasheet(container, Locale.ENGLISH);
                    }
                });
                break;
            case "DIS_FamilyPageMFTR": nodes.stream().findFirst().ifPresent(n -> {
                String qualifier = getAttributeFromNode(n, ASSET_ID_ATTR);
                target.setFamilyManufacturerImage(getMediaContainerService().getMediaContainerForQualifier(qualifier));
            });
               break;
            case "Family Image": nodes.stream().findFirst().ifPresent(n -> {
                String qualifier = getAttributeFromNode(n, ASSET_ID_ATTR);
                target.setFamilyImage(getMediaContainerService().getMediaContainerForQualifier(qualifier));
            });
        }
    }

    private void assignFamilyPageMedia(Node node, CategoryModel target) {
        DistOrderedMediaElementModel element = getModelService().create(DistOrderedMediaElementModel.class);
        element.setName(getAttributeFromNode(node, ASSET_ID_ATTR));
        element.setOrder(getOrderForMedia(node));
        element.setMediaType(getTypeForMedia(node));
        element.setMediaContainer(getMediaContainerService().getMediaContainerForQualifier(getAttributeFromNode(node, ASSET_ID_ATTR)));
        getModelService().save(element);
        target.getFamilyMedia().add(element);
    }

    private Integer getOrderForMedia(Node node) {
        return getNodesForXPath((Element) node, XP_ORDER).findFirst()
                                                         .map(Node::getText)
                                                         .map(Integer::valueOf)
                                                         .orElse(0);
    }

    private String getTypeForMedia(Node node) {
        return getNodesForXPath((Element) node, XP_MEDIA_TYPE).findFirst()
                                                              .map(Node::getText)
                                                              .orElse("UNKNOWN");
    }

    private void convertAndPopulateName(Element source, CategoryModel target) {
        getConverterLanguageUtil().getLocalizedValues(source, XP_NAME)
                .forEach((k, v) -> target.setName(v, k));
    }

    private Optional<String> getSupercategoryCode(Element source) {
        return getNodesForXPath(source, XP_SUPERCATEGORY_ELEMENT).findFirst().map(Node::getText).map(c -> String.format("cat-%s",c));
    }

    public static Stream<Node> getNodesForXPath(Element source, String XPath) {
        return Optional.ofNullable(source.selectNodes(XPath))
                       .map(List::stream)
                       .orElse(Stream.empty())
                       .filter(Node.class::isInstance)
                       .map(Node.class::cast);
    }

    private String getAttributeFromNode(Node node, String attribute) {
        return ((Element) node).attribute(attribute).getText();
    }
}
