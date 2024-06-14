/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.seo.DistCategoryPredecessorModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

/**
 * Abstract class with logic to convert XML content into a hybris category (product category or classification category).
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public abstract class AbstractCategoryElementConverter extends AbstractElementConverter implements PimImportCategoryElementConverter {

    private static final Logger LOG = LogManager.getLogger(AbstractCategoryElementConverter.class);

    public static final String CATEGORY_ID_SUFFIX = "_SSC";
    public static final String CATEGORY_ID_PREFIX = "cat-";

    private static final String ICON_PREFIX = "icon-";
    private static final String LIST_PREFIX = "list-";

	public static final String ELEMENT_CLASSIFICATION = "Classification";

    private static final List<String> PIM_CATEGORY_TYPES = new ArrayList<String>(
            Arrays.asList("ClassFolder", "L0D e-Shop", "L1D Section", "L2D Category", "L2-3D Cluster", "L3D Sub Category", "DL3_Productline", "Familie"));

    private static final Collection<String> PIM_CATEGORY_TYPES_LOWER_CASE = CollectionUtils.transformedCollection(PIM_CATEGORY_TYPES, new Transformer() {

        @Override
        public Object transform(final Object input) {
            return input != null ? input.toString().toLowerCase() : null;
        }
    });

    /**
     * Special characters mapping
     */
    private static final Map<String, String> SPECIAL_CHAR_MAP = new HashMap<String, String>() {
        {
            put("\u00BE", "3/4");
            put("\u00BD", "1/2");
            put("\u00BC", "1/4");
            put("¾", "3/4");
            put("½", "1/2");
            put("¼", "1/4");
            put("⅓", "1/3");
        }
    };

	protected static final String XP_NAME = "Name";
    protected static final String XP_MEDIA_CONTAINER = "AssetCrossReference[@Type='Primary Image']/@AssetID";
    // private static final String XP_PROMOTION_CATEGORY = "Values/Value[@AttributeID='promotioncat_txt']";
    private static final String XP_METADATA_CLASSIFICATION = "MetaData/Value[@AttributeID='web_template01']"; // Values/Value[@AttributeID='ff_filter_output01_txt']
    /**
     * Taxonomy - SEO
     */
    // Predecessor Time Stamp
    private static final String PREDECESSOR_TIMESTAMP_PREFIX = "timestamp_predecessor_dat_";
    // Predecessor Category ID
    private static final String PREDECESSOR_ID_PREFIX = "category_predecessor_id_";

    private static final int ROOT_CATEGORY_LEVEL = 0;

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private MediaContainerService mediaContainerService;

    @Autowired
    private ProductLineCountryElementConverter productLineCountryElementConverter;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;


    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;


    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#getId(org.dom4j.Element)
     */
    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ID).replace(CATEGORY_ID_SUFFIX, "");
    }

    /**
     * Replace all special characters with their mapped value.
     * 
     * @param value
     *            the String to update.
     * @return the updated String.
     */
    protected String replacingForFF(String value) {
        for (final String sign : SPECIAL_CHAR_MAP.keySet()) {
            if (value.contains(sign)) {
                value = value.replace(sign, SPECIAL_CHAR_MAP.get(sign));
            }
        }
        return value;
    }

    /**
     * Sets the category level.
     * 
     * @param target
     * @param superCategory
     * @param importContext
     */
    protected void setCategoryLevel(final CategoryModel target, final CategoryModel superCategory, final ImportContext importContext) {
        if (superCategory != null && superCategory.getLevel() != null) {
            target.setLevel(Integer.valueOf(superCategory.getLevel().intValue() + 1));
            importContext.getCategoryLevels().put(target.getCode(), target.getLevel());
        } else {
            target.setLevel(Integer.valueOf(ROOT_CATEGORY_LEVEL));
        }
    }

    /**
     * Look for the most appropriate PIM Category Type
     * 
     * @param source
     *            the PIM XML element
     * @return an instance of {@link DistPimCategoryTypeModel} if any, {@code null} otherwise.
     */
    protected DistPimCategoryTypeModel getPimCategoryType(final Element source) {
        String code = getUserTypeId(source);
        if (StringUtils.isEmpty(code)) {
            LOG.debug("No user type set on xml element {}", new Object[] { source.asXML() });
            return null;
        }
        if (!PIM_CATEGORY_TYPES_LOWER_CASE.contains(code.toLowerCase())) {
            if (checkIconAttribute(source)) {
                // USE ICON PAGE PREFIX
                code = ICON_PREFIX + code;
            } else if (checkListAttribute(source)) {
                // USE LIST PAGE PREFIX
                code = LIST_PREFIX + code;
            }
        }

        try {
            return codelistService.getDistPimCategoryType(code);
        } catch (final NotFoundException e) {
            LOG.debug("Could not get pim category type for code [{}]", new Object[] { code }, e);
            return null;
        }
    }

    /**
     * Check whether the PIM category type is List
     * 
     * @param source
     *            the PIM XML element to check
     * @return {@code true} if the PIM category type is List, {@code false} otherwise
     */
    private boolean checkListAttribute(final Element source) {
        final XPath xpath = source.createXPath(XP_METADATA_CLASSIFICATION);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final String iconValue = nodes.get(0).getTextTrim();
            if ("List".equals(iconValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the PIM category type is Icon
     * 
     * @param source
     *            the PIM XML element to check
     * @return {@code true} if the PIM category type is Icon, {@code false} otherwise
     */
    private boolean checkIconAttribute(final Element source) {
        final XPath xpath = source.createXPath(XP_METADATA_CLASSIFICATION);
        final List<Element> nodes = xpath.selectNodes(source);
        if (CollectionUtils.isNotEmpty(nodes)) {
            final String iconValue = nodes.get(0).getTextTrim();
            if ("Icon".equals(iconValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve the value of the element attribute named {@code UserTypeID}
     * 
     * @param element
     *            the target element
     * @return the value of the element attribute named {@code UserTypeID}
     */
    public String getUserTypeId(final Element element) {
        return element.attributeValue(ATTRIBUTE_USER_TYPE_ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportCategoryElementConverter#setPimCategoryType(de.hybris.
     * platform.category.model.CategoryModel, org.dom4j.Element)
     */
    @Override
    public void setPimCategoryType(final Element source, final CategoryModel target) {
        final DistPimCategoryTypeModel categoryType = getPimCategoryType(source);
        if (categoryType != null) {
            target.setPimCategoryType(categoryType);
        } else {
            LOG.error("Could not resolve pim category type [{}] for [{}]", new Object[] { getUserTypeId(source), getId(source) });
        }
    }

    /**
     * Look for the user group allowed to see the current category.
     * 
     * @return the user group allowed to see the current category.
     */
    protected List<PrincipalModel> getAllowedPrincipals() {
        final String userGroupUid = Config.getParameter(DistConstants.PropKey.Import.CATEGORY_ALLOWED_USER_GROUP);
        try {
            return Collections.<PrincipalModel> singletonList(userService.getUserGroupForUID(userGroupUid));
        } catch (final UnknownIdentifierException e) {
            LOG.warn("Could not find user group for UID [" + userGroupUid + "]", e);
            return Collections.emptyList();
        }
    }

    /**
     * Populate the predecessor category IDs and their time stamps of the target category.
     * 
     * @param source
     *            the source XML element coming from PIM
     * @param target
     *            the target successor category
     */
    protected void populatePredecessorInfo(final Element source, final CategoryModel target) {
        final List<Element> metaDataValues = source.selectNodes(PimConstants.XP_METADATA_VALUE);
        if (CollectionUtils.isEmpty(metaDataValues)) {
            return;
        }

        Attribute attribute = null;
        final Map<Integer, Couple<String, Date>> map = new HashMap<Integer, Couple<String, Date>>();
        final Set<DistCategoryPredecessorModel> predecessors = new LinkedHashSet<>();

        for (final Element metaDataValue : metaDataValues) {
            if ((attribute = metaDataValue.attribute(PimConstants.XP_ATTRIBUTE_ID)) == null //
                    || StringUtils.isBlank(attribute.getValue()) //
                    || !(attribute.getValue().startsWith(PREDECESSOR_ID_PREFIX) //
                            || attribute.getValue().startsWith(PREDECESSOR_TIMESTAMP_PREFIX))) {
                continue;
            }
            final String attributeValue = attribute.getValue().trim();
            try {
                final Integer index = Integer.valueOf(attributeValue.substring(attributeValue.lastIndexOf('_') + 1));
                final Couple<String, Date> couple = map.containsKey(index) ? map.get(index) : new Couple<String, Date>();
                if (attributeValue.startsWith(PREDECESSOR_ID_PREFIX)) {
                    couple.first = CATEGORY_ID_PREFIX + metaDataValue.getTextTrim();
                } else {
                    couple.second = getPredecessorTimestampDateFormat().parse(metaDataValue.getTextTrim());
                }

                map.put(index, couple);

                if (couple.isValid()) { // We process immediately the couple if it is valid.
                    if (!StringUtils.equals(couple.first, target.getCode())) {
                        predecessors.add(createCategoryPredecessor(target, couple.first, couple.second));
                    } else {
                        LOG.warn("WARNING! The category [{}] cannot be successor of itself.[PRED_ID: {}, TIMESTAMP: {}]",
                                new Object[] { target.getCode(), couple.first, couple.second });
                    }
                    // Last thing to do is to remove the mapping.
                    map.remove(index);
                }
            } catch (final Exception exp) {
                LOG.error("ERROR! an error occur during the processing of one of the predecessors for " + target.getCode(), exp);
            }
        }

        target.setPredecessors(predecessors);
    }

    /**
     * Creates a new instance of {@link DistCategoryPredecessorModel}
     * 
     * @param successor
     *            the successor category
     * @param predecessorId
     *            the predecessor category ID
     * @param predecessorTimestamp
     *            the time-stamp from which the redirection will take place
     * @return a new instance of {@link DistCategoryPredecessorModel}
     */
    private DistCategoryPredecessorModel createCategoryPredecessor(final CategoryModel successor, final String predecessorId, final Date predecessorTimestamp) {
        final DistCategoryPredecessorModel predecessor = getModelService().create(DistCategoryPredecessorModel.class);
        predecessor.setSuccessor(successor);
        predecessor.setPredecessorId(predecessorId);
        predecessor.setPredecessorTimestamp(predecessorTimestamp);
        return predecessor;
    }

    /**
     * Fetch the predecessor time-stamp date format from the application configuration. The default date format {@code yyyy-MM-dd HH:mm:ss}
     * if no configuration is found.
     * 
     * @return an instance of {@link DateFormat}.
     */
    private DateFormat getPredecessorTimestampDateFormat() {
        return new SimpleDateFormat(
                getConfigurationService().getConfiguration().getString("pim.taxonomy.category.predecessor.timestamp.format", "yyyy-MM-dd HH:mm:ss"));
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public MediaContainerService getMediaContainerService() {
        return mediaContainerService;
    }

    public void setMediaContainerService(final MediaContainerService mediaContainerService) {
        this.mediaContainerService = mediaContainerService;
    }

    public ProductLineCountryElementConverter getProductLineCountryElementConverter() {
        return productLineCountryElementConverter;
    }

    public void setProductLineCountryElementConverter(final ProductLineCountryElementConverter productLineCountryElementConverter) {
        this.productLineCountryElementConverter = productLineCountryElementConverter;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    protected ConverterLanguageUtil getConverterLanguageUtil() {
        return converterLanguageUtil;
    }

    private void setConverterLanguageUtil(ConverterLanguageUtil converterLanguageUtil) {
        this.converterLanguageUtil = converterLanguageUtil;
    }

    /**
     * {@code Couple}
     * 
     * @param <X>
     * @param <Y>
     *
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 5.18
     */
    private static class Couple<X, Y> {
        private X first;
        private Y second;

        public boolean isValid() {
            return first != null && second != null;
        }
    }
}
