package com.namics.distrelec.b2b.core.quotation.dao.impl;

import com.namics.distrelec.b2b.core.model.DistB2BQuoteLimitModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@IntegrationTest
public class DefaultDistQuotationDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private ModelService modelService;

    @Resource
    private DefaultDistQuotationDao quotationDao;

    private DistB2BQuoteLimitModel quote;
    private UserModel user;

    @Before
    public void setUp() {
        user = modelService.create(UserModel.class);
        user.setUid("test@quotations.ch");
        quote = modelService.create(DistB2BQuoteLimitModel.class);
        quote.setCurrentQuotationCount(0);
        quote.setUser(user);

        modelService.save(user);
        modelService.save(quote);
    }

    @After
    public void tearDown() {
        modelService.remove(user);
        modelService.remove(quote);
    }

    @Test
    public void testGetQuotationCount() {
        final DistB2BQuoteLimitModel quotationCount = quotationDao.getQuotationCount(user);
        assertEquals(user, quotationCount.getUser());
        assertEquals(0, quotationCount.getCurrentQuotationCount().intValue());
    }

    @Test
    public void testGetNoResultsFromDao() {
        modelService.remove(quote);
        final DistB2BQuoteLimitModel quotationCount = quotationDao.getQuotationCount(user);
        assertNull(quotationCount);
    }
}
