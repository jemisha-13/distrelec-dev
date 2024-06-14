/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * {@code CheckoutRicevutaBancariaForm}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class CheckoutRicevutaBancariaForm {

    private String bankCollectionCAB;
    private String bankCollectionABI;
    private String bankCollectionInstitute;

    @NotBlank(message = "{ricevuta.bancaria.invalid}")
    @Pattern(regexp = "[0-9]{5}", message = "{ricevuta.bancaria.format}")
    public String getBankCollectionCAB() {
        return bankCollectionCAB;
    }

    public void setBankCollectionCAB(final String bankCollectionCAB) {
        this.bankCollectionCAB = bankCollectionCAB;
    }

    @NotBlank(message = "{ricevuta.bancaria.invalid}")
    @Pattern(regexp = "[0-9]{5}", message = "{ricevuta.bancaria.format}")
    public String getBankCollectionABI() {
        return bankCollectionABI;
    }

    public void setBankCollectionABI(final String bankCollectionABI) {
        this.bankCollectionABI = bankCollectionABI;
    }

    @NotBlank(message = "{ricevuta.bancaria.invalid}")
    public String getBankCollectionInstitute() {
        return bankCollectionInstitute;
    }

    public void setBankCollectionInstitute(final String bankCollectionInstitute) {
        this.bankCollectionInstitute = bankCollectionInstitute;
    }
}
