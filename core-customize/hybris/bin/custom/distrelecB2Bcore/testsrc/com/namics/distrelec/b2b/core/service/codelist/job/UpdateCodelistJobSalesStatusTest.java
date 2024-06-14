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
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the sales status codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobSalesStatusTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertSalesStatus() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    @Test
    public void testUpdateSalesStatus() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateSalesStatusWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateSalesStatusWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "70";
    }

    @Override
    protected String getFirstNameEn() {
        return "Middle of Life";
    }

    @Override
    protected String getFirstNameDe() {
        return "Lebensmitte";
    }

    @Override
    protected String getSecondNameEn() {
        return "End of Life";
    }

    @Override
    protected String getSecondNameDe() {
        return "Lebensende";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistSalesStatusModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistrelecSalesStatus((DistSalesStatusModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getSalesStatus().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecSalesStatus();
    }

    @Override
    protected String getElementLabel() {
        return "sales status";
    }

}
