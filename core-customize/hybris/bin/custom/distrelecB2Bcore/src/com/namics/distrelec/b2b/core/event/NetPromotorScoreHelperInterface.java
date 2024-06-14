package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

public interface NetPromotorScoreHelperInterface {

    /**
     * Populate the NPS business process from the event
     *
     * @param event
     * @param npsProcessModel
     */
    default void ipopulate(final DistNetPromoterScoreEvent event, final DistNpsProcessModel npsProcessModel) {
        // Basic data
        npsProcessModel.setSite(event.getSite());
        npsProcessModel.setStore(event.getBaseStore());
        // NPS data
        npsProcessModel.setReason(event.getReason());
        npsProcessModel.setSubReason(event.getSubreason());
        npsProcessModel.setNpsCode(event.getCode());
        npsProcessModel.setErpContactID(event.getErpContactID());
        npsProcessModel.setErpCustomerID(event.getErpCustomerID());
        npsProcessModel.setType(event.getType());
        npsProcessModel.setEmail(event.getEmail());
        npsProcessModel.setSalesOrg(event.getSalesOrg());
        npsProcessModel.setFirstname(event.getFirstname());
        npsProcessModel.setLastname(event.getLastname());
        npsProcessModel.setCompanyName(event.getCompanyName());
        npsProcessModel.setOrderNumber(event.getOrderNumber());
        npsProcessModel.setDomain(event.getDomain());
        npsProcessModel.setDeliveryDate(event.getDeliveryDate());
        npsProcessModel.setValue(event.getValue());
        npsProcessModel.setText(event.getText());
        npsProcessModel.setToEmail(event.getToEmail());
        npsProcessModel.setEmailSubjectMsg(event.getEmailSubjectMsg());
    }

    /**
     * Populate the NPS business process from the event
     *
     * @param npsData
     * @param npsProcessModel
     */
    default void iupdateNPS(final DistNetPromoterScoreData npsData, final DistNpsProcessModel npsProcessModel) {

        // npsProcessModel.setNpsCode(npsData.getCode());
        // npsProcessModel.setEmail(npsData.getEmail());

        // NPS data
        npsProcessModel.setReason(npsData.getReason());
        npsProcessModel.setSubReason(npsData.getSubreason());
        npsProcessModel.setErpContactID(npsData.getErpContactID());
        npsProcessModel.setErpCustomerID(npsData.getErpCustomerID());
        npsProcessModel.setType(npsData.getType());
        npsProcessModel.setSalesOrg(npsData.getSalesOrg());
        npsProcessModel.setFirstname(npsData.getFirstname());
        npsProcessModel.setLastname(npsData.getLastname());
        npsProcessModel.setCompanyName(npsData.getCompanyName());
        npsProcessModel.setOrderNumber(npsData.getOrderNumber());
        npsProcessModel.setDomain(npsData.getDomain());
        npsProcessModel.setDeliveryDate(npsData.getDeliveryDate());
        npsProcessModel.setValue(npsData.getValue());
        npsProcessModel.setText(npsData.getText());
    }
}
