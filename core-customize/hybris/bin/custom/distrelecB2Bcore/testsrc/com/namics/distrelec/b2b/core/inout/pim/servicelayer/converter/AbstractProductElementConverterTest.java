/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.media.DistImage360Service;
import com.namics.distrelec.b2b.core.service.media.DistMediaContainerService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractProductElementConverterTest {

    @Mock
    protected DistManufacturerService mockedDistManufacturerService;

    @Mock
    protected DistCategoryService mockedDistCategoryService;

    @Mock
    protected DistMediaContainerService mockedDistMediaContainerService;

    @Mock
    protected ConfigurationService mockedConfigurationService;

    @Mock
    protected MediaService mockedMediaService;

    @Mock
    protected ProductCountryElementConverter productCountryElementConverter;

    @Mock
    protected DistImage360Service distImage360Service;

    @Mock
    protected ClassificationSystemService mockedClassificationSystemService;

    @Mock
    protected ModelService mockedModelService;

    @Before
    public void init() {
        mockConfigurationService();
    }

    protected DistManufacturerModel mockDistManufacturerService(final String manufacturerCode) {
        final DistManufacturerModel manufacturer = Mockito.mock(DistManufacturerModel.class);
        when(manufacturer.getCode()).thenReturn(manufacturerCode);

        when(mockedDistManufacturerService.getManufacturerByCode(manufacturerCode)).thenReturn(manufacturer);
        return manufacturer;
    }

    protected CategoryModel mockDistCategoryService(final String categoryCode) {
        final DistPimCategoryTypeModel categoryType = new DistPimCategoryTypeModel();
        categoryType.setCode("Familie");
        categoryType.setVisible(Boolean.TRUE);

        final CategoryModel category = new CategoryModel();
        category.setCode(categoryCode);
        category.setPimCategoryType(categoryType);

        when(mockedDistCategoryService.getCategoryForCode(category.getCode())).thenReturn(category);

        return category;
    }

    protected MediaContainerModel mockMediaContainerService(final ImportContext importContext, final String mediaContainerQualifier) {
        final MediaContainerModel mediaContainer = new MediaContainerModel();
        mediaContainer.setQualifier(mediaContainerQualifier);

        when(mockedDistMediaContainerService.getMediaContainerForQualifier(importContext.getProductCatalogVersion(), mediaContainer.getQualifier()))
                .thenReturn(mediaContainer);
        return mediaContainer;
    }

    protected void mockConfigurationService() {
        final Configuration configuration = mock(BaseConfiguration.class);
        when(mockedConfigurationService.getConfiguration()).thenReturn(configuration);
    }

}
