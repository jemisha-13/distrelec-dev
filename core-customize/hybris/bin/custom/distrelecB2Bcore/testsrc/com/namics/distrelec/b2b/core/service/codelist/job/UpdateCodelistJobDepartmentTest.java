/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;

import de.hybris.bootstrap.annotations.IntegrationTest;

/**
 * Tests for the update of the department codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class UpdateCodelistJobDepartmentTest extends AbstractUpdateCodelistJobTest {

    @Test
    public void testUpdateDepartment() throws P1FaultMessage {
        testUpdateCodelistEntry();
    }

    @Test
    public void testUpdateDepartmentWhenNoTimestampInShop() throws P1FaultMessage {
        testUpdateWhenNoTimestampInShop();
    }

    @Test
    public void testNotUpdateDepartmentWhenNewerInShop() throws P1FaultMessage {
        testNotUpdateWhenNewerInShop();
    }

    @Override
    protected String getCode() {
        return "0042";
    }

    @Override
    protected String getFirstNameEn() {
        return "Cleaning Department";
    }

    @Override
    protected String getFirstNameDe() {
        return "Putzabteilung";
    }

    @Override
    protected String getSecondNameEn() {
        return "IT Crowd";
    }

    @Override
    protected String getSecondNameDe() {
        return "IT Haufen";
    }

    @Override
    protected DistCodelistErpModel createCodelistModel() {
        final DistDepartmentModel department = new DistDepartmentModel();
        department.setErpSystem(DistErpSystem.SAP);
        return department;
    }

    @Override
    protected void saveEntry(final DistCodelistErpModel initialEntry) {
        distrelecCodelistService.insertOrUpdateDistDepartment((DistDepartmentModel) initialEntry);
    }

    @Override
    protected void addEntryToResponse(final CodeList codelistEntry, final ReadModifiedCodelistResponse response) {
        response.getDepartment().add(codelistEntry);
    }

    @Override
    protected List<? extends DistCodelistErpModel> getCodelistFromShop() {
        return distrelecCodelistService.getAllDistDepartments();
    }

    @Override
    protected String getElementLabel() {
        return "department";
    }

}
