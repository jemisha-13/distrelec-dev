/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.List;

import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the payment method codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobPaymentMethodTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertPaymentMethod() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    @Test
    public void testUpdatePaymentMethod() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdatePaymentMethodWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdatePaymentMethodWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "D001";
    }

    @Override
    protected String getFirstNameEn() {
        return "Coins";
    }

    @Override
    protected String getFirstNameDe() {
        return "Münzen";
    }

    @Override
    protected String getSecondNameEn() {
        return "Bank notes";
    }

    @Override
    protected String getSecondNameDe() {
        return "Banknoten";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistPaymentMethodModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistPaymentMethod((DistPaymentMethodModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getPaymentMethod().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecPaymentMethod();
    }

    @Override
    protected String getElementLabel() {
        return "payment method";
    }

}
