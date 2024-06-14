/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.distrelec.webservice.sap.v1.CodeListUnit;
import com.distrelec.webservice.sap.v1.LocalizedString;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistRequest;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Tests for the update of the sales unit codelist via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class UpdateCodelistUnitJobSalesUnitTest extends AbstractCronJobTest {

    private static final String CODE = "423";

    private static final long FIRST_SAP_AMOUNT = 65l;

    private static final long SECOND_SAP_AMOUNT = 37l;

    private static final Double FIRST_AMOUNT = new Double(65);

    private static final Double SECOND_AMOUNT = new Double(37);

    private static final String FIRST_UNIT = "pieces";

    private static final String SECOND_UNIT = "kg";

    private static final String UNKNOWN_UNIT = "stuecke";

    private static final String FIRST_NAME_EN = "Package with 65 pieces";

    private static final String FIRST_NAME_DE = "Packung mit 65 Stück";

    private static final String SECOND_NAME_EN = "Package with 37 kilograms";

    private static final String SECOND_NAME_DE = "Packung mit 37 Kilogramm";

    @Test
    public void testInsertSalesUnit() throws P1FaultMessage {
        // init
        // start without any elements in the shop in this case
        initMockWebServiceClientResponse(CODE, EARLIER_SAP_DATE, FIRST_SAP_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(CODE, EARLIER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
    }

    @Test
    public void testInsertSalesUnitWithUnknownUnitFails() throws P1FaultMessage {
        // init
        // start without any elements in the shop in this case
        initMockWebServiceClientResponse(CODE, EARLIER_SAP_DATE, FIRST_SAP_AMOUNT, UNKNOWN_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        final List<DistSalesUnitModel> allSalesUnits = distrelecCodelistService.getAllDistrelecSalesUnit();
        assertEquals("wrong number of sales units", 0, allSalesUnits.size());
    }

    @Test
    public void testUpdateSalesUnit() throws P1FaultMessage {
        // init
        initCodelistEntryInShop(CODE, EARLIER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
        initMockWebServiceClientResponse(CODE, LATER_SAP_DATE, SECOND_SAP_AMOUNT, SECOND_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(CODE, LATER_DATE, SECOND_AMOUNT, SECOND_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);
    }

    @Test
    public void testUpdateSalesUnitWithUnknownUnitFails() throws P1FaultMessage {
        // init
        initCodelistEntryInShop(CODE, EARLIER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
        initMockWebServiceClientResponse(CODE, LATER_SAP_DATE, SECOND_SAP_AMOUNT, UNKNOWN_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(CODE, EARLIER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
    }

    @Test
    public void testUpdateSalesUnitWhenNoTimestampInShop() throws P1FaultMessage {
        // init
        initCodelistEntryInShop(CODE, null, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
        initMockWebServiceClientResponse(CODE, LATER_SAP_DATE, SECOND_SAP_AMOUNT, SECOND_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(CODE, LATER_DATE, SECOND_AMOUNT, SECOND_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);
    }

    @Test
    public void testNotUpdateSalesUnitWhenNewerInShop() throws P1FaultMessage {
        // init
        initCodelistEntryInShop(CODE, LATER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
        initMockWebServiceClientResponse(CODE, EARLIER_SAP_DATE, SECOND_SAP_AMOUNT, SECOND_UNIT, SECOND_NAME_EN, SECOND_NAME_DE);

        // action
        final int waitSeconds = 2;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(CODE, LATER_DATE, FIRST_AMOUNT, FIRST_UNIT, FIRST_NAME_EN, FIRST_NAME_DE);
    }

    private void initCodelistEntryInShop(final String code, final Date lastModifiedErp, final Double amount, final String unit, final String nameEn,
            final String nameDe) {
        final DistSalesUnitModel initialEntry = new DistSalesUnitModel();
        initialEntry.setCode(code);
        initialEntry.setAmount(amount);
        initialEntry.setUnit(findUnitWithCode(unit));
        initialEntry.setLastModifiedErp(lastModifiedErp);
        initialEntry.setNameErp(nameEn, new Locale("en"));
        initialEntry.setNameErp(nameDe, new Locale("de"));
        distrelecCodelistService.insertOrUpdateDistrelecSalesUnit(initialEntry);
    }

    private UnitModel findUnitWithCode(final String unitCode) {
        final String query = "SELECT {" + UnitModel.PK + "} FROM {" + UnitModel._TYPECODE + "} WHERE {" + UnitModel.CODE + "}=?unitCode";
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);
        flexibleSearchQuery.addQueryParameter("unitCode", unitCode);
        return flexibleSearchService.searchUnique(flexibleSearchQuery);
    }

    private void initMockWebServiceClientResponse(final String code, final long lastModifiedErp, final long amount, final String unit,
            final String description_en, final String description_de) throws P1FaultMessage {
        final CodeListUnit codelistUnitEntry = new CodeListUnit();
        codelistUnitEntry.setCode(code);
        codelistUnitEntry.setLastModifiedErp(BigInteger.valueOf(lastModifiedErp));
        codelistUnitEntry.setAmount(BigInteger.valueOf(amount));
        codelistUnitEntry.setUnit(unit);
        final List<LocalizedString> descriptions = codelistUnitEntry.getDescription();
        final LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue(description_de);
        descriptions.add(locStrDe);
        final LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue(description_en);
        descriptions.add(locStrEn);
        final ReadModifiedCodelistResponse response = new ReadModifiedCodelistResponse();
        response.getSalesUnit().add(codelistUnitEntry);
        when(webServiceClient.if50ReadModifiedCodelist(any(ReadModifiedCodelistRequest.class))).thenReturn(response);
    }

    private void assertOnlyEntryInShop(final String code, final Date lastModifiedErp, final Double amount, final String unit, final String name_en,
            final String name_de) {
        final List<DistSalesUnitModel> allSalesUnits = distrelecCodelistService.getAllDistrelecSalesUnit();
        assertEquals("wrong number of sales units", 1, allSalesUnits.size());
        final DistSalesUnitModel firstSalesUnit = allSalesUnits.get(0);
        // refresh the model to get the values after the execution of the cron job
        modelService.refresh(firstSalesUnit);
        assertEquals("code of sales unit is wrong", code, firstSalesUnit.getCode());
        assertEquals("lastModifiedErp of sales unit is wrong", lastModifiedErp, firstSalesUnit.getLastModifiedErp());
        assertEquals("amount of sales unit is wrong", amount, firstSalesUnit.getAmount());
        assertEquals("unit of sales unit is wrong", unit, firstSalesUnit.getUnit().getCode());
        assertEquals("en nameERP of sales unit is wrong", name_en, firstSalesUnit.getNameErp(new Locale("en")));
        assertEquals("de nameERP of sales unit is wrong", name_de, firstSalesUnit.getNameErp(new Locale("de")));
    }

}
