package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.Pattern;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;

@AtLeastOneNotBlank(message = "{vat.reg.error}", value = { "vat4", "legalEmail" })
public class CodiceVatForm {

    private String vat4;

    private String legalEmail;

    private String codiceCIG;

    private String codiceCUP;

    public String getVat4() {
        return vat4;
    }

    public void setVat4(String vat4) {
        this.vat4 = vat4;
    }

    public String getLegalEmail() {
        return legalEmail;
    }

    public void setLegalEmail(String legalEmail) {
        this.legalEmail = legalEmail;
    }

    @Pattern(regexp = "\\s*|.{10}", message = "{vat.reg.codice.cig.error}")
    public String getCodiceCIG() {
        return codiceCIG;
    }

    public void setCodiceCIG(String codiceCIG) {
        this.codiceCIG = codiceCIG;
    }

    @Pattern(regexp = "\\s*|.{15}", message = "{vat.reg.codice.error}")
    public String getCodiceCUP() {
        return codiceCUP;
    }

    public void setCodiceCUP(String codiceCUP) {
        this.codiceCUP = codiceCUP;
    }
}
