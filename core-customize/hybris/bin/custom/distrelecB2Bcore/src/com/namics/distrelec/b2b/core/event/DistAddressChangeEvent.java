/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

public class DistAddressChangeEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private String customerNumber;
    private String comment;
    private Address oldAddress;
    private Address newAddress;

    public DistAddressChangeEvent(final String customerNumber, final String comment) {
        super();
        this.customerNumber = customerNumber;
        this.comment = comment;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Address getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(final Address oldAddress) {
        this.oldAddress = oldAddress;
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(final Address newAddress) {
        this.newAddress = newAddress;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public class Address {
        private String companyName;
        private String firstName;
        private String lastName;
        private String department;
        private String street;
        private String number;
        private String pobox;
        private String zip;
        private String place;
        private String country;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(final String companyName) {
            this.companyName = companyName;
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

        public String getDepartment() {
            return department;
        }

        public void setDepartment(final String department) {
            this.department = department;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(final String street) {
            this.street = street;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(final String number) {
            this.number = number;
        }

        public String getPobox() {
            return pobox;
        }

        public void setPobox(final String pobox) {
            this.pobox = pobox;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(final String zip) {
            this.zip = zip;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(final String place) {
            this.place = place;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(final String country) {
            this.country = country;
        }
    }

}
