package com.namics.hybris.ffsearch.category;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.category.dao.DistCategoryDao;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.service.DistFactFinderChannelDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import static java.util.Locale.ENGLISH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUpdateCategoryTraversalDataServiceUnitTest {

    private static final String PREVIOUS_VALUE = "previous";

    private static final String CATEGORY_1_CODE = "cat-L1D_379523";

    private static final String CATEGORY_1_NAME = "Office, Computing & Network Products";

    private static final String CATEGORY_1_NAME_SEO = "office-computing-network-products";

    private static final String CATEGORY_2_CODE = "cat-L2D_542667";

    private static final String CATEGORY_2_NAME = "Cameras & Accessories";

    private static final String CATEGORY_2_NAME_SEO = "cameras-accessories";

    private static final String CATEGORY_3_CODE = "cat-L3D_542671";

    private static final String CATEGORY_3_NAME = "Navigation / GPS";

    private static final String CATEGORY_3_NAME_SEO = "navigation-gps";

    private static final String CATEGORY_4_CODE = "cat-171751";

    private static final String CATEGORY_4_NAME = "GPS Navigation";

    private static final String CATEGORY_4_NAME_SEO = "gps-navigation";

    private static final String EXPECTED_CATPATHEXTENSIONS_1 = "[{\"url\":\"/en/office-computing-network-products/c/cat-L1D_379523\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg\"}]";

    private static final String EXPECTED_CATPATHEXTENSIONS_2 = "[{\"url\":\"/en/office-computing-network-products/c/cat-L1D_379523\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/c/cat-L2D_542667\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/17/L2D_542667_iPhone.jpg\"}]";

    private static final String EXPECTED_CATPATHEXTENSIONS_3 = "[{\"url\":\"/en/office-computing-network-products/c/cat-L1D_379523\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/c/cat-L2D_542667\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/17/L2D_542667_iPhone.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/navigation-gps/c/cat-L3D_542671\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/25/L3D_542671_iPhone.jpg\"}]";

    private static final String EXPECTED_CATPATHEXTENSIONS_4 = "[{\"url\":\"/en/office-computing-network-products/c/cat-L1D_379523\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/c/cat-L2D_542667\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/17/L2D_542667_iPhone.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/navigation-gps/c/cat-L3D_542671\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/25/L3D_542671_iPhone.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/navigation-gps/gps-navigation/c/cat-171751\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/17/51/gps_navigation_171751.jpg\"}]";

    private static final String EXPECTED_CATPATHEXTENSIONS_4_NO_IMAGE_4 = "[{\"url\":\"/en/office-computing-network-products/c/cat-L1D_379523\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/c/cat-L2D_542667\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/17/L2D_542667_iPhone.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/navigation-gps/c/cat-L3D_542671\",\"imageUrl\":\"/Web/WebShopImages/landscape_small/31/25/L3D_542671_iPhone.jpg\"},{\"url\":\"/en/office-computing-network-products/cameras-accessories/navigation-gps/gps-navigation/c/cat-171751\"}]";

    private static final String CATEGORY_1_URL = "/en/office-computing-network-products/c/cat-L1D_379523";

    private static final String CATEGORY_1_IMAGE_URL = "/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg";

    private static final String CATEGORY_2_URL = "/en/office-computing-network-products/cameras-accessories/c/cat-L2D_542667";

    private static final String CATEGORY_2_IMAGE_URL = "/Web/WebShopImages/landscape_small/31/17/L2D_542667_iPhone.jpg";

    private static final String CATEGORY_3_URL = "/en/office-computing-network-products/cameras-accessories/navigation-gps/c/cat-L3D_542671";

    private static final String CATEGORY_3_IMAGE_URL = "/Web/WebShopImages/landscape_small/31/25/L3D_542671_iPhone.jpg";

    private static final String CATEGORY_4_URL = "/en/office-computing-network-products/cameras-accessories/navigation-gps/gps-navigation/c/cat-171751";

    private static final String CATEGORY_4_IMAGE_URL = "/Web/WebShopImages/landscape_small/17/51/gps_navigation_171751.jpg";

    @InjectMocks
    private DefaultUpdateCategoryTraversalDataService defaultUpdateCategoryTraversalDataService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private DistCategoryDao distCategoryDao;

    @Mock
    private DistFactFinderChannelDao distFactFinderChannelDao;

    @Mock
    private I18NService i18NService;

    @Mock
    private ModelService modelService;

    @Mock
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    private CategoryModel categoryLevel1;

    private CategoryModel categoryLevel2;

    private CategoryModel categoryLevel3;

    private CategoryModel categoryLevel4;

    @Mock
    private MediaContainerModel mediaContainer1;

    @Mock
    private MediaModel media1;

    @Mock
    private MediaContainerModel mediaContainer2;

    @Mock
    private MediaModel media2;

    @Mock
    private MediaContainerModel mediaContainer3;

    @Mock
    private MediaModel media3;

    @Mock
    private MediaContainerModel mediaContainer4;

    @Mock
    private MediaModel media4;

    @Mock
    private MediaFormatModel mediaFormat;

    @Mock
    private DistFactFinderExportChannelModel distFactFinderExportChannel;

    @Mock
    private LanguageModel language;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(distFactFinderChannelDao.getAllChannels()).thenReturn(List.of(distFactFinderExportChannel));
        when(distFactFinderExportChannel.getLanguage()).thenReturn(language);
        when(language.getIsocode()).thenReturn("en");
        when(commonI18NService.getLocaleForIsoCode("en")).thenReturn(ENGLISH);

        categoryLevel1 = new CategoryModel();
        categoryLevel1.setCode(CATEGORY_1_CODE);
        categoryLevel1.setName(CATEGORY_1_NAME, ENGLISH);
        categoryLevel1.setNameSeo(CATEGORY_1_NAME_SEO, ENGLISH);
        categoryLevel1.setLevel(1);
        categoryLevel1.setPrimaryImage(mediaContainer1);

        categoryLevel2 = new CategoryModel();
        categoryLevel2.setCode(CATEGORY_2_CODE);
        categoryLevel2.setName(CATEGORY_2_NAME, ENGLISH);
        categoryLevel2.setNameSeo(CATEGORY_2_NAME_SEO, ENGLISH);
        categoryLevel2.setLevel(2);
        categoryLevel2.setPrimaryImage(mediaContainer2);

        categoryLevel3 = new CategoryModel();
        categoryLevel3.setCode(CATEGORY_3_CODE);
        categoryLevel3.setName(CATEGORY_3_NAME, ENGLISH);
        categoryLevel3.setNameSeo(CATEGORY_3_NAME_SEO, ENGLISH);
        categoryLevel3.setLevel(3);
        categoryLevel3.setPrimaryImage(mediaContainer3);

        categoryLevel4 = new CategoryModel();
        categoryLevel4.setCode(CATEGORY_4_CODE);
        categoryLevel4.setName(CATEGORY_4_NAME, ENGLISH);
        categoryLevel4.setNameSeo(CATEGORY_4_NAME_SEO, ENGLISH);
        categoryLevel4.setLevel(4);
        categoryLevel4.setPrimaryImage(mediaContainer4);

        categoryLevel4.setSupercategories(List.of(categoryLevel1, categoryLevel2, categoryLevel3));
        categoryLevel3.setSupercategories(List.of(categoryLevel1, categoryLevel2));
        categoryLevel2.setSupercategories(List.of(categoryLevel1));
        categoryLevel1.setSupercategories(List.of());

        when(distCategoryDao.getCategoryByLevelRange(1, 2)).thenReturn(List.of(categoryLevel1));
        when(distCategoryDao.getCategoryByLevelRange(2, 3)).thenReturn(List.of(categoryLevel2));
        when(distCategoryDao.getCategoryByLevelRange(3, 4)).thenReturn(List.of(categoryLevel3));
        when(distCategoryDao.getCategoryByLevelRange(4, 5)).thenReturn(List.of(categoryLevel4));

        when(categoryModelUrlResolver.resolve(categoryLevel1)).thenReturn(CATEGORY_1_URL);
        when(categoryModelUrlResolver.resolve(categoryLevel2)).thenReturn(CATEGORY_2_URL);
        when(categoryModelUrlResolver.resolve(categoryLevel3)).thenReturn(CATEGORY_3_URL);
        when(categoryModelUrlResolver.resolve(categoryLevel4)).thenReturn(CATEGORY_4_URL);

        when(mediaFormat.getQualifier()).thenReturn(DistConstants.MediaFormat.LANDSCAPE_SMALL);
        when(media1.getMediaFormat()).thenReturn(mediaFormat);
        when(media1.getInternalURL()).thenReturn(CATEGORY_1_IMAGE_URL);
        when(mediaContainer1.getMedias()).thenReturn(List.of(media1));
        when(media2.getMediaFormat()).thenReturn(mediaFormat);
        when(media2.getInternalURL()).thenReturn(CATEGORY_2_IMAGE_URL);
        when(mediaContainer2.getMedias()).thenReturn(List.of(media2));
        when(media3.getMediaFormat()).thenReturn(mediaFormat);
        when(media3.getInternalURL()).thenReturn(CATEGORY_3_IMAGE_URL);
        when(mediaContainer3.getMedias()).thenReturn(List.of(media3));
        when(media4.getMediaFormat()).thenReturn(mediaFormat);
        when(media4.getInternalURL()).thenReturn(CATEGORY_4_IMAGE_URL);
        when(mediaContainer4.getMedias()).thenReturn(List.of(media4));
    }

    @Test
    public void testUpdateCategoryTraversalData() {
        defaultUpdateCategoryTraversalDataService.updateCategoryTraversalData();

        assertThat(categoryLevel1.getCat1code(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel1.getCat1name(ENGLISH), is(CATEGORY_1_NAME));
        assertThat(categoryLevel1.getCat1nameSeo(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel1.getCatPathSelectCode(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel1.getCatPathSelectName(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel1.getCatPathExtensions(ENGLISH), is(EXPECTED_CATPATHEXTENSIONS_1));

        assertThat(categoryLevel2.getCat1code(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel2.getCat1name(ENGLISH), is(CATEGORY_1_NAME));
        assertThat(categoryLevel2.getCat1nameSeo(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel2.getCat2code(), is(CATEGORY_2_CODE));
        assertThat(categoryLevel2.getCat2name(ENGLISH), is(CATEGORY_2_NAME));
        assertThat(categoryLevel2.getCat2nameSeo(ENGLISH), is(CATEGORY_2_NAME_SEO));
        assertThat(categoryLevel2.getCatPathSelectCode(), is(CATEGORY_1_CODE + "/"
                                                             + CATEGORY_2_CODE));
        assertThat(categoryLevel2.getCatPathSelectName(ENGLISH), is(CATEGORY_1_NAME_SEO + "/"
                                                                    + CATEGORY_2_NAME_SEO));
        assertThat(categoryLevel2.getCatPathExtensions(ENGLISH), is(EXPECTED_CATPATHEXTENSIONS_2));

        assertThat(categoryLevel3.getCat1code(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel3.getCat1name(ENGLISH), is(CATEGORY_1_NAME));
        assertThat(categoryLevel3.getCat1nameSeo(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel3.getCat2code(), is(CATEGORY_2_CODE));
        assertThat(categoryLevel3.getCat2name(ENGLISH), is(CATEGORY_2_NAME));
        assertThat(categoryLevel3.getCat2nameSeo(ENGLISH), is(CATEGORY_2_NAME_SEO));
        assertThat(categoryLevel3.getCat3code(), is(CATEGORY_3_CODE));
        assertThat(categoryLevel3.getCat3name(ENGLISH), is(CATEGORY_3_NAME));
        assertThat(categoryLevel3.getCat3nameSeo(ENGLISH), is(CATEGORY_3_NAME_SEO));
        assertThat(categoryLevel3.getCatPathSelectCode(), is(CATEGORY_1_CODE + "/"
                                                             + CATEGORY_2_CODE + "/"
                                                             + CATEGORY_3_CODE));
        assertThat(categoryLevel3.getCatPathSelectName(ENGLISH), is(CATEGORY_1_NAME_SEO + "/"
                                                                    + CATEGORY_2_NAME_SEO + "/"
                                                                    + CATEGORY_3_NAME_SEO));
        assertThat(categoryLevel3.getCatPathExtensions(ENGLISH), is(EXPECTED_CATPATHEXTENSIONS_3));

        assertThat(categoryLevel4.getCat1code(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel4.getCat1name(ENGLISH), is(CATEGORY_1_NAME));
        assertThat(categoryLevel4.getCat1nameSeo(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel4.getCat2code(), is(CATEGORY_2_CODE));
        assertThat(categoryLevel4.getCat2name(ENGLISH), is(CATEGORY_2_NAME));
        assertThat(categoryLevel4.getCat2nameSeo(ENGLISH), is(CATEGORY_2_NAME_SEO));
        assertThat(categoryLevel4.getCat3code(), is(CATEGORY_3_CODE));
        assertThat(categoryLevel4.getCat3name(ENGLISH), is(CATEGORY_3_NAME));
        assertThat(categoryLevel4.getCat3nameSeo(ENGLISH), is(CATEGORY_3_NAME_SEO));
        assertThat(categoryLevel4.getCat4code(), is(CATEGORY_4_CODE));
        assertThat(categoryLevel4.getCat4name(ENGLISH), is(CATEGORY_4_NAME));
        assertThat(categoryLevel4.getCat4nameSeo(ENGLISH), is(CATEGORY_4_NAME_SEO));
        assertThat(categoryLevel4.getCatPathSelectCode(), is(CATEGORY_1_CODE + "/"
                                                             + CATEGORY_2_CODE + "/"
                                                             + CATEGORY_3_CODE + "/"
                                                             + CATEGORY_4_CODE));
        assertThat(categoryLevel4.getCatPathSelectName(ENGLISH), is(CATEGORY_1_NAME_SEO + "/"
                                                                    + CATEGORY_2_NAME_SEO + "/"
                                                                    + CATEGORY_3_NAME_SEO + "/"
                                                                    + CATEGORY_4_NAME_SEO));
        assertThat(categoryLevel4.getCatPathExtensions(ENGLISH), is(EXPECTED_CATPATHEXTENSIONS_4));
    }

    @Test
    public void testUpdateCategoryTraversalDataPreviousValue() {
        categoryLevel3.setCat1code(PREVIOUS_VALUE);
        categoryLevel3.setCat1name(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat1nameSeo(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat2code(PREVIOUS_VALUE);
        categoryLevel3.setCat2name(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat2nameSeo(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat3code(PREVIOUS_VALUE);
        categoryLevel3.setCat3name(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat3nameSeo(PREVIOUS_VALUE, ENGLISH);
        //leftover values
        categoryLevel3.setCat4code(PREVIOUS_VALUE);
        categoryLevel3.setCat4name(PREVIOUS_VALUE, ENGLISH);
        categoryLevel3.setCat4nameSeo(PREVIOUS_VALUE, ENGLISH);

        defaultUpdateCategoryTraversalDataService.updateCategoryTraversalData();

        assertThat(categoryLevel3.getCat1code(), is(CATEGORY_1_CODE));
        assertThat(categoryLevel3.getCat1name(ENGLISH), is(CATEGORY_1_NAME));
        assertThat(categoryLevel3.getCat1nameSeo(ENGLISH), is(CATEGORY_1_NAME_SEO));
        assertThat(categoryLevel3.getCat2code(), is(CATEGORY_2_CODE));
        assertThat(categoryLevel3.getCat2name(ENGLISH), is(CATEGORY_2_NAME));
        assertThat(categoryLevel3.getCat2nameSeo(ENGLISH), is(CATEGORY_2_NAME_SEO));
        assertThat(categoryLevel3.getCat3code(), is(CATEGORY_3_CODE));
        assertThat(categoryLevel3.getCat3name(ENGLISH), is(CATEGORY_3_NAME));
        assertThat(categoryLevel3.getCat3nameSeo(ENGLISH), is(CATEGORY_3_NAME_SEO));
        assertThat(categoryLevel3.getCat4code(), nullValue());
        assertThat(categoryLevel3.getCat4name(ENGLISH), nullValue());
        assertThat(categoryLevel3.getCat4nameSeo(ENGLISH), nullValue());
    }

    @Test
    public void testUpdateCategoryTraversalDataPreviousNoMediaURL() {
        categoryLevel4.setPrimaryImage(null);

        defaultUpdateCategoryTraversalDataService.updateCategoryTraversalData();

        assertThat(categoryLevel4.getCatPathExtensions(ENGLISH), is(EXPECTED_CATPATHEXTENSIONS_4_NO_IMAGE_4));
    }

}
