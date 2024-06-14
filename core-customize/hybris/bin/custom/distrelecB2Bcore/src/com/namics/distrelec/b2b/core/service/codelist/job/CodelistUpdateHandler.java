/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.LocalizedString;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistCodelistErpModel;

/**
 * Handler for updating codelists in the shop with codelists coming from the SAP system.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 * @param <T>
 *            The subclass of {@link DistCodelistErpModel} on which the handler should work.
 */
public class CodelistUpdateHandler<T extends DistCodelistErpModel> {

    private static final Logger LOG = Logger.getLogger(CodelistUpdateHandler.class);

    private final List<CodeList> codeListFromSap;

    private final List<T> codeListInShop;

    private final Class<T> clazz;

    private List<T> modelsToInsertOrUpdate;

    private Map<String, T> codeToCodeListEntryMap;

    public CodelistUpdateHandler(final List<CodeList> codeListFromSap, final List<T> codeListInShop, final Class<T> clazz) {
        this.codeListFromSap = codeListFromSap;
        this.codeListInShop = codeListInShop;
        this.clazz = clazz;
    }

    public List<T> prepareModelsForInsertOrUpdate() {
        modelsToInsertOrUpdate = new ArrayList<T>();
        buildCodeToCodeListEntryMap();

        for (CodeList codeListEntryFromSap : codeListFromSap) {
            final T codeListEntryInShop = codeToCodeListEntryMap.get(codeListEntryFromSap.getCode());
            if (null == codeListEntryInShop) {
                // not found in shop -> add
                LOG.info("inserting new " + clazz.getSimpleName() + " with code " + codeListEntryFromSap.getCode());
                try {
                    addInsertOrUpdateEntry(codeListEntryFromSap, clazz.newInstance());
                } catch (InstantiationException e) {
                    LOG.error("Creation of new " + clazz.getSimpleName() + " failed", e);
                } catch (IllegalAccessException e) {
                    LOG.error("Creation of new " + clazz.getSimpleName() + " failed", e);
                }
                continue;
            }
            final Date lastModifiedInShop = codeListEntryInShop.getLastModifiedErp();
            if (null == lastModifiedInShop) {
                // never updated from SAP -> update
                LOG.info("updating existing " + clazz.getSimpleName() + " with code " + codeListEntryFromSap.getCode());
                addInsertOrUpdateEntry(codeListEntryFromSap, codeListEntryInShop);
                continue;
            }
            final Date lastModifiedInSap = SoapConversionHelper.convertTimestampFromSap(codeListEntryFromSap.getLastModifiedErp());
            if (lastModifiedInSap.after(lastModifiedInShop)) {
                // newer in SAP -> update
                LOG.info("updating existing " + clazz.getSimpleName() + " with code " + codeListEntryFromSap.getCode());
                addInsertOrUpdateEntry(codeListEntryFromSap, codeListEntryInShop);
            }
        }

        return modelsToInsertOrUpdate;
    }

    private void buildCodeToCodeListEntryMap() {
        codeToCodeListEntryMap = new HashMap<String, T>();
        for (T codeListEntryInShop : codeListInShop) {
            if (codeToCodeListEntryMap.containsKey(codeListEntryInShop.getCode())) {
                throw new IllegalStateException("found two " + codeListEntryInShop.getClass().getSimpleName() + "s with code " + codeListEntryInShop.getCode());
            }
            codeToCodeListEntryMap.put(codeListEntryInShop.getCode(), codeListEntryInShop);
        }
    }

    private void addInsertOrUpdateEntry(final CodeList codeListEntryFromSap, final T codeListEntryInShop) {
        codeListEntryInShop.setCode(codeListEntryFromSap.getCode());
        codeListEntryInShop.setLastModifiedErp(SoapConversionHelper.convertTimestampFromSap(codeListEntryFromSap.getLastModifiedErp()));
        for (LocalizedString locStr : codeListEntryFromSap.getDescription()) {
            codeListEntryInShop.setNameErp(locStr.getValue(), new Locale(locStr.getLang()));
        }
        modelsToInsertOrUpdate.add(codeListEntryInShop);
    }
}
