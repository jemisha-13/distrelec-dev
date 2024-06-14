package com.namics.distrelec.occ.core.mapping.mappers;

import static java.util.Objects.nonNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class CheckoutAddressDataMapper extends AbstractCustomMapper<AddressData, AddressWsDTO> {

    @Autowired
    private DistPhoneNumberFacade distPhoneNumberFacade;

    @Override
    public void mapBtoA(AddressWsDTO addressWsDTO, AddressData addressData, MappingContext context) {
        if (nonNull(addressWsDTO) && nonNull(addressWsDTO.getCountry()) && isCheckoutPhone(addressWsDTO)) {
            PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(addressWsDTO.getCheckoutPhone(),
                                                                             addressWsDTO.getCountry().getIsocode());
            if (nonNull(phoneNumber)) {
                PhoneNumberType phoneNumberType = distPhoneNumberFacade.getPhoneNumberType(phoneNumber);
                String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumber(phoneNumber);
                if (PhoneNumberType.MOBILE.equals(phoneNumberType) || PhoneNumberType.FIXED_LINE_OR_MOBILE.equals(phoneNumberType)) {
                    addressData.setCellphone(formattedPhoneNumber);
                }
                if (PhoneNumberType.FIXED_LINE.equals(phoneNumberType)) {
                    addressData.setPhone(formattedPhoneNumber);
                }
            }
        }
    }

    private boolean isCheckoutPhone(AddressWsDTO addressWsDTO) {
        // phone,cellphone,fax shouldn't be included in the mapping otherwise it will override existing data
        return StringUtils.isNotBlank(addressWsDTO.getCheckoutPhone());
    }
}
