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
import com.namics.distrelec.b2b.core.model.DistRestrictionOfHazardousSubstancesModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the restriction of hazardous substances codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobRestrictionOfHazardousSubstancesTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testInsertRestrictionOfHazardousSubstances() throws P1FaultMessage {
        testInsertNewCodelistEntry();
    }

    @Test
    public void testUpdateRestrictionOfHazardousSubstances() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateRestrictionOfHazardousSubstancesWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateRestrictionOfHazardousSubstancesWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "16";
    }

    @Override
    protected String getFirstNameEn() {
        return "yes, level 3";
    }

    @Override
    protected String getFirstNameDe() {
        return "Ja, Level 3";
    }

    @Override
    protected String getSecondNameEn() {
        return "yes, level 4";
    }

    @Override
    protected String getSecondNameDe() {
        return "Ja, Level 4";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        return new DistRestrictionOfHazardousSubstancesModel();
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistrelecRestrictionOfHazardousSubstances((DistRestrictionOfHazardousSubstancesModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getRohs().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistrelecRestrictionOfHazardousSubstances();
    }

    @Override
    protected String getElementLabel() {
        return "restriction of hazardous substances";
    }

}
