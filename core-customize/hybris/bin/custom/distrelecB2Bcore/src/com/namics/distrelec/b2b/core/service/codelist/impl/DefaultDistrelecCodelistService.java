/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistAssetTypeModel;
import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistDangerousGoodsProfileModel;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.DistHazardStatementModel;
import com.namics.distrelec.b2b.core.model.DistMaterialTypeModel;
import com.namics.distrelec.b2b.core.model.DistOrderChannelModel;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.model.DistRMAReasonModel;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import com.namics.distrelec.b2b.core.model.DistRestrictionOfHazardousSubstancesModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.DistSupplementalHazardInfoModel;
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.codelist.dao.DistrelecCodelistDao;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

/**
 * Implements the <code>DistrelecCodelisteService</code>.
 * 
 * @author rmeier, Namics AG
 * @author daehsuir, Distrelec
 * 
 */
public class DefaultDistrelecCodelistService extends AbstractBusinessService implements DistrelecCodelistService {

    @Autowired
    private DistrelecCodelistDao distrelecCodelistDao;

    @Override
    public DistAssetTypeModel getDistrelecAssetType(final String code) {
        return (DistAssetTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistAssetTypeModel._TYPECODE);
    }

    @Override
    public List<DistAssetTypeModel> getAllDistrelecAssetType() {
        return (List<DistAssetTypeModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistAssetTypeModel._TYPECODE);
    }

    @Override
    public DistAssetTypeModel getOrInsertDistrelecAssetType(final String code) {
        return (DistAssetTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistAssetTypeModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecAssetType(final DistAssetTypeModel distAssetTypeModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distAssetTypeModel);
    }

    @Override
    public DistBrandModel getDistrelecBrand(final String code) {
        return (DistBrandModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistBrandModel._TYPECODE);
    }

    @Override
    public List<DistBrandModel> getAllDistrelecBrand() {
        return (List<DistBrandModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistBrandModel._TYPECODE);
    }

    @Override
    public DistBrandModel getOrInsertDistrelecBrand(final String code) {
        return (DistBrandModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistBrandModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecBrand(final DistBrandModel distBrandModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distBrandModel);
    }

    @Override
    public DistDangerousGoodsProfileModel getDistrelecDangerousGoodsProfile(final String code) {
        return (DistDangerousGoodsProfileModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistDangerousGoodsProfileModel._TYPECODE);
    }

    @Override
    public List<DistHazardStatementModel> getAllDistHazardStatement() {
        return (List<DistHazardStatementModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistHazardStatementModel._TYPECODE);
    }

    @Override
    public List<DistSupplementalHazardInfoModel> getAllDistSupplementalHazardInfo() {
        return (List<DistSupplementalHazardInfoModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistSupplementalHazardInfoModel._TYPECODE);
    }
    
    @Override
    public List<DistDangerousGoodsProfileModel> getAllDistrelecDangerousGoodsProfile() {
        return (List<DistDangerousGoodsProfileModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistDangerousGoodsProfileModel._TYPECODE);
    }
    
    @Override
    public DistDangerousGoodsProfileModel getOrInsertDistrelecDangerousGoodsProfile(final String code) {
        return (DistDangerousGoodsProfileModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistDangerousGoodsProfileModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecDangerousGoodsProfile(final DistDangerousGoodsProfileModel distDangerousGoodsProfileModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distDangerousGoodsProfileModel);
    }

    @Override
    public DistDocumentTypeModel getDistrelecDocumentType(final String code) {
        return (DistDocumentTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistDocumentTypeModel._TYPECODE);
    }

    @Override
    public List<DistDocumentTypeModel> getAllDistrelecDocumentType() {
        return (List<DistDocumentTypeModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistDocumentTypeModel._TYPECODE);
    }

    @Override
    public DistDocumentTypeModel getOrInsertDistrelecDocumentType(final String code) {
        return (DistDocumentTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistDocumentTypeModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecDocumentType(final DistDocumentTypeModel distDocumentTypeModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distDocumentTypeModel);
    }

    @Override
    public DistMaterialTypeModel getDistrelecMaterialType(final String code) {
        return (DistMaterialTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistMaterialTypeModel._TYPECODE);
    }

    @Override
    public List<DistMaterialTypeModel> getAllDistrelecMaterialType() {
        return (List<DistMaterialTypeModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistMaterialTypeModel._TYPECODE);
    }

    @Override
    public DistMaterialTypeModel getOrInsertDistrelecMaterialType(final String code) {
        return (DistMaterialTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistMaterialTypeModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecMaterialType(final DistMaterialTypeModel distMaterialTypeModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distMaterialTypeModel);
    }

    @Override
    public DistSalesStatusModel getDistrelecSalesStatus(final String code) {
        return (DistSalesStatusModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesStatusModel._TYPECODE);
    }

    @Override
    public List<DistSalesStatusModel> getAllDistrelecSalesStatus() {
        return (List<DistSalesStatusModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistSalesStatusModel._TYPECODE);
    }

    @Override
    public DistSalesStatusModel getOrInsertDistrelecSalesStatus(final String code) {
        return (DistSalesStatusModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesStatusModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecSalesStatus(final DistSalesStatusModel distSalesStatusModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distSalesStatusModel);
    }

    @Override
    public DistPromotionLabelModel getDistrelecPromotionLabel(final String code) {
        return (DistPromotionLabelModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistPromotionLabelModel._TYPECODE);
    }

    @Override
    public List<DistPromotionLabelModel> getAllDistrelecPromotionLabel() {
        return (List<DistPromotionLabelModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistPromotionLabelModel._TYPECODE);
    }

    @Override
    public DistPromotionLabelModel getOrInsertDistrelecPromotionLabel(final String code) {
        return (DistPromotionLabelModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistPromotionLabelModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecPromotionLabel(final DistPromotionLabelModel distPromotionLabelModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distPromotionLabelModel);
    }

    @Override
    public DistReplacementReasonModel getDistrelecReplacementReason(final String code) {
        return (DistReplacementReasonModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistReplacementReasonModel._TYPECODE);
    }

    @Override
    public List<DistReplacementReasonModel> getAllDistrelecReplacementReason() {
        return (List<DistReplacementReasonModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistReplacementReasonModel._TYPECODE);
    }

    @Override
    public DistReplacementReasonModel getOrInsertDistrelecReplacementReason(final String code) {
        return (DistReplacementReasonModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistReplacementReasonModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecReplacementReason(final DistReplacementReasonModel distReplacementReasonModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distReplacementReasonModel);
    }

    @Override
    public DistRestrictionOfHazardousSubstancesModel getDistrelecRestrictionOfHazardousSubstances(final String code) {
        return (DistRestrictionOfHazardousSubstancesModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code,
                DistRestrictionOfHazardousSubstancesModel._TYPECODE);
    }

    @Override
    public List<DistRestrictionOfHazardousSubstancesModel> getAllDistrelecRestrictionOfHazardousSubstances() {
        return (List<DistRestrictionOfHazardousSubstancesModel>) distrelecCodelistDao
                .getAllCodelistEntries(DistRestrictionOfHazardousSubstancesModel._TYPECODE);
    }

    @Override
    public DistRestrictionOfHazardousSubstancesModel getOrInsertDistrelecRestrictionOfHazardousSubstances(final String code) {
        return (DistRestrictionOfHazardousSubstancesModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code,
                DistRestrictionOfHazardousSubstancesModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecRestrictionOfHazardousSubstances(
            final DistRestrictionOfHazardousSubstancesModel distRestrictionOfHazardousSubstancesModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distRestrictionOfHazardousSubstancesModel);
    }

    @Override
    public DistSalesOrgModel getDistrelecSalesOrg(final String code) {
        return (DistSalesOrgModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesOrgModel._TYPECODE);
    }

    @Override
    public List<DistSalesOrgModel> getAllDistrelecSalesOrg() {
        return (List<DistSalesOrgModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistSalesOrgModel._TYPECODE);
    }

    @Override
    public DistSalesOrgModel getOrInsertDistrelecSalesOrg(final String code) {
        return (DistSalesOrgModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesOrgModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecSalesOrg(final DistSalesOrgModel distSalesOrgModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distSalesOrgModel);
    }

    @Override
    public DistSalesUnitModel getDistrelecSalesUnit(final String code) {
        return (DistSalesUnitModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesUnitModel._TYPECODE);
    }

    @Override
    public List<DistSalesUnitModel> getAllDistrelecSalesUnit() {
        return (List<DistSalesUnitModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistSalesUnitModel._TYPECODE);
    }

    @Override
    public DistSalesUnitModel getOrInsertDistrelecSalesUnit(final String code) {
        return (DistSalesUnitModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistSalesUnitModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecSalesUnit(final DistSalesUnitModel distSalesUnitModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distSalesUnitModel);
    }

    @Override
    public DistTransportGroupModel getDistrelecTransportGroup(final String code) {
        return (DistTransportGroupModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistTransportGroupModel._TYPECODE);
    }

    @Override
    public List<DistTransportGroupModel> getAllDistrelecTransportGroup() {
        return (List<DistTransportGroupModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistTransportGroupModel._TYPECODE);
    }

    @Override
    public DistTransportGroupModel getOrInsertDistrelecTransportGroup(final String code) {
        return (DistTransportGroupModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistTransportGroupModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistrelecTransportGroup(final DistTransportGroupModel distTransportGroupModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distTransportGroupModel);
    }

    @Override
    public DistPaymentMethodModel getDistrelecPaymentMethod(final String code) {
        return (DistPaymentMethodModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistPaymentMethodModel._TYPECODE);
    }

    @Override
    public List<DistPaymentMethodModel> getAllDistrelecPaymentMethod() {
        return (List<DistPaymentMethodModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistPaymentMethodModel._TYPECODE);
    }

    @Override
    public DistPaymentMethodModel getOrInsertDistPaymentMethod(final String code) {
        return (DistPaymentMethodModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistPaymentMethodModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistPaymentMethod(final DistPaymentMethodModel distPaymentMethodModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distPaymentMethodModel);
    }

    @Override
    public DistShippingMethodModel getDistrelecShippingMethod(final String code) {
        return (DistShippingMethodModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistShippingMethodModel._TYPECODE);
    }

    @Override
    public List<DistShippingMethodModel> getAllDistrelecShippingMethod() {
        return (List<DistShippingMethodModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistShippingMethodModel._TYPECODE);
    }

    @Override
    public DistShippingMethodModel getOrInsertDistShippingMethod(final String code) {
        return (DistShippingMethodModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistShippingMethodModel._TYPECODE);
    }

    @Override
    public void insertOrUpdateDistShippingtMethod(final DistShippingMethodModel distShippingMethodModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distShippingMethodModel);
    }

    @Override
    public DistRMAReasonModel getDistRMAReason(final String code) {
        return (DistRMAReasonModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistRMAReasonModel._TYPECODE);
    }

    @Override
    public List<DistRMAReasonModel> getAllDistRMAReasons() {
        return (List<DistRMAReasonModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistRMAReasonModel._TYPECODE);
    }

    @Override
    public DistDepartmentModel getDistDepartment(final String code) {
        return (DistDepartmentModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistDepartmentModel._TYPECODE);
    }

    @Override
    public List<DistDepartmentModel> getAllDistDepartments() {
        return getDistDepartmentsByErpSystem(DistErpSystem.SAP);
    }

    @Override
    public List<DistDepartmentModel> getDistDepartmentsByErpSystem(final DistErpSystem erpSystem) {
        final DistErpSystem defaultErpSystem = (erpSystem != null ? erpSystem : DistErpSystem.SAP);
        return (List<DistDepartmentModel>) CollectionUtils.select(getDistrelecCodelistDao().getAllCodelistEntries(DistDepartmentModel._TYPECODE),
                new Predicate() {

                    @Override
                    public boolean evaluate(final Object object) {
                        return defaultErpSystem.equals(((DistDepartmentModel) object).getErpSystem());
                    }
                });
    }

    @Override
    public void insertOrUpdateDistDepartment(final DistDepartmentModel distDepartmentModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distDepartmentModel);
    }

    @Override
    public DistFunctionModel getDistFunction(final String code) {
        return (DistFunctionModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistFunctionModel._TYPECODE);
    }

    @Override
    public List<DistFunctionModel> getAllDistFunctions() {
        return getDistFunctionsByErpSystem(DistErpSystem.SAP);
    }

    @Override
    public List<DistFunctionModel> getDistFunctionsByErpSystem(final DistErpSystem erpSystem) {
        final DistErpSystem defaultErpSystem = (erpSystem != null ? erpSystem : DistErpSystem.SAP);
        return (List<DistFunctionModel>) CollectionUtils.select(getDistrelecCodelistDao().getAllCodelistEntries(DistFunctionModel._TYPECODE), new Predicate() {

            @Override
            public boolean evaluate(final Object object) {
                return defaultErpSystem.equals(((DistFunctionModel) object).getErpSystem());
            }
        });
    }

    @Override
    public void insertOrUpdateDistFunction(final DistFunctionModel distFunctionModel) {
        getDistrelecCodelistDao().insertOrUpdateDistrelecCodelistEntry(distFunctionModel);
    }

    @Override
    public DistOrderStatusModel getDistOrderStatus(final String code) {
        return (DistOrderStatusModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistOrderStatusModel._TYPECODE);
    }

    @Override
    public DistOrderStatusModel getDistOrderStatusForHybrisOrderStatusCode(final String yOSCode) {
        return getAllDistOrderStatus().stream().filter(os -> os.getHybrisOrderStatus().getCode().equals(yOSCode)).findFirst().orElse(null);
    }

    @Override
    public List<DistOrderStatusModel> getAllDistOrderStatus() {
        return (List<DistOrderStatusModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistOrderStatusModel._TYPECODE);
    }

    @Override
    public DistPimCategoryTypeModel getDistPimCategoryType(final String code) {
        return (DistPimCategoryTypeModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistPimCategoryTypeModel._TYPECODE);
    }

    @Override
    public List<DistPimCategoryTypeModel> getAllDistPimCategoryTypes() {
        return (List<DistPimCategoryTypeModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistPimCategoryTypeModel._TYPECODE);
    }

    @Override
    public DistOrderChannelModel getDistOrderChannel(String code) {
        return (DistOrderChannelModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistOrderChannelModel._TYPECODE);
    }

    @Override
    public List<DistOrderChannelModel> getAllDistOrderChannels() {
        return (List<DistOrderChannelModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistOrderChannelModel._TYPECODE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService#getDistQuotationStatus(java.lang.String)
     */
    @Override
    public DistQuotationStatusModel getDistQuotationStatus(final String code) {
        return (DistQuotationStatusModel) getDistrelecCodelistDao().getDistrelecCodelistEntry(code, DistQuotationStatusModel._TYPECODE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService#getAllDistQuotationStatus()
     */
    @Override
    public List<DistQuotationStatusModel> getAllDistQuotationStatus() {
        return (List<DistQuotationStatusModel>) getDistrelecCodelistDao().getAllCodelistEntries(DistQuotationStatusModel._TYPECODE);
    }

    public DistrelecCodelistDao getDistrelecCodelistDao() {
        return distrelecCodelistDao;
    }

    public void setDistrelecCodelistDao(DistrelecCodelistDao distrelecCodelistDao) {
        this.distrelecCodelistDao = distrelecCodelistDao;
    }
}
