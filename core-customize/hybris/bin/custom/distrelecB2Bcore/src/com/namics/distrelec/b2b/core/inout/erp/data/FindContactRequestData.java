/**
 * @author Francesco Bersani, Distrelec AG
 * @since Distrelec 3.0.m4
 *
 */

package com.namics.distrelec.b2b.core.inout.erp.data;

import org.apache.commons.lang.builder.ToStringBuilder;

public class FindContactRequestData {
    private String salesOrganization;
    private String erpCustomerId;
    private String vatNumber;
    private String organizationalNumber;
    private String firstName;
    private String lastName;
    private String email;

    public String getSalesOrganization() {
        return salesOrganization;
    }

    public void setSalesOrganization(final String salesOrganization) {
        this.salesOrganization = salesOrganization;
    }

    public String getErpCustomerId() {
        return erpCustomerId;
    }

    public void setErpCustomerId(final String erpCustomerId) {
        this.erpCustomerId = erpCustomerId;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(final String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getOrganizationalNumber() {
        return organizationalNumber;
    }

    public void setOrganizationalNumber(final String organizationalNumber) {
        this.organizationalNumber = organizationalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
