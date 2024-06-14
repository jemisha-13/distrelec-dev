/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist;

import java.util.List;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistAssetTypeModel;
import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistDangerousGoodsProfileModel;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
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
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.core.model.DistSupplementalHazardInfoModel;
import com.namics.distrelec.b2b.core.model.DistHazardStatementModel;
import com.namics.distrelec.b2b.core.service.exception.InsertException;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

/**
 * This interface provides methods to handling the code lists.
 * 
 * @author daehusir, Distrelec
 * 
 */

public interface DistrelecCodelistService {

    /**
     * Returns {@link DistAssetTypeModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistAssetTypeModel} with the specified code is found
     * @return {@link DistAssetTypeModel}
     */
    DistAssetTypeModel getDistrelecAssetType(final String code);

    /**
     * Returns all {@link DistAssetTypeModel}.
     * 
     * @return a list of {@link DistAssetTypeModel}, or an empty list if no code list entries can be found.
     */
    List<DistAssetTypeModel> getAllDistrelecAssetType();

    /**
     * Returns {@link DistAssetTypeModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistAssetTypeModel} with the specified code.
     * @return {@link DistAssetTypeModel}
     */
    DistAssetTypeModel getOrInsertDistrelecAssetType(final String code);

    /**
     * Inserts or updates a {@link DistAssetTypeModel}.
     * 
     * @param distAssetTypeModel
     *            the {@link DistAssetTypeModel} to insert or update
     */
    void insertOrUpdateDistrelecAssetType(final DistAssetTypeModel distAssetTypeModel);

    /**
     * Returns {@link DistBrandModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistBrandModel} with the specified code is found
     * @return {@link DistBrandModel}
     */
    DistBrandModel getDistrelecBrand(final String code);

    /**
     * Returns all {@link DistBrandModel}.
     * 
     * @return a list of {@link DistBrandModel}, or an empty list if no code list entries can be found.
     */
    List<DistBrandModel> getAllDistrelecBrand();

    /**
     * Returns {@link DistBrandModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistBrandModel} with the specified code.
     * @return {@link DistBrandModel}
     */
    DistBrandModel getOrInsertDistrelecBrand(final String code);

    /**
     * Inserts or updates a {@link DistBrandModel}.
     * 
     * @param distBrandModel
     *            the {@link DistBrandModel} to insert or update
     */
    void insertOrUpdateDistrelecBrand(final DistBrandModel distBrandModel);

    /**
     * Returns {@link DistDangerousGoodsProfileModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistDangerousGoodsProfileModel} with the specified code is found
     * @return {@link DistDangerousGoodsProfileModel}
     */
    DistDangerousGoodsProfileModel getDistrelecDangerousGoodsProfile(final String code);

    /**
     * Returns all {@link DistDangerousGoodsProfileModel}.
     * 
     * @return a list of {@link DistDangerousGoodsProfileModel}, or an empty list if no code list entries can be found.
     */
    List<DistDangerousGoodsProfileModel> getAllDistrelecDangerousGoodsProfile();

    /**
     * Returns {@link DistDangerousGoodsProfileModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistDangerousGoodsProfileModel} with the specified code.
     * @return {@link DistDangerousGoodsProfileModel}
     */
    DistDangerousGoodsProfileModel getOrInsertDistrelecDangerousGoodsProfile(final String code);

    /**
     * Inserts or updates a {@link DistBrandModel}.
     * 
     * @param distDangerousGoodsProfileModel
     *            the {@link DistBrandModel} to insert or update
     */
    void insertOrUpdateDistrelecDangerousGoodsProfile(final DistDangerousGoodsProfileModel distDangerousGoodsProfileModel);

    /**
     * Returns {@link DistDocumentTypeModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistDocumentTypeModel} with the specified code is found
     * @return {@link DistDocumentTypeModel}
     */
    DistDocumentTypeModel getDistrelecDocumentType(final String code);

    /**
     * Returns all {@link DistDocumentTypeModel}.
     * 
     * @return a list of {@link DistDocumentTypeModel}, or an empty list if no code list entries can be found.
     */
    List<DistDocumentTypeModel> getAllDistrelecDocumentType();

    /**
     * Returns {@link DistDocumentTypeModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistDocumentTypeModel} with the specified code.
     * @return {@link DistDocumentTypeModel}
     */
    DistDocumentTypeModel getOrInsertDistrelecDocumentType(final String code);

    /**
     * Inserts or updates a {@link DistDocumentTypeModel}.
     * 
     * @param distDocumentTypeModel
     *            the {@link DistDocumentTypeModel} to insert or update
     */
    void insertOrUpdateDistrelecDocumentType(final DistDocumentTypeModel distDocumentTypeModel);

    /**
     * Returns {@link DistMaterialTypeModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistMaterialTypeModel} with the specified code is found
     * @return {@link DistMaterialTypeModel}
     */
    DistMaterialTypeModel getDistrelecMaterialType(final String code);

    /**
     * Returns all {@link DistMaterialTypeModel}.
     * 
     * @return a list of {@link DistMaterialTypeModel}, or an empty list if no code list entries can be found.
     */
    List<DistMaterialTypeModel> getAllDistrelecMaterialType();

    /**
     * Returns {@link DistMaterialTypeModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistMaterialTypeModel} with the specified code.
     * @return {@link DistMaterialTypeModel}
     */
    DistMaterialTypeModel getOrInsertDistrelecMaterialType(final String code);

    /**
     * Inserts or updates a {@link DistMaterialTypeModel}.
     * 
     * @param distMaterialTypeModel
     *            the {@link DistMaterialTypeModel} to insert or update
     */
    void insertOrUpdateDistrelecMaterialType(final DistMaterialTypeModel distMaterialTypeModel);

    /**
     * Returns {@link DistSalesStatusModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistSalesStatusModel} with the specified code is found
     * @return {@link DistSalesStatusModel}
     */
    DistSalesStatusModel getDistrelecSalesStatus(final String code);

    /**
     * Returns all {@link DistSalesStatusModel}.
     * 
     * @return a list of {@link DistSalesStatusModel}, or an empty list if no code list entries can be found.
     */
    List<DistSalesStatusModel> getAllDistrelecSalesStatus();

    /**
     * Returns {@link DistSalesStatusModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistSalesStatusModel} with the specified code.
     * @return {@link DistSalesStatusModel}
     */
    DistSalesStatusModel getOrInsertDistrelecSalesStatus(final String code);

    /**
     * Inserts or updates a {@link DistSalesStatusModel}.
     * 
     * @param distSalesStatusModel
     *            the {@link DistSalesStatusModel} to insert or update
     */
    void insertOrUpdateDistrelecSalesStatus(final DistSalesStatusModel distSalesStatusModel);

    /**
     * Returns {@link DistPromotionLabelModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistPromotionLabelModel} with the specified code is found
     * @return {@link DistPromotionLabelModel}
     */
    DistPromotionLabelModel getDistrelecPromotionLabel(final String code);

    /**
     * Returns all {@link DistPromotionLabelModel}.
     * 
     * @return a list of {@link DistPromotionLabelModel}, or an empty list if no code list entries can be found.
     */
    List<DistPromotionLabelModel> getAllDistrelecPromotionLabel();

    /**
     * Returns {@link DistPromotionLabelModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistPromotionLabelModel} with the specified code.
     * @return {@link DistPromotionLabelModel}
     */
    DistPromotionLabelModel getOrInsertDistrelecPromotionLabel(final String code);

    /**
     * Inserts or updates a {@link DistPromotionLabelModel}.
     * 
     * @param distPromotionLabelModel
     *            the {@link DistPromotionLabelModel}to insert or update
     */
    void insertOrUpdateDistrelecPromotionLabel(final DistPromotionLabelModel distPromotionLabelModel);

    /**
     * Returns {@link DistReplacementReasonModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistReplacementReasonModel} with the specified code is found
     * @return {@link DistReplacementReasonModel}
     */
    DistReplacementReasonModel getDistrelecReplacementReason(final String code);

    /**
     * Returns all {@link DistReplacementReasonModel}.
     * 
     * @return a list of {@link DistReplacementReasonModel}, or an empty list if no code list entries can be found.
     */
    List<DistReplacementReasonModel> getAllDistrelecReplacementReason();

    /**
     * Returns {@link DistReplacementReasonModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistReplacementReasonModel} with the specified code.
     * @return {@link DistReplacementReasonModel}
     */
    DistReplacementReasonModel getOrInsertDistrelecReplacementReason(final String code);

    /**
     * Inserts or updates a {@link DistReplacementReasonModel}.
     * 
     * @param distReplacementReasonModel
     *            the {@link DistReplacementReasonModel}to insert or update
     */
    void insertOrUpdateDistrelecReplacementReason(final DistReplacementReasonModel distReplacementReasonModel);

    /**
     * Returns {@link DistRestrictionOfHazardousSubstancesModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistRestrictionOfHazardousSubstancesModel} with the specified code is found
     * @return {@link DistRestrictionOfHazardousSubstancesModel}
     */
    DistRestrictionOfHazardousSubstancesModel getDistrelecRestrictionOfHazardousSubstances(final String code);

    /**
     * Returns all {@link DistRestrictionOfHazardousSubstancesModel}.
     * 
     * @return a list of {@link DistRestrictionOfHazardousSubstancesModel}, or an empty list if no code list entries can be found.
     */
    List<DistRestrictionOfHazardousSubstancesModel> getAllDistrelecRestrictionOfHazardousSubstances();

    /**
     * Returns {@link DistRestrictionOfHazardousSubstancesModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistRestrictionOfHazardousSubstancesModel} with the specified code.
     * @return {@link DistRestrictionOfHazardousSubstancesModel}
     */
    DistRestrictionOfHazardousSubstancesModel getOrInsertDistrelecRestrictionOfHazardousSubstances(final String code);

    /**
     * Inserts or updates a {@link DistRestrictionOfHazardousSubstancesModel}.
     * 
     * @param distRestrictionOfHazardousSubstancesModel
     *            the {@link DistRestrictionOfHazardousSubstancesModel}to insert or update
     */
    void insertOrUpdateDistrelecRestrictionOfHazardousSubstances(final DistRestrictionOfHazardousSubstancesModel distRestrictionOfHazardousSubstancesModel);

    /**
     * Returns {@link DistSalesOrgModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistSalesOrgModel} with the specified code is found
     * @return {@link DistRestrictionOfHazardousSubstancesModel}
     */
    DistSalesOrgModel getDistrelecSalesOrg(final String code);

    /**
     * Returns all {@link DistSalesOrgModel}.
     * 
     * @return a list of {@link DistSalesOrgModel}, or an empty list if no code list entries can be found.
     */
    List<DistSalesOrgModel> getAllDistrelecSalesOrg();

    /**
     * Returns {@link DistSalesOrgModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistSalesOrgModel} with the specified code.
     * @return {@link DistSalesOrgModel}
     */
    DistSalesOrgModel getOrInsertDistrelecSalesOrg(final String code);

    /**
     * Inserts or updates a {@link DistSalesOrgModel}.
     * 
     * @param distSalesOrgModel
     *            the {@link DistSalesOrgModel}to insert or update
     */
    void insertOrUpdateDistrelecSalesOrg(final DistSalesOrgModel distSalesOrgModel);

    /**
     * Returns {@link DistSalesUnitModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistSalesUnitModel} with the specified code is found
     * @return {@link DistRestrictionOfHazardousSubstancesModel}
     */
    DistSalesUnitModel getDistrelecSalesUnit(final String code);

    /**
     * Returns all {@link DistSalesUnitModel}.
     * 
     * @return a list of {@link DistSalesUnitModel}, or an empty list if no code list entries can be found.
     */
    List<DistSalesUnitModel> getAllDistrelecSalesUnit();

    /**
     * Returns {@link DistSalesUnitModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistSalesUnitModel} with the specified code.
     * @return {@link DistSalesUnitModel}
     */
    DistSalesUnitModel getOrInsertDistrelecSalesUnit(final String code);

    /**
     * Inserts or updates a {@link DistSalesUnitModel}.
     * 
     * @param distSalesUnitModel
     *            the {@link DistSalesUnitModel}to insert or update
     */
    void insertOrUpdateDistrelecSalesUnit(final DistSalesUnitModel distSalesUnitModel);

    /**
     * Returns {@link DistTransportGroupModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistTransportGroupModel} with the specified code is found
     * @return {@link DistTransportGroupModel}
     */
    DistTransportGroupModel getDistrelecTransportGroup(final String code);

    /**
     * Returns all {@link DistTransportGroupModel}.
     * 
     * @return a list of {@link DistTransportGroupModel}, or an empty list if no code list entries can be found.
     */
    List<DistTransportGroupModel> getAllDistrelecTransportGroup();

    /**
     * Returns {@link DistTransportGroupModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistTransportGroupModel} with the specified code.
     * @return {@link DistTransportGroupModel}
     */
    DistTransportGroupModel getOrInsertDistrelecTransportGroup(final String code);

    /**
     * Inserts or updates a {@link DistTransportGroupModel}.
     * 
     * @param distTransportGroupModel
     *            the {@link DistTransportGroupModel}to insert or update
     */
    void insertOrUpdateDistrelecTransportGroup(final DistTransportGroupModel distTransportGroupModel);

    /**
     * Returns {@link DistPaymentMethodModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistPaymentMethodModel} with the specified code is found
     * @return {@link DistPaymentMethodModel}
     */
    DistPaymentMethodModel getDistrelecPaymentMethod(final String code);

    /**
     * Returns all {@link DistPaymentMethodModel}.
     * 
     * @return a list of {@link DistPaymentMethodModel}, or an empty list if no code list entries can be found.
     */
    List<DistPaymentMethodModel> getAllDistrelecPaymentMethod();

    /**
     * Returns {@link DistPaymentMethodModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistPaymentMethodModel} with the specified code.
     * @return {@link DistPaymentMethodModel}
     */
    DistPaymentMethodModel getOrInsertDistPaymentMethod(final String code);

    /**
     * Inserts or updates a {@link DistPaymentMethodModel}.
     * 
     * @param distPaymentMethodModel
     *            the {@link DistPaymentMethodModel}to insert or update
     */
    void insertOrUpdateDistPaymentMethod(final DistPaymentMethodModel distPaymentMethodModel);

    /**
     * Returns {@link DistShippingMethodModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistShippingMethodModel} with the specified code is found
     * @return {@link DistShippingMethodModel}
     */
    DistShippingMethodModel getDistrelecShippingMethod(final String code);

    /**
     * Returns all {@link DistShippingMethodModel}.
     * 
     * @return a list of {@link DistShippingMethodModel}, or an empty list if no code list entries can be found.
     */
    List<DistShippingMethodModel> getAllDistrelecShippingMethod();

    /**
     * Returns {@link DistShippingMethodModel} for the code. If the code doesn't exists a skeleton will be inserted.
     * 
     * @param code
     *            code
     * @throws InsertException
     *             if an exception happens due to create a {@link DistShippingMethodModel} with the specified code.
     * @return {@link DistShippingMethodModel}
     */
    DistShippingMethodModel getOrInsertDistShippingMethod(final String code);

    /**
     * Inserts or updates a {@link DistShippingMethodModel}.
     * 
     * @param distShippingMethodModel
     *            the {@link DistShippingMethodModel}to insert or update
     */
    void insertOrUpdateDistShippingtMethod(final DistShippingMethodModel distShippingMethodModel);

    /**
     * Returns {@link DistRMAReasonModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistRMAReasonModel} with the specified code is found
     * @return {@link DistRMAReasonModel}
     */
    DistRMAReasonModel getDistRMAReason(final String code);

    /**
     * Returns all {@link DistRMAReasonModel}.
     * 
     * @return a list of {@link DistRMAReasonModel}, or an empty list if no code list entries can be found.
     */
    List<DistRMAReasonModel> getAllDistRMAReasons();

    /**
     * Returns {@link DistDepartmentModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistDepartmentModel} with the specified code is found
     * @return {@link DistDepartmentModel}
     */
    DistDepartmentModel getDistDepartment(final String code);

    /**
     * Returns all {@link DistDepartmentModel}.
     * 
     * @return a list of {@link DistDepartmentModel}, or an empty list if no code list entries can be found.
     */
    List<DistDepartmentModel> getAllDistDepartments();

    /**
     * Return all {@code DistDepartmentModel} belonging to the specified {@code DistErpSystem}
     * 
     * @param erpSystem
     *            the ERP System
     * @return a list of {@code DistDepartmentModel} belonging to the specified {@code DistErpSystem}
     */
    List<DistDepartmentModel> getDistDepartmentsByErpSystem(final DistErpSystem erpSystem);

    /**
     * Inserts or updates a {@link DistDepartmentModel}.
     * 
     * @param distDepartmentModel
     *            the {@link DistDepartmentModel} to insert or update
     */
    void insertOrUpdateDistDepartment(final DistDepartmentModel distDepartmentModel);

    /**
     * Returns {@link DistFunctionModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistFunctionModel} with the specified code is found
     * @return {@link DistFunctionModel}
     */
    DistFunctionModel getDistFunction(final String code);

    /**
     * Returns all {@link DistFunctionModel}.
     * 
     * @return a list of {@link DistFunctionModel}, or an empty list if no code list entries can be found.
     */
    List<DistFunctionModel> getAllDistFunctions();

    /**
     * Returns all {@link DistFunctionModel}.
     * 
     * @return a list of {@link DistFunctionModel}, or an empty list if no code list entries can be found.
     */
    List<DistFunctionModel> getDistFunctionsByErpSystem(final DistErpSystem erpSystem);

    /**
     * Inserts or updates a {@link DistFunctionModel}.
     * 
     * @param distFunctionModel
     *            the {@link DistFunctionModel} to insert or update
     */
    void insertOrUpdateDistFunction(final DistFunctionModel distFunctionModel);

    /**
     * Returns {@link DistOrderStatusModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistOrderStatusModel} with the specified code is found
     * @return {@link DistOrderStatusModel}
     */
    DistOrderStatusModel getDistOrderStatus(final String code);

    /**
     * Returns {@link DistOrderStatusModel} for the hybris order status code.
     * 
     * @param hybrisOrderStatusCode
     *            hybris order status code
     * @throws NotFoundException
     *             if no unique {@link DistOrderStatusModel} with the specified code is found
     * @return {@link DistOrderStatusModel}
     */
    DistOrderStatusModel getDistOrderStatusForHybrisOrderStatusCode(final String hybrisOrderStatusCode);

    /**
     * Returns all {@link DistOrderStatusModel}.
     * 
     * @return a list of {@link DistOrderStatusModel}, or an empty list if no code list entries can be found.
     */
    List<DistOrderStatusModel> getAllDistOrderStatus();

    /**
     * Returns {@link DistPimCategoryTypeModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistPimCategoryTypeModel} with the specified code is found
     * @return {@link DistPimCategoryTypeModel}
     */
    DistPimCategoryTypeModel getDistPimCategoryType(final String code);

    /**
     * Returns all {@link DistPimCategoryTypeModel}.
     * 
     * @return a list of {@link DistPimCategoryTypeModel}, or an empty list if no code list entries can be found.
     */
    List<DistPimCategoryTypeModel> getAllDistPimCategoryTypes();

    /**
     * Returns {@link DistOrderChannelModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistOrderChannelModel} with the specified code is found
     * @return {@link DistOrderChannelModel}
     */
    DistOrderChannelModel getDistOrderChannel(final String code);

    /**
     * Returns all {@link DistOrderChannelModel}.
     * 
     * @return a list of {@link DistOrderChannelModel}, or an empty list if no code list entries can be found.
     */
    List<DistOrderChannelModel> getAllDistOrderChannels();

    /**
     * Returns {@link DistQuotationStatusModel} for the code.
     * 
     * @param code
     *            code
     * @throws NotFoundException
     *             if no unique {@link DistQuotationStatusModel} with the specified code is found
     * @return {@link DistQuotationStatusModel}
     */
    DistQuotationStatusModel getDistQuotationStatus(final String code);

    /**
     * Returns all {@link DistQuotationStatusModel}.
     * 
     * @return a list of {@link DistQuotationStatusModel}, or an empty list if no code list entries can be found.
     */
    List<DistQuotationStatusModel> getAllDistQuotationStatus();

	List<DistHazardStatementModel> getAllDistHazardStatement();

	List<DistSupplementalHazardInfoModel> getAllDistSupplementalHazardInfo();

}
