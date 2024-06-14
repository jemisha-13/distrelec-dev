/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the transport group codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobTransportGroupTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertTransportGroup() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    @Test
    public void testUpdateTransportGroup() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateTransportGroupWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateTransportGroupWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return String.valueOf(new Random().nextInt(1000));
    }

    @Override
    protected String getFirstNameEn() {
        return "Small goods";
    }

    @Override
    protected String getFirstNameDe() {
        return "Kleine Güter";
    }

    @Override
    protected String getSecondNameEn() {
        return "Big goods";
    }

    @Override
    protected String getSecondNameDe() {
        return "Grosse Güter";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistTransportGroupModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistrelecTransportGroup((DistTransportGroupModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getTransportGroup().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecTransportGroup();
    }

    @Override
    protected String getElementLabel() {
        return "transport group";
    }

}
