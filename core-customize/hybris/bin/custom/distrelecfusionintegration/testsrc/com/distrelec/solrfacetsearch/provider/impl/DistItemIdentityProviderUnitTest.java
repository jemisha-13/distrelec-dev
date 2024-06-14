package com.distrelec.solrfacetsearch.provider.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistItemIdentityProviderUnitTest {

    private static final String COUNTRY_ISOCODE = "SE";

    private static final String PRODUCT_CODE = "30218839";

    private static final String MANUFACTURER_CODE = "man_vrt";

    private static final String CATEGORY_CODE = "cat-DNAV_PL_3351664";

    private static final long PK_VALUE = 26491529586071L;

    @InjectMocks
    private DistItemIdentityProvider distItemIdentityProvider;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private CountryModel country;

    @Mock
    private ProductModel product;

    @Mock
    private CategoryModel category;

    @Mock
    private DistManufacturerModel manufacturer;

    @Mock
    private PriceRowModel priceRow;

    private PK pk;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(country.getIsocode()).thenReturn(COUNTRY_ISOCODE);
        when(cmsSite.getCountry()).thenReturn(country);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(product.getCode()).thenReturn(PRODUCT_CODE);
        when(manufacturer.getCode()).thenReturn(MANUFACTURER_CODE);
        when(category.getCode()).thenReturn(CATEGORY_CODE);
        pk = PK.fromLong(PK_VALUE);
        when(priceRow.getPk()).thenReturn(pk);
    }

    @Test
    public void testGetIdentifierProduct() {
        String result = distItemIdentityProvider.getIdentifier(indexConfig, product);

        assertThat(result, is(getExpectedIdentifier(PRODUCT_CODE)));
    }

    @Test
    public void testGetIdentifierManufacturer() {
        String result = distItemIdentityProvider.getIdentifier(indexConfig, manufacturer);

        assertThat(result, is(getExpectedIdentifier(MANUFACTURER_CODE)));
    }

    @Test
    public void testGetIdentifierCategory() {
        String result = distItemIdentityProvider.getIdentifier(indexConfig, category);

        assertThat(result, is(getExpectedIdentifier(CATEGORY_CODE)));
    }

    @Test
    public void testGetIdentifierOther() {
        String result = distItemIdentityProvider.getIdentifier(indexConfig, priceRow);

        assertThat(result, is(pk.getLongValueAsString()));
    }

    private String getExpectedIdentifier(String value) {
        return value + "-" + COUNTRY_ISOCODE;
    }
}
