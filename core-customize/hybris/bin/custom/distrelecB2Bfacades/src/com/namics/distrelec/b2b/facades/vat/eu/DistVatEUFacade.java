package com.namics.distrelec.b2b.facades.vat.eu;

/**
 *
 * @author sayali
 *
 */
public interface DistVatEUFacade {

    /**
     *
     * @param vatNumber
     * @param countryCode
     * @return true - if VAT number is valid
     * @return false - if VAT number is invalid
     */
    boolean validateVatNumber(String vatNumber, final String countryCode);

    boolean isVatValidatedExternally(String countryCode);

    String getVatPrefixForCountry(String countryCode);

    String getVatWithoutPrefix(String vat, String country);

}
