/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimXmlHashDto;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.ElementPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementIdNotFoundException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.MasterImportModelNotFoundException;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Abstract XML element handler to check a persisted hash value if the current XML element has changed.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public abstract class AbstractHashAwarePimImportElementHandler extends AbstractPimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractHashAwarePimImportElementHandler.class);
    private static final String CRLF = "\r\n"; // line separator used in PIM XML files (Windows)

    @Autowired
    private ModelService modelService;

    @Autowired
    private ConfigurationService configurationService;

    private PimImportElementConverter pimImportElementConverter;
    private final String typeCode;

    public AbstractHashAwarePimImportElementHandler(final String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public void onEnd(final ElementPath elementPath) {
        final Element element = elementPath.getCurrent();
        try {
            doUpdateIfRequired(element);
        } finally {
            getImportContext().incrementCounter(getTypeCode());
            element.detach();
        }
    }

    protected void doUpdateIfRequired(final Element element) {
        String id;
        try {
            id = pimImportElementConverter.getId(element);
        } catch (final ElementIdNotFoundException e) {
            LOG.error("No ID found on XML element: {}", e.getMessage());
            return;
        }

        final PimXmlHashDto pimXmlHashDto = getImportContext().getHashValue(getTypeCode(), id);
        final String oldHash = pimXmlHashDto != null ? pimXmlHashDto.getPimXmlHashMaster() : null;
        final Date pimHashTimestamp = pimXmlHashDto != null ? pimXmlHashDto.getPimHashTimestamp() : null;
        final Date globalHashTimestamp = getImportContext().getGlobalHashTimestamp();
        final String newHash = calculateHashV2(element.asXML());
        final ItemModel model = getItemModel(id, element);
        if (updateRequired(element, id, oldHash, newHash, model, pimHashTimestamp, globalHashTimestamp)) {
            doUpdate(element, id, newHash, model);
        } else {
            getImportContext().incrementSkippedCounter(getTypeCode());
        }

        doAfterProcess(id);
    }

    protected boolean updateRequired(final Element element, String id, final String oldHash, final String newHash, final ItemModel item,
                                     final Date pimHashTimestamp, final Date globalHashTimestamp) {
        if (item == null) {
            return false;
        }

        if (!isWhitelisted()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skip update since category is not whitelisted.");
            }
            return false;
        }

        if (oldHash == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No hash value found for typeCode [{}] and ID [{}]. Import new item.", getTypeCode(), id);
            }
            return true;
        }

        if (pimHashTimestamp != null && globalHashTimestamp != null && pimHashTimestamp.before(globalHashTimestamp)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Deprecated hash value found for typeCode [{}] and ID [{}]. Import new item.", getTypeCode(), id);
            }
            return true;
        }

        if (oldHash.equals(newHash)) {
            final int MAX_AGE_MINUTES = getConfigurationService().getConfiguration().getInt("product.features.max.age.minutes", 10080);
            if (item.getModifiedtime() != null && item.getModifiedtime().before(DateUtils.addMinutes(new Date(), -MAX_AGE_MINUTES))) {
                return true;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skip update for typeCode [{}] and ID [{}] due to equal hash values.", getTypeCode(), id);
            }
            return false;
        } else {
            // Test with legacy hash generation (without sorting the XML lines). This is to avoid time consuming full import due to
            // changed hash value generation. This additional test can be removed after some time (e.g. mid 2015).
            if (oldHash.equals(calculateHashV1(element.asXML()))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Skip update for typeCode [{}] and ID [{}] due to equal hash values (legacy hash value generation).", getTypeCode(), id);
                }
                return false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Hash values not equal. Update required for typeCode [{}] and ID [{}].", getTypeCode(), id);
            }
        }
        return true;
    }

    protected void doUpdate(final Element element, final String id, final String newHash, final ItemModel model) {
        try {
            pimImportElementConverter.convert(element, model, getImportContext(), newHash);
            modelService.save(model);
        } catch (final ElementConverterException e) {
            LOG.error("Could not convert element [" + element.getName() + "] with id [" + id + "] to model of type [" + getTypeCode() + "]: " + e.getMessage(), e);
        } catch (final ModelSavingException e) {
            LOG.error("Could not save model with id [" + id + "] and type [" + getTypeCode() + "]", e);
        } finally {
            modelService.detach(model);
        }
    }

    private ItemModel getItemModel(String id, Element element) {
        try {
            return getModel(id, element);
        } catch (final MasterImportModelNotFoundException e) {
            LOG.error("Model for typeCode [{}] and ID [{}] not found", new Object[] { getTypeCode(), id }, e);
            return null;
        }
    }

    protected boolean isWhitelisted() {
        return true;
    }

    protected String calculateHashV1(String s) {
        return DigestUtils.md5Hex(s);
    }

    /**
     * Calculate a hash value for an XML fragment. Do not honor the order of the lines in the XML file. XML Nodes can have a different sort
     * order from export to export. (Jira DISTRELEC-3994)
     * 
     * @param xml
     *            XML fragment
     * @return md5 hash value
     */
    protected String calculateHashV2(String xml) {
        List<String> lines = Arrays.asList(StringUtils.split(xml, CRLF));
        Collections.sort(lines);
        return DigestUtils.md5Hex(StringUtils.join(lines, CRLF));
    }

    protected abstract ItemModel getModel(final String id, final Element element);

    protected void doAfterProcess(@SuppressWarnings("unused")
    final String id) {
        // Nothing to do.
    }

    public ModelService getModelService() {
        return modelService;
    }

    public String getTypeCode() {
        return typeCode;
    }

    @Required
    public void setPimImportElementConverter(final PimImportElementConverter pimImportElementConverter) {
        this.pimImportElementConverter = pimImportElementConverter;
    }

    public PimImportElementConverter getPimImportElementConverter() {
        return pimImportElementConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
