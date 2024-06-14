/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the replacement reason codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobReplacementReasonTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertReplacementReason() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    //TODO
    @Ignore
    public void testUpdateReplacementReason() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }
    
    //TODO
    @Ignore
    public void testUpdateReplacementReasonWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }
    
    //TODO
    @Ignore
    public void testNotUpdateReplacementReasonWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "L";
    }

    @Override
    protected String getFirstNameEn() {
        return "too large";
    }

    @Override
    protected String getFirstNameDe() {
        return "zu gross";
    }

    @Override
    protected String getSecondNameEn() {
        return "too small";
    }

    @Override
    protected String getSecondNameDe() {
        return "zu klein";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistReplacementReasonModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistrelecReplacementReason((DistReplacementReasonModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getReplacementReason().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecReplacementReason();
    }

    @Override
    protected String getElementLabel() {
        return "replacement reason";
    }

}
