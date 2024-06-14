package com.namics.distrelec.b2b.facades.rendering.suppliers.page.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.data.DistCmsDataFactory;
import com.namics.distrelec.b2b.core.service.data.DistRestrictionData;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.rendering.suppliers.page.RenderingPageModelSupplier;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Predicate;

public class RenderingManufacturerPageModelSupplier implements RenderingPageModelSupplier {

    private static final Logger LOG = LoggerFactory.getLogger(RenderingManufacturerPageModelSupplier.class);

    private Predicate<String> constrainedBy;
    private DistCmsDataFactory distCmsDataFactory;
    private DistCmsPageService distCmsPageService;
    private DistManufacturerService distManufacturerService;

    @Override
    public Predicate<String> getConstrainedBy() {
        return constrainedBy;
    }

    @Override
    public Optional<AbstractPageModel> getPageModel(String manufacturerCode) {
        DistManufacturerModel manufacturer = getManufacturer(manufacturerCode);
        if (manufacturer != null) {
            try {
                DistManufacturerPageModel manufacturerPage = distCmsPageService.getPageForManufacturer(manufacturer);
                return Optional.of(manufacturerPage);
            } catch (CMSItemNotFoundException e) {
                LOG.warn("Unable to find page for manufacturer: " + manufacturer, e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RestrictionData> getRestrictionData(String manufacturerCode) {
        DistManufacturerModel manufacturerModel = getManufacturer(manufacturerCode);
        if (manufacturerModel != null) {
            DistRestrictionData restrictionData = distCmsDataFactory.createRestrictionData(manufacturerModel);
            return Optional.of(restrictionData);
        }
        return Optional.empty();
    }

    protected DistManufacturerModel getManufacturer(String manufacturerCode) {
        try {
            DistManufacturerModel manufacturer = distManufacturerService.getManufacturerByCode(manufacturerCode);
            return manufacturer;
        } catch (UnknownIdentifierException e) {
            LOG.warn("Manufacturer code is not defined or manufacturer is not found: " + manufacturerCode, e);
            return null;
        }
    }

    public void setConstrainedBy(Predicate<String> constrainedBy) {
        this.constrainedBy = constrainedBy;
    }

    public void setDistCmsPageService(DistCmsPageService distCmsPageService) {
        this.distCmsPageService = distCmsPageService;
    }

    public void setDistCmsDataFactory(DistCmsDataFactory distCmsDataFactory) {
        this.distCmsDataFactory = distCmsDataFactory;
    }

    public void setDistManufacturerService(DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }
}
