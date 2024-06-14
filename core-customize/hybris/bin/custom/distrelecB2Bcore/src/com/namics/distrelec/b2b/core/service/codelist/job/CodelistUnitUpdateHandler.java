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
import com.distrelec.webservice.sap.v1.CodeListUnit;
import com.distrelec.webservice.sap.v1.LocalizedString;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Handler for updating the salesUnit codelist in the shop with the codelist coming from the SAP system.
 * 
 * We can't use {@link CodelistUpdateHandler} here, because {@link CodeListUnit} is not inherited from {@link CodeList}, because they are
 * generated from the .wsdl file coming from the SAP PI system, which defines them independently.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
public class CodelistUnitUpdateHandler {

    private static final Logger LOG = Logger.getLogger(CodelistUnitUpdateHandler.class);

    private final FlexibleSearchService flexibleSearchService;

    private final List<CodeListUnit> codeListFromSap;

    private final List<DistSalesUnitModel> codeListInShop;

    private List<DistSalesUnitModel> modelsToInsertOrUpdate;

    private Map<String, DistSalesUnitModel> codeToCodeListEntryMap;

    public CodelistUnitUpdateHandler(final FlexibleSearchService flexibleSearchService, final List<CodeListUnit> codeListFromSap,
            final List<DistSalesUnitModel> codeListInShop) {
        this.flexibleSearchService = flexibleSearchService;
        this.codeListFromSap = codeListFromSap;
        this.codeListInShop = codeListInShop;
    }

    public List<DistSalesUnitModel> prepareModelsForInsertOrUpdate() {
        modelsToInsertOrUpdate = new ArrayList<DistSalesUnitModel>();
        buildCodeToCodeListEntryMap();

        for (CodeListUnit codeListEntryFromSap : codeListFromSap) {
            final DistSalesUnitModel codeListEntryInShop = codeToCodeListEntryMap.get(codeListEntryFromSap.getCode());
            if (null == codeListEntryInShop) {
                // not found in shop -> add
                LOG.info("inserting new DistSalesUnitModel with code " + codeListEntryFromSap.getCode());
                addInsertOrUpdateEntry(codeListEntryFromSap, new DistSalesUnitModel());
                continue;
            }
            final Date lastModifiedInShop = codeListEntryInShop.getLastModifiedErp();
            if (null == lastModifiedInShop) {
                // never updated from SAP -> update
                LOG.info("updating existing DistSalesUnitModel with code " + codeListEntryFromSap.getCode());
                addInsertOrUpdateEntry(codeListEntryFromSap, codeListEntryInShop);
                continue;
            }
            final Date lastModifiedInSap = SoapConversionHelper.convertTimestampFromSap(codeListEntryFromSap.getLastModifiedErp());
            if (lastModifiedInSap.after(lastModifiedInShop)) {
                // newer in SAP -> update
                LOG.info("updating existing DistSalesUnitModel with code " + codeListEntryFromSap.getCode());
                addInsertOrUpdateEntry(codeListEntryFromSap, codeListEntryInShop);
            }
        }

        return modelsToInsertOrUpdate;
    }

    private void buildCodeToCodeListEntryMap() {
        codeToCodeListEntryMap = new HashMap<String, DistSalesUnitModel>();
        for (DistSalesUnitModel codeListEntryInShop : codeListInShop) {
            if (codeToCodeListEntryMap.containsKey(codeListEntryInShop.getCode())) {
                throw new IllegalStateException("found two DistSalesUnitModels with code " + codeListEntryInShop.getCode());
            }
            codeToCodeListEntryMap.put(codeListEntryInShop.getCode(), codeListEntryInShop);
        }
    }

    private void addInsertOrUpdateEntry(final CodeListUnit codeListEntryFromSap, final DistSalesUnitModel codeListEntryInShop) {
        codeListEntryInShop.setCode(codeListEntryFromSap.getCode());
        codeListEntryInShop.setLastModifiedErp(SoapConversionHelper.convertTimestampFromSap(codeListEntryFromSap.getLastModifiedErp()));
        for (LocalizedString locStr : codeListEntryFromSap.getDescription()) {
            codeListEntryInShop.setNameErp(locStr.getValue(), new Locale(locStr.getLang()));
        }
        codeListEntryInShop.setAmount(Double.valueOf(codeListEntryFromSap.getAmount().doubleValue()));
        final UnitModel unit = findUnitWithCode(codeListEntryFromSap.getUnit());
        if (null == unit) {
            LOG.error("Can not insert or update DistSalesUnitModel with code " + codeListEntryFromSap.getCode() + " because no UnitModel with code "
                    + codeListEntryFromSap.getUnit() + " could be resolved");
            return;
        }
        codeListEntryInShop.setUnit(unit);
        modelsToInsertOrUpdate.add(codeListEntryInShop);
    }

    private UnitModel findUnitWithCode(final String unitFromSap) {
        final String query = "SELECT {" + UnitModel.PK + "} FROM {" + UnitModel._TYPECODE + "} WHERE {" + UnitModel.CODE + "}=?sapCode";
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);
        flexibleSearchQuery.addQueryParameter("sapCode", unitFromSap);
        UnitModel unit = null;
        try {
            unit = flexibleSearchService.searchUnique(flexibleSearchQuery);
        } catch (final ModelNotFoundException e) {
            LOG.error("Can not find Unit with code " + unitFromSap);
        } catch (final AmbiguousIdentifierException e) {
            LOG.error("More than one Unit found with code " + unitFromSap);
        }
        return unit;
    }

}
