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
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the shipping method codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobShippingMethodTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertShippingMethod() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    @Test
    public void testUpdateShippingMethod() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateShippingMethodWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateShippingMethodWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "Y1";
    }

    @Override
    protected String getFirstNameEn() {
        return "Carrier pigeon";
    }

    @Override
    protected String getFirstNameDe() {
        return "Brieftaube";
    }

    @Override
    protected String getSecondNameEn() {
        return "Snail mail";
    }

    @Override
    protected String getSecondNameDe() {
        return "Schneckenpost";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistShippingMethodModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistShippingtMethod((DistShippingMethodModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getShippingMethod().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecShippingMethod();
    }

    @Override
    protected String getElementLabel() {
        return "shipping method";
    }

}
