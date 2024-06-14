/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.List;

import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the function codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobFunctionTest extends AbstractUpdateCodelistJobTest {
    

    @Test
    public void testUpdateFunction() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateFunctionWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateFunctionWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "42";
    }

    @Override
    protected String getFirstNameEn() {
        return "Cleaner";
    }

    @Override
    protected String getFirstNameDe() {
        return "Raumpfleger";
    }

    @Override
    protected String getSecondNameEn() {
        return "IT Supporter";
    }

    @Override
    protected String getSecondNameDe() {
        return "IT Helfer";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        final DistFunctionModel function = new DistFunctionModel();
        function.setErpSystem(DistErpSystem.SAP);
        return function;
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistFunction((DistFunctionModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getFunction().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistFunctions();
    }

    @Override
    protected String getElementLabel() {
        return "function";
    }

}
