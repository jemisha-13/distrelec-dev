/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import java.util.Date;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.sap.v1.CodeList;
import com.distrelec.webservice.sap.v1.CodeListUnit;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistRequest;
import com.distrelec.webservice.sap.v1.ReadModifiedCodelistResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import com.namics.distrelec.b2b.core.model.DistRestrictionOfHazardousSubstancesModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.core.model.jobs.UpdateCodelistsCronJobModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

/**
 * Job that reads the modified codelists from the SAP system and updates the codelists in hybris.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class UpdateCodelistsJob extends AbstractJobPerformable<UpdateCodelistsCronJobModel> {

    private static final Logger LOG = LogManager.getLogger(UpdateCodelistsJob.class);

    @Autowired
    private DistrelecCodelistService distrelecCodelistService;

    private SIHybrisV1Out webServiceClient;

    public DistrelecCodelistService getDistrelecCodelistService() {
        return distrelecCodelistService;
    }

    public void setDistrelecCodelistService(final DistrelecCodelistService distrelecCodelistService) {
        this.distrelecCodelistService = distrelecCodelistService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    @Override
    public PerformResult perform(final UpdateCodelistsCronJobModel cronJob) {
        final ReadModifiedCodelistRequest request = new ReadModifiedCodelistRequest();
        final ReadModifiedCodelistResponse response = executeSOAPRequest(request);
        if (null == response) {
            LOG.error("The call to SAP PI IF-50 Codelist service returned no response. No CodeLists can be updated.");
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        updateCodelists(response);
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private ReadModifiedCodelistResponse executeSOAPRequest(final ReadModifiedCodelistRequest request) {
        ReadModifiedCodelistResponse response = null;
        final long startTime = new Date().getTime();
        try {
            response = webServiceClient.if50ReadModifiedCodelist(request);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if50ReadModifiedCodelist", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if50ReadModifiedCodelist", webServiceException);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-50 Codelist Service took " + (endTime - startTime) + "ms");
        return response;
    }

    private void updateCodelists(final ReadModifiedCodelistResponse response) {
        // country: nearly static information, so do not import
        // currency: nearly static information, so do not import
        updateCodelistDepartments(response.getDepartment());
        updateCodelistFunctions(response.getFunction());
        updateCodelistPaymentMethods(response.getPaymentMethod());
        updateCodelistReplacementReasons(response.getReplacementReason());
        updateCodelistRestrictionsOfHazardousSubstances(response.getRohs());
        // sales org: we would need additional information from the SAP system, so do not import
        updateCodelistSalesStatus(response.getSalesStatus());
        updateCodelistSalesUnits(response.getSalesUnit());
        updateCodelistShippingMethods(response.getShippingMethod());
        // tax class: we would need additional information from the SAP system, so do not import
        updateCodelistTransportGroups(response.getTransportGroup());
        // warehouse: we would need additional information from the SAP system, so do not import
    }

    private void updateCodelistDepartments(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistDepartmentModel> codeListInShop = getDistrelecCodelistService().getAllDistDepartments();
        // compute entries to insert or update
        final List<DistDepartmentModel> departmentsToInsertOrUpdate = new CodelistUpdateHandler<DistDepartmentModel>(codeListFromSap, codeListInShop,
                DistDepartmentModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistDepartmentModel departmentToInsertOrUpdate : departmentsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistDepartment(departmentToInsertOrUpdate);
        }
    }

    private void updateCodelistFunctions(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistFunctionModel> codeListInShop = getDistrelecCodelistService().getAllDistFunctions();
        // compute entries to insert or update
        final List<DistFunctionModel> functionsToInsertOrUpdate = new CodelistUpdateHandler<DistFunctionModel>(codeListFromSap, codeListInShop,
                DistFunctionModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistFunctionModel functionToInsertOrUpdate : functionsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistFunction(functionToInsertOrUpdate);
        }
    }

    private void updateCodelistPaymentMethods(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistPaymentMethodModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecPaymentMethod();
        // compute entries to insert or update
        final List<DistPaymentMethodModel> paymentMethodsToInsertOrUpdate = new CodelistUpdateHandler<DistPaymentMethodModel>(codeListFromSap, codeListInShop,
                DistPaymentMethodModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistPaymentMethodModel paymentMethodToInsertOrUpdate : paymentMethodsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistPaymentMethod(paymentMethodToInsertOrUpdate);
        }
    }

    private void updateCodelistReplacementReasons(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistReplacementReasonModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecReplacementReason();
        // compute entries to insert or update
        final List<DistReplacementReasonModel> replacementReasonsToInsertOrUpdate = new CodelistUpdateHandler<DistReplacementReasonModel>(codeListFromSap,
                codeListInShop, DistReplacementReasonModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistReplacementReasonModel replacementReasonToInsertOrUpdate : replacementReasonsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistrelecReplacementReason(replacementReasonToInsertOrUpdate);
        }
    }

    private void updateCodelistRestrictionsOfHazardousSubstances(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistRestrictionOfHazardousSubstancesModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecRestrictionOfHazardousSubstances();
        // compute entries to insert or update
        final List<DistRestrictionOfHazardousSubstancesModel> restrictionsToInsertOrUpdate = new CodelistUpdateHandler<DistRestrictionOfHazardousSubstancesModel>(
                codeListFromSap, codeListInShop, DistRestrictionOfHazardousSubstancesModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistRestrictionOfHazardousSubstancesModel restrictionToInsertOrUpdate : restrictionsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistrelecRestrictionOfHazardousSubstances(restrictionToInsertOrUpdate);
        }
    }

    private void updateCodelistSalesStatus(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistSalesStatusModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecSalesStatus();
        // compute entries to insert or update
        final List<DistSalesStatusModel> salesStatusToInsertOrUpdate = new CodelistUpdateHandler<DistSalesStatusModel>(codeListFromSap, codeListInShop,
                DistSalesStatusModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistSalesStatusModel salesStatToInsertOrUpdate : salesStatusToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistrelecSalesStatus(salesStatToInsertOrUpdate);
        }
    }

    private void updateCodelistSalesUnits(final List<CodeListUnit> codeListUnitFromSap) {
        // get current state of codelist in shop
        final List<DistSalesUnitModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecSalesUnit();
        // compute entries to insert or update
        final List<DistSalesUnitModel> salesModelsToInsertOrUpdate = new CodelistUnitUpdateHandler(flexibleSearchService, codeListUnitFromSap, codeListInShop)
                .prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistSalesUnitModel salesModelToInsertOrUpdate : salesModelsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistrelecSalesUnit(salesModelToInsertOrUpdate);
        }
    }

    private void updateCodelistShippingMethods(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistShippingMethodModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecShippingMethod();
        // compute entries to insert or update
        final List<DistShippingMethodModel> shippingMethodsToInsertOrUpdate = new CodelistUpdateHandler<DistShippingMethodModel>(codeListFromSap,
                codeListInShop, DistShippingMethodModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistShippingMethodModel shippingMethodToInsertOrUpdate : shippingMethodsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistShippingtMethod(shippingMethodToInsertOrUpdate);
        }
    }

    private void updateCodelistTransportGroups(final List<CodeList> codeListFromSap) {
        // get current state of codelist in shop
        final List<DistTransportGroupModel> codeListInShop = getDistrelecCodelistService().getAllDistrelecTransportGroup();
        // compute entries to insert or update
        final List<DistTransportGroupModel> transportGroupsToInsertOrUpdate = new CodelistUpdateHandler<DistTransportGroupModel>(codeListFromSap,
                codeListInShop, DistTransportGroupModel.class).prepareModelsForInsertOrUpdate();
        // insert or update the entries
        for (DistTransportGroupModel transportGroupToInsertOrUpdate : transportGroupsToInsertOrUpdate) {
            getDistrelecCodelistService().insertOrUpdateDistrelecTransportGroup(transportGroupToInsertOrUpdate);
        }
    }

}
