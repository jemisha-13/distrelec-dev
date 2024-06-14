package com.namics.distrelec.b2b.core.punchouts.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCTPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.customer.CustomerPunchoutEntryModel;
import com.namics.distrelec.b2b.core.model.jobs.SyncCustomerPunchoutsCronJobModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class SyncCustomerPunchoutsJob extends AbstractJobPerformable<SyncCustomerPunchoutsCronJobModel> {

    private static final Logger LOG = Logger.getLogger(SyncCustomerPunchoutsJob.class);

    private ModelService modelService;

    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    private DistSalesOrgService distSalesOrgService;

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public PerformResult perform(final SyncCustomerPunchoutsCronJobModel cronJob) {
        int counter = 0;
        int partCounter = 0;
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {c.").append(B2BUnitModel.PK).append("} FROM {").append(B2BUnitModel._TYPECODE).append(" as c}");
        final SearchResult<B2BUnitModel> result = flexibleSearchService.search(query.toString());

        if (CollectionUtils.isNotEmpty(result.getResult())) {
            LOG.info("synchronizing customer punchouts for " + result.getCount() + " customers");
            for (final B2BUnitModel company : result.getResult()) {
                partCounter++;
                if (partCounter == 1000) {
                    counter = counter + 1000;
                    partCounter = 0;
                    LOG.info(counter + " customers already synchronized");
                }
                final List<String> punchouts = new ArrayList<String>();
                final Date date = new Date();
                loadCountryPunchouts(punchouts, date, company.getSalesOrg());
                loadCustomerPunchouts(company, company.getSalesOrg(), punchouts, date);
//                loadCustomerTypePunchouts(company, punchouts, date);
//                loadManufacturerPunchouts(company, punchouts, date);
                if (CollectionUtils.isNotEmpty(punchouts)) {
                    LOG.info("adding punchouts " + punchouts.toString() + " to customer " + company.getErpCustomerID());
                    final List<CustomerPunchoutEntryModel> punchoutList = new ArrayList<CustomerPunchoutEntryModel>();
                    for (final String punchout : punchouts) {
                        if (!customerPunchoutAlreadyExists(punchout, company)) {
                            punchoutList.add(createEntry(punchout, company));
                        } else {
                            punchoutList.addAll(getAlreadyExistingCustomerPunchoutEntry(punchout, company));
                        }
                    }
                    company.setCompanyPunchouts(punchoutList);
                    try {
                        getModelService().save(company);
                    } catch (ModelSavingException ex) {
                        LOG.error("ModelSavingException: \n" + ex.getMessage());
                        LOG.error("customer: " + company.getErpCustomerID());
                    }
                }
            }
            // CLEAN CUSTOMER'S OLD PUNCHOUTS NOW
            cleanOldPunchoutsForCustomer();
        }
        LOG.info("synchronizing is done.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void cleanOldPunchoutsForCustomer() {
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {pe.").append(CustomerPunchoutEntryModel.PK).append("} FROM {").append(CustomerPunchoutEntryModel._TYPECODE)
                .append(" as pe} WHERE {pe." + CustomerPunchoutEntryModel.COMPANY + "} is null");
        final SearchResult<CustomerPunchoutEntryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result != null && result.getResult() != null) {
            for (final CustomerPunchoutEntryModel punchoutEntry : result.getResult()) {
                modelService.remove(punchoutEntry);
            }
        }
    }

    private List<CustomerPunchoutEntryModel> getAlreadyExistingCustomerPunchoutEntry(final String punchoutCode, final B2BUnitModel company) {
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {pe.").append(CustomerPunchoutEntryModel.PK).append("} FROM {").append(CustomerPunchoutEntryModel._TYPECODE)
                .append(" as pe} WHERE {pe." + CustomerPunchoutEntryModel.COMPANY + "}=(?").append(CustomerPunchoutEntryModel.COMPANY).append(")");
        query.append(" AND {pe." + CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE + "}=(?").append(CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE)
                .append(")");
        params.put(CustomerPunchoutEntryModel.COMPANY, company);
        params.put(CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE, punchoutCode);
        final SearchResult<CustomerPunchoutEntryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result == null || CollectionUtils.isEmpty(result.getResult())) {
            return Collections.<CustomerPunchoutEntryModel> emptyList();
        }
        return result.getResult();
    }

    private boolean customerPunchoutAlreadyExists(final String punchoutCode, final B2BUnitModel company) {
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {pe.").append(CustomerPunchoutEntryModel.PK).append("} FROM {").append(CustomerPunchoutEntryModel._TYPECODE)
                .append(" as pe} WHERE {pe." + CustomerPunchoutEntryModel.COMPANY + "}=(?").append(CustomerPunchoutEntryModel.COMPANY).append(")");
        query.append(" AND {pe." + CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE + "}=(?").append(CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE)
                .append(")");
        params.put(CustomerPunchoutEntryModel.COMPANY, company);
        params.put(CustomerPunchoutEntryModel.PRODUCTHIERARCHYCODE, punchoutCode);
        final SearchResult<CustomerPunchoutEntryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result == null || CollectionUtils.isEmpty(result.getResult())) {
            return false;
        }
        return true;
    }

    private CustomerPunchoutEntryModel createEntry(final String punchoutCode, final B2BUnitModel company) {
        final CustomerPunchoutEntryModel entry = getModelService().create(CustomerPunchoutEntryModel.class);
        entry.setProductHierarchyCode(punchoutCode);
        entry.setCompany(company);
        getModelService().save(entry);
        getModelService().refresh(entry);
        return entry;
    }

//    private void loadManufacturerPunchouts(final B2BUnitModel unit, final List<String> punchouts, final Date date) {
//        if (unit != null && date != null && unit.getErpCustomerID() != null) {
//            final StringBuilder query = new StringBuilder();
//            query.append("SELECT  {po.").append(DistManufacturerPunchOutFilterModel.PK).append("} FROM {")
//                    .append(DistManufacturerPunchOutFilterModel._TYPECODE).append(" as po ");
//            query.append(" JOIN ").append(B2BUnitModel._TYPECODE).append(" as co ON {po:").append(DistManufacturerPunchOutFilterModel.ERPCUSTOMERID)
//                    .append("} = {co:").append(B2BUnitModel.ERPCUSTOMERID).append("}}");
//            query.append(" WHERE {po." + B2BUnitModel.ERPCUSTOMERID + "}=(?" + B2BUnitModel.ERPCUSTOMERID + ")");
//
//            query.append(" AND {po.").append(DistManufacturerPunchOutFilterModel.PRODUCTHIERARCHY).append("} is not null");
//            query.append(" AND {po." + DistManufacturerPunchOutFilterModel.VALIDFROMDATE + "}<=(?" + DistManufacturerPunchOutFilterModel.VALIDFROMDATE + ")");
//            query.append(" AND {po." + DistManufacturerPunchOutFilterModel.VALIDUNTILDATE + "}>=(?" + DistManufacturerPunchOutFilterModel.VALIDUNTILDATE + ")");
//
//            final Map<String, Object> params = new HashMap<String, Object>();
//            params.put(DistManufacturerPunchOutFilterModel.ERPCUSTOMERID, unit.getErpCustomerID());
//            params.put(DistManufacturerPunchOutFilterModel.VALIDFROMDATE, date);
//            params.put(DistManufacturerPunchOutFilterModel.VALIDUNTILDATE, date);
//            final SearchResult<DistManufacturerPunchOutFilterModel> result = flexibleSearchService.search(query.toString(), params);
//            if (CollectionUtils.isNotEmpty(result.getResult())) {
//                for (final DistManufacturerPunchOutFilterModel po : result.getResult()) {
//                    final String hierarchy = po.getProductHierarchy();
//                    if (!punchouts.contains(hierarchy)) {
//                        punchouts.add(hierarchy);
//                    }
//                }
//
//            }
//        }
//    }

//    private void loadCustomerTypePunchouts(final B2BUnitModel company, final List<String> punchouts, final Date date) {
//        if (company != null && company.getCustomerType() != null) {
//            final StringBuilder query = new StringBuilder();
//            query.append("SELECT  {po.").append(DistCTPunchOutFilterModel.PK).append("} FROM {").append(DistCTPunchOutFilterModel._TYPECODE)
//                    .append(" as po} WHERE {po." + DistCTPunchOutFilterModel.CUSTOMERTYPE + "}=(?" + DistCTPunchOutFilterModel.CUSTOMERTYPE + ")");
//            query.append(" AND {po.").append(DistCTPunchOutFilterModel.PRODUCTHIERARCHY).append("} is not null");
//            query.append(" AND {po." + DistCTPunchOutFilterModel.VALIDFROMDATE + "}<=(?" + DistCTPunchOutFilterModel.VALIDFROMDATE + ")");
//            query.append(" AND {po." + DistCTPunchOutFilterModel.VALIDUNTILDATE + "}>=(?" + DistCTPunchOutFilterModel.VALIDUNTILDATE + ")");
//
//            final Map<String, Object> params = new HashMap<String, Object>();
//            params.put(DistCTPunchOutFilterModel.VALIDFROMDATE, date);
//            params.put(DistCTPunchOutFilterModel.VALIDUNTILDATE, date);
//
//            if (company.getCustomerType().getCode().equalsIgnoreCase("b2c")) {
//                params.put(DistCTPunchOutFilterModel.CUSTOMERTYPE, SiteChannel.B2C);
//            } else {
//                params.put(DistCTPunchOutFilterModel.CUSTOMERTYPE, SiteChannel.B2B);
//            }
//            final SearchResult<DistCTPunchOutFilterModel> result = flexibleSearchService.search(query.toString(), params);
//            if (CollectionUtils.isNotEmpty(result.getResult())) {
//                for (final DistCTPunchOutFilterModel po : result.getResult()) {
//                    final String hierarchy = po.getProductHierarchy();
//                    if (!punchouts.contains(hierarchy)) {
//                        punchouts.add(hierarchy);
//                    }
//                }
//
//            }
//        }
//    }

    private void loadCustomerPunchouts(final B2BUnitModel unit, final DistSalesOrgModel salesOrg, final List<String> punchouts, final Date date) {
        if (unit != null && salesOrg != null && unit.getErpCustomerID() != null) {
            final StringBuilder query = new StringBuilder();
            query.append("SELECT  {po.").append(DistCUPunchOutFilterModel.PK).append("} FROM {").append(DistCUPunchOutFilterModel._TYPECODE).append(" as po ");
            query.append(" JOIN ").append(B2BUnitModel._TYPECODE).append(" as co ON {po:").append(DistCUPunchOutFilterModel.ERPCUSTOMERID).append("} = {co:")
                    .append(DistCUPunchOutFilterModel.ERPCUSTOMERID).append("}}");
            query.append(" WHERE {po." + DistCUPunchOutFilterModel.ERPCUSTOMERID + "}=(?" + DistCUPunchOutFilterModel.ERPCUSTOMERID + ")");
            query.append(" AND {po." + DistCUPunchOutFilterModel.SALESORG + "}=(?" + DistCUPunchOutFilterModel.SALESORG + ")");
            query.append(" AND {po.").append(DistCUPunchOutFilterModel.PRODUCTHIERARCHY).append("} is not null");
            query.append(" AND {po." + DistCUPunchOutFilterModel.VALIDFROMDATE + "}<=(?" + DistCUPunchOutFilterModel.VALIDFROMDATE + ")");
            query.append(" AND {po." + DistCUPunchOutFilterModel.VALIDUNTILDATE + "}>=(?" + DistCUPunchOutFilterModel.VALIDUNTILDATE + ")");

            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(DistCUPunchOutFilterModel.ERPCUSTOMERID, unit.getErpCustomerID());
            params.put(DistCUPunchOutFilterModel.SALESORG, salesOrg);
            params.put(DistCUPunchOutFilterModel.VALIDFROMDATE, date);
            params.put(DistCUPunchOutFilterModel.VALIDUNTILDATE, date);

            final SearchResult<DistCUPunchOutFilterModel> result = flexibleSearchService.search(query.toString(), params);
            if (CollectionUtils.isNotEmpty(result.getResult())) {
                for (final DistCUPunchOutFilterModel po : result.getResult()) {
                    final String hierarchy = po.getProductHierarchy();
                    if (!punchouts.contains(hierarchy)) {
                        punchouts.add(hierarchy);
                    }
                }

            }
        }
    }

    private void loadCountryPunchouts(final List<String> punchouts, final Date date, final DistSalesOrgModel salesOrg) {
        if (salesOrg != null) {
            final StringBuilder query = new StringBuilder();
            query.append("SELECT  {po.").append(DistCOPunchOutFilterModel.PK).append("} FROM {").append(DistCOPunchOutFilterModel._TYPECODE)
                    .append(" as po} WHERE {po." + DistCOPunchOutFilterModel.COUNTRY + "}=(?" + DistCOPunchOutFilterModel.COUNTRY + ")");
            query.append(" AND {po." + DistCOPunchOutFilterModel.SALESORG + "}=(?" + DistCOPunchOutFilterModel.SALESORG + ")");
            query.append(" AND {po.").append(DistCOPunchOutFilterModel.PRODUCTHIERARCHY).append("} is not null");
            query.append(" AND {po." + DistCOPunchOutFilterModel.VALIDFROMDATE + "}<=(?" + DistCOPunchOutFilterModel.VALIDFROMDATE + ")");
            query.append(" AND {po." + DistCOPunchOutFilterModel.VALIDUNTILDATE + "}>=(?" + DistCOPunchOutFilterModel.VALIDUNTILDATE + ")");

            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(DistCOPunchOutFilterModel.COUNTRY, salesOrg.getCountry());
            params.put(DistCOPunchOutFilterModel.SALESORG, salesOrg);
            params.put(DistCOPunchOutFilterModel.VALIDFROMDATE, date);
            params.put(DistCOPunchOutFilterModel.VALIDUNTILDATE, date);
            final SearchResult<DistCOPunchOutFilterModel> result = flexibleSearchService.search(query.toString(), params);
            if (CollectionUtils.isNotEmpty(result.getResult())) {
                for (final DistCOPunchOutFilterModel po : result.getResult()) {
                    final String hierarchy = po.getProductHierarchy();
                    if (!punchouts.contains(hierarchy)) {
                        punchouts.add(hierarchy);
                    }
                }

            }
        }
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }
}
