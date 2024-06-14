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

import org.junit.Ignore;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.LocalizedString;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistRequest;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;

/**
 * Common base class for the tests for the update of codelists via cron job {@link UpdateCodelistsJob}.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@Ignore
public abstract class AbstractUpdateCodelistJobTest extends AbstractCronJobTest {

    protected final void testInsertNewCodelistEntry() throws P1FaultMessage {
        // init
        // start without any elements in the shop in this case
        final String code = getCode();
        initMockWebServiceClientResponse(code, EARLIER_SAP_DATE, getFirstNameEn(), getFirstNameDe());

        // action
        final int waitSeconds = 4;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(code, EARLIER_DATE, getFirstNameEn(), getFirstNameDe());
    }

    protected final void testUpdateCodelistEntry() throws P1FaultMessage {
        // init
        final String code = getCode();
        initCodelistEntryInShop(code, EARLIER_DATE, getFirstNameEn(), getFirstNameDe());
        initMockWebServiceClientResponse(code, LATER_SAP_DATE, getSecondNameEn(), getSecondNameDe());

        // action
        final int waitSeconds = 4;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(code, LATER_DATE, getSecondNameEn(), getSecondNameDe());
    }

    protected final void testUpdateWhenNoTimestampInShop() throws P1FaultMessage {
        // init
        final String code = getCode();
        initCodelistEntryInShop(code, null, getFirstNameEn(), getFirstNameDe());
        initMockWebServiceClientResponse(code, LATER_SAP_DATE, getSecondNameEn(), getSecondNameDe());

        // action
        final int waitSeconds = 4;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(code, LATER_DATE, getSecondNameEn(), getSecondNameDe());
    }

    protected final void testNotUpdateWhenNewerInShop() throws P1FaultMessage {
        // init
        final String code = getCode();
        initCodelistEntryInShop(code, LATER_DATE, getFirstNameEn(), getFirstNameDe());
        initMockWebServiceClientResponse(code, EARLIER_SAP_DATE, getSecondNameEn(), getSecondNameDe());

        // action
        final int waitSeconds = 4;
        executeCronJobAndWait(waitSeconds);

        // evaluation
        assertCronJobSuccess(waitSeconds);
        assertOnlyEntryInShop(code, LATER_DATE, getFirstNameEn(), getFirstNameDe());
    }

    protected abstract String getCode();

    protected abstract String getFirstNameEn();

    protected abstract String getFirstNameDe();

    protected abstract String getSecondNameEn();

    protected abstract String getSecondNameDe();

    private void initCodelistEntryInShop(final String code, final Date lastModifiedErp, final String nameEn, final String nameDe) {
        final DistCodelistErpModel initialEntry = createCodelistModel();
        initialEntry.setCode(code);
        initialEntry.setLastModifiedErp(lastModifiedErp);
        initialEntry.setNameErp(nameEn, new Locale("en"));
        initialEntry.setNameErp(nameDe, new Locale("de"));
        saveEntry(initialEntry);
    }

    protected abstract DistCodelistErpModel createCodelistModel();

    protected abstract void saveEntry(DistCodelistErpModel initialEntry);

    private void initMockWebServiceClientResponse(final String code, final long lastModifiedErp, final String description_en, final String description_de)
            throws P1FaultMessage {
        final CodeList codelistEntry = new CodeList();
        codelistEntry.setCode(code);
        codelistEntry.setLastModifiedErp(BigInteger.valueOf(lastModifiedErp));
        final List<LocalizedString> descriptions = codelistEntry.getDescription();
        final LocalizedString locStrDe = new LocalizedString();
        locStrDe.setLang("de");
        locStrDe.setValue(description_de);
        descriptions.add(locStrDe);
        final LocalizedString locStrEn = new LocalizedString();
        locStrEn.setLang("en");
        locStrEn.setValue(description_en);
        descriptions.add(locStrEn);
        final ReadModifiedCodelistResponse response = new ReadModifiedCodelistResponse();
        addEntryToResponse(codelistEntry, response);
        when(webServiceClient.if50ReadModifiedCodelist(any(ReadModifiedCodelistRequest.class))).thenReturn(response);
    }

    protected abstract void addEntryToResponse(CodeList codelistEntry, ReadModifiedCodelistResponse response);

    private void assertOnlyEntryInShop(final String code, final Date lastModifiedErp, final String name_en, final String name_de) {
        final List<? extends DistCodelistErpModel> allEntries = getCodelistFromShop();
        assertEquals("wrong number of " + getElementLabel() + "s", 1, allEntries.size());
        final DistCodelistErpModel firstEntry = allEntries.get(0);
        // refresh the model to get the values after the execution of the cron job
        modelService.refresh(firstEntry);
        assertEquals("code of " + getElementLabel() + " is wrong", code, firstEntry.getCode());
        assertEquals("lastModifiedErp of " + getElementLabel() + " is wrong", lastModifiedErp, firstEntry.getLastModifiedErp());
        assertEquals("en nameERP of " + getElementLabel() + " is wrong", name_en, firstEntry.getNameErp(new Locale("en")));
        assertEquals("de nameERP of " + getElementLabel() + " is wrong", name_de, firstEntry.getNameErp(new Locale("de")));
    }

    protected abstract List<? extends DistCodelistErpModel> getCodelistFromShop();

    protected abstract String getElementLabel();

}
