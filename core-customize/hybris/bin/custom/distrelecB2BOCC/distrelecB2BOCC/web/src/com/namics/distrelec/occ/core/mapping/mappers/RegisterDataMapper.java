package com.namics.distrelec.occ.core.mapping.mappers;

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber;
import com.namics.distrelec.b2b.core.security.SanitizationService;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class RegisterDataMapper extends AbstractCustomMapper<RegisterData, UserSignUpWsDTO> {

    @Autowired
    private DistPhoneNumberFacade distPhoneNumberFacade;

    @Autowired
    private SanitizationService sanitizationService;

    @Override
    public void mapBtoA(UserSignUpWsDTO userSignUpWsDTO, RegisterData registerData, MappingContext context) {
        sanitizeInputFields(registerData);
        mapLogin(userSignUpWsDTO, registerData, context);
        mapPhoneNumber(userSignUpWsDTO, registerData, context);
    }

    private void mapPhoneNumber(UserSignUpWsDTO userSignUpWsDTO, RegisterData registerData, MappingContext context) {
        if (nonNull(userSignUpWsDTO) && isNotBlank(userSignUpWsDTO.getCountryCode()) && isNotBlank(userSignUpWsDTO.getPhoneNumber())) {
            Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(userSignUpWsDTO.getPhoneNumber(), userSignUpWsDTO.getCountryCode());
            if (nonNull(phoneNumber) && shouldMap(userSignUpWsDTO, registerData, context)) {
                PhoneNumberType phoneNumberType = distPhoneNumberFacade.getPhoneNumberType(phoneNumber);
                String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumber(phoneNumber);
                context.beginMappingField("phoneNumber", getBType(), userSignUpWsDTO, "mobileNumber", getAType(), registerData);
                if (PhoneNumberType.MOBILE.equals(phoneNumberType)) {
                    mapPhoneNumberAndMobileNumber(registerData, EMPTY, formattedPhoneNumber, context);
                } else {
                    mapPhoneNumberAndMobileNumber(registerData, formattedPhoneNumber, EMPTY, context);
                }
            }
        }

    }

    private void sanitizeInputFields(RegisterData registerData) {
        registerData.setFirstName(sanitizationService.removePeriods(registerData.getFirstName()));
        registerData.setLastName(sanitizationService.removePeriods(registerData.getLastName()));
    }

    private void mapPhoneNumberAndMobileNumber(RegisterData registerData, String phoneNumber, String mobileNumber, MappingContext context) {
        try {
            registerData.setPhoneNumber(phoneNumber);
            registerData.setMobileNumber(mobileNumber);
        } finally {
            context.endMappingField();
        }
    }

    private void mapLogin(UserSignUpWsDTO userSignUpWsDTO, RegisterData registerData, MappingContext context) {
        context.beginMappingField("uid", getBType(), userSignUpWsDTO, "login", getAType(), registerData);
        try {
            if (shouldMap(userSignUpWsDTO, registerData, context) && isNotBlank(userSignUpWsDTO.getUid())) {
                registerData.setLogin(userSignUpWsDTO.getUid());
            }
        } finally {
            context.endMappingField();
        }
    }
}
