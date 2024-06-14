/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.impl;

import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@IntegrationTest
public class RemoveGhostRelationToRollbackedCustomerIntegrationTest extends ServicelayerTest {

    private static final String B2BUNIT_UID = "dummyUid";

    private static final String CUSTOMER_UID = "aaaa@bbb.ccc";

    private static final Logger LOG = LogManager.getLogger(RemoveGhostRelationToRollbackedCustomerIntegrationTest.class);

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Resource
    private ModelService modelService;

    @Resource
    private UserService userService;

    @Resource
    private DefaultDistCustomerFacade defaultDistCustomerFacade;


    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/testDistCustomerFacade.impex", "utf-8");
    }

    @Test
    public void queryIsValid_AllB2BUnits() {
        defaultDistCustomerFacade.getOrphanB2BUnit2Approvers(Optional.empty());
    }

    @Test
    public void queryIsValid_OneB2BUnit() {
        final B2BUnitModel b2bUnit = getOrCreateB2BUnit();
        defaultDistCustomerFacade.getOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
    }

    @Test
    public void normallyNoResults_AllB2BUnits() {
        assertEquals(0, defaultDistCustomerFacade.removeOrphanB2BUnit2Approvers(Optional.empty()));
    }

    @Test
    public void normallyNoResults_OneB2BUnit() {
        final B2BUnitModel b2bUnit = getOrCreateB2BUnit();
        assertEquals(0, defaultDistCustomerFacade.removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit)));
    }

    @Test
    public void checkIfProblemExists() throws InterruptedException {
        try {
            getUserService().getUserForUID(CUSTOMER_UID, B2BCustomerModel.class);
            fail();
        } catch (final UnknownIdentifierException e) {
            // OK, it should not be there
        }

        final B2BUnitModel b2bUnit = getOrCreateB2BUnit();

        final Transaction tx = Transaction.current();
        tx.begin();
        try {

            LOG.info("Approvers at the beginning: {} ", Arrays.toString(b2bUnit.getApprovers().stream().map(B2BCustomerModel::getUid).toArray()));

            final B2BCustomerModel newCustomer = getModelService().create(B2BCustomerModel.class);
            newCustomer.setUid(CUSTOMER_UID);
            newCustomer.setCustomerType(CustomerType.B2B);
            newCustomer.setEmail(CUSTOMER_UID);
            newCustomer.setName("Pippo");
            newCustomer.setDefaultB2BUnit(b2bUnit);
            getModelService().save(newCustomer);

            LOG.info("b2bUnit.getPk(): {}, newCustomer.getPk(): {}", b2bUnit.getPk(), newCustomer.getPk());

            getB2bCommerceUnitService().addApproverToUnit(b2bUnit, newCustomer);

            LOG.info("Approvers before rollback: {} ", Arrays.toString(b2bUnit.getApprovers().stream().map(B2BCustomerModel::getUid).toArray()));

        } finally {
            tx.rollback();
        }

        try {
            getUserService().getUserForUID(CUSTOMER_UID, B2BCustomerModel.class);
            fail();
        } catch (final UnknownIdentifierException e) {
            // OK, it should not be there
        }

        LOG.info("Approvers after rollback: {} ", Arrays.toString(b2bUnit.getApprovers().stream().map(B2BCustomerModel::getUid).toArray()));

        modelService.refresh(b2bUnit);

        LOG.info("Approvers after rollback and refresh: {} ", Arrays.toString(b2bUnit.getApprovers().stream().map(B2BCustomerModel::getUid).toArray()));

        assertTrue(defaultDistCustomerFacade.getOrphanB2BUnit2Approvers(Optional.of(b2bUnit)).size() == 0);

    }

    protected B2BUnitModel getOrCreateB2BUnit() {
        B2BUnitModel b2bUnit;
        try {
            b2bUnit = getUserService().getUserGroupForUID(B2BUNIT_UID, B2BUnitModel.class);
        } catch (final Exception e) {
            b2bUnit = getModelService().create(B2BUnitModel.class);
        }
        b2bUnit.setUid(B2BUNIT_UID);
        b2bUnit.setCustomerType(CustomerType.B2B);
        getModelService().save(b2bUnit);
        return b2bUnit;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistB2BCommerceUnitService getB2bCommerceUnitService() {
        return b2bCommerceUnitService;
    }

    public void setB2bCommerceUnitService(final DistB2BCommerceUnitService b2bCommerceUnitService) {
        this.b2bCommerceUnitService = b2bCommerceUnitService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }





}
