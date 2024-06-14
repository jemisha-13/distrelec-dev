/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.management.OperationsException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.swagger.ApiBaseSiteIdAndUserIdAndAddressParams;
import com.namics.distrelec.occ.core.user.data.AddressDataList;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.helper.AddressHelper;
import com.namics.distrelec.occ.core.validation.data.AddressValidationData;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/addresses")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Address")
public class AddressController extends BaseCommerceController {
    public static final String ADDRESS_DOES_NOT_EXIST = "Address with given id: '%s' doesn't exist or belong to another user";

    private static final Logger LOG = LoggerFactory.getLogger(AddressController.class);

    private static final String ADDRESS_MAPPING = "firstName,lastName,titleCode,email,phone,cellphone,line1,line2,town,postalCode,region(DEFAULT),district,country(isocode),defaultAddress,billingAddress,shippingAddress";

    private static final String OBJECT_NAME_ADDRESS = "address";

    public static final String OBJECT_NAME_ADDRESS_ID = "addressId";

    private static final String OBJECT_NAME_ADDRESS_DATA = "addressData";

    @Resource(name = "addressVerificationFacade")
    private AddressVerificationFacade addressVerificationFacade;

    @Resource(name = "addressDataErrorsPopulator")
    private Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> addressDataErrorsPopulator;

    @Resource(name = "httpRequestAddressDataPopulator")
    private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;

    @Resource(name = "validationErrorConverter")
    private Converter<Object, List<ErrorWsDTO>> validationErrorConverter;

    @Resource(name = "b2bBillingAddressValidator")
    private Validator b2bBillingAddressValidator;

    @Resource(name = "b2bShippingAddressValidator")
    private Validator b2bShippingAddressValidator;

    @Resource(name = "b2cUserAddressValidator")
    private Validator b2cUserAddressValidator;

    @Resource(name = "b2eUserAddressValidator")
    private Validator b2eUserAddressValidator;

    @Resource(name = "addressValidator")
    private Validator addressValidator;

    @Resource(name = "addressDTOValidator")
    private Validator addressDTOValidator;

    @Autowired
    private AddressHelper addressHelper;

    @ReadOnly
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(method = RequestMethod.GET)
    @Operation(operationId = "getAddresses", summary = "Get customer's addresses", description = "Returns customer's addresses.")
    @ApiBaseSiteIdAndUserIdParam
    @ApiResponse(responseCode = "200", description = "List of customer's addresses")
    public AddressListWsDTO getAddresses(@RequestParam final AddressTypeWsDTO type,
                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<AddressData> addressList = getUserFacade().getAddressBook();
        final List<AddressData> filteredAddressList = new ArrayList<>();
        getListFiltered(type, addressList, filteredAddressList);
        final List<AddressWsDTO> distAddressWsDTOList = getDataMapper().mapAsList(filteredAddressList, AddressWsDTO.class, fields);
        AddressListWsDTO addressListWsDTO = new AddressListWsDTO();
        addressListWsDTO.setDistAddresses(distAddressWsDTOList);
        return addressListWsDTO;
    }

    /**
     * @deprecated since 2005. Please use {@link AddressController#createAddress(AddressWsDTO, String)} instead.
     */
    @Deprecated(since = "2005", forRemoval = true)
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(hidden = true, summary = "Creates a new address.", description = "Creates a new address.")
    @Parameter(name = "baseSiteId", description = "Base site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "userId", description = "User identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "firstName", description = "Customer's first name", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "lastName", description = "Customer's last name", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "email", description = "Customer's email", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "titleCode", description = "Customer's title code. Customer's title code. For a list of codes, see /{baseSiteId}/titles resource", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "country.isocode", description = "Country isocode. This parameter is required and have influence on how rest of parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "line1", description = "First part of address. If this parameter is required depends on country (usually it is required).", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "line2", description = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "town", description = "Town name or city isocode. If this parameter is required depends on country (usually it is required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "postalCode", description = "Postal code. Isocode for region. If this parameter is required depends on country.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "region.isocode", description = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "district", description = "District isocode. If this parameter is required depends on country (usually it is required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "cellphone", description = "Cellphone number. If this parameter is required depends on country (usually it is required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    public AddressWsDTO createAddress(final HttpServletRequest request,
                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final AddressData addressData = populateAddressDataAndCreateAddress(request);
        return getDataMapper().map(addressData, AddressWsDTO.class, fields);
    }

    private AddressData populateAddressDataAndCreateAddress(final HttpServletRequest request) {
        final AddressData addressData = new AddressData();
        httpRequestAddressDataPopulator.populate(request, addressData);

        validate(addressData, "addressData", addressValidator);

        return addressHelper.createAddress(addressData);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createAddress", summary = "Creates a new address.", description = "Creates a new address.")
    @ApiBaseSiteIdAndUserIdParam
    public AddressWsDTO createAddress(@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        CustomerType customerType = getUserService().getCurrentCustomerType();
        validate(address, OBJECT_NAME_ADDRESS,
                 Objects.requireNonNull(getAddressValidatorByCustomerType(customerType, BooleanUtils.isTrue(address.getBillingAddress()))));
        AddressData addressData = getDataMapper().map(address, AddressData.class, addressHelper.getAddressMappingByCustomerType(customerType, Boolean.FALSE));
        addressData.setShippingAddress(true);
        addressData.setVisibleInAddressBook(true);

        boolean saveInErp = customerType != CustomerType.B2E;
        addAddress(addressData, saveInErp);

        return getDataMapper().map(addressData, AddressWsDTO.class, fields);
    }

    private void addAddress(AddressData addressData, boolean saveInErp) {
        try {
            getUserFacade().addAddress(addressData, saveInErp);
        } catch (final ErpCommunicationException e) {
            LOG.error("Can not add address for the customer with Email: {} ", addressData.getEmail(), e);
            throw new ErpCommunicationException(getErrorMessage("form.global.error.erpcommunication"));
        } catch (final Exception e) {
            LOG.error("Can not add address for the customer with Email: {} ", addressData.getEmail(), e);
            throw new RuntimeException(getErrorMessage("text.account.addresses.addAddressError"));
        }
    }

    @ReadOnly
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.GET)
    @Operation(operationId = "getAddress", summary = "Get info about address", description = "Returns detailed information about address with a given id.")
    @ApiBaseSiteIdAndUserIdParam
    public AddressWsDTO getAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                                   @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getAddress: id={}", sanitize(addressId));
        final AddressData addressData = getAddressData(addressId);
        AddressWsDTO addressWsDTO = getDataMapper().map(addressData, AddressWsDTO.class, fields);
        addressWsDTO.setCanEditCompanyName(getB2bCustomerFacade().canEditCompanyName(addressData));
        return addressWsDTO;
    }

    /**
     * @deprecated since 2005. Please use {@link AddressController#replaceAddress(String, AddressWsDTO)} instead.
     */
    @Deprecated(since = "2005", forRemoval = true)
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(hidden = true, summary = "Updates the address", description = "Updates the address. Attributes not provided in the request will be defined again (set to null or default).")
    @ApiBaseSiteIdAndUserIdAndAddressParams
    public void replaceAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                               final HttpServletRequest request) {
        LOG.debug("editAddress: id={}", sanitize(addressId));
        final AddressData addressData = getAddressData(addressId);
        final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
        addressData.setFirstName(null);
        addressData.setLastName(null);
        addressData.setEmail(null);
        addressData.setCountry(null);
        addressData.setLine1(null);
        addressData.setLine2(null);
        addressData.setPostalCode(null);
        addressData.setRegion(null);
        addressData.setDistrict(null);
        addressData.setTitle(null);
        addressData.setTown(null);
        addressData.setDefaultAddress(false);
        addressData.setFormattedAddress(null);

        httpRequestAddressDataPopulator.populate(request, addressData);

        final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
        addressValidator.validate(addressData, errors);

        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
        getUserFacade().editAddress(addressData);

        if (!isAlreadyDefaultAddress && addressData.isDefaultAddress()) {
            getUserFacade().setDefaultAddress(addressData);
        }
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @B2ERestricted
    @RequestMapping(value = "/{addressId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceAddress", summary = "Updates the address", description = "Updates the address. Attributes not provided in the request will be defined again (set to null or default).")
    @ApiBaseSiteIdAndUserIdParam
    public void replaceAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                               @Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address) {
        validate(address, OBJECT_NAME_ADDRESS, addressDTOValidator);
        final AddressData addressData = getAddressData(addressId);
        final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
        addressData.setFormattedAddress(null);
        getDataMapper().map(address, addressData, ADDRESS_MAPPING, true);

        getUserFacade().editAddress(addressData);

        if (!isAlreadyDefaultAddress && addressData.isDefaultAddress()) {
            getUserFacade().setDefaultAddress(addressData);
        }
    }

    /**
     * @deprecated since 2005. Please use {@link AddressController#updateAddress(String, AddressWsDTO)} instead.
     */
    @Deprecated(since = "2005", forRemoval = true)
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.PATCH)
    @Operation(hidden = true, summary = "Updates the address", description = "Updates the address. Only attributes provided in the request body will be changed.")
    @ApiBaseSiteIdAndUserIdAndAddressParams
    @ResponseStatus(HttpStatus.OK)
    public void updateAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                              final HttpServletRequest request) {
        LOG.debug("editAddress: id={}", sanitize(addressId));
        final AddressData addressData = getAddressData(addressId);
        final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
        addressData.setFormattedAddress(null);
        final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);

        httpRequestAddressDataPopulator.populate(request, addressData);
        addressValidator.validate(addressData, errors);

        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }

        if (addressData.getId().equals(getUserFacade().getDefaultAddress().getId())) {
            addressData.setDefaultAddress(true);
            addressData.setVisibleInAddressBook(true);
        }
        if (!isAlreadyDefaultAddress && addressData.isDefaultAddress()) {
            getUserFacade().setDefaultAddress(addressData);
        }
        getUserFacade().editAddress(addressData);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{addressId}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "updateAddress", summary = "Updates the address", description = "Updates the address. Only attributes provided in the request body will be changed.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public AddressWsDTO updateAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                                      @Parameter(description = "Address object", required = true) @RequestBody final AddressWsDTO address,
                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        final AddressData addressData = getAddressData(addressId);
        final boolean isBillingAddress = addressData.isBillingAddress();
        CustomerType customerType = getUserService().getCurrentCustomerType();

        if (CustomerType.B2B_KEY_ACCOUNT.equals(customerType) && isBillingAddress) {
            throw new Exception(getErrorMessage("text.account.addresses.editAddressError"));
        }
        addressData.setFormattedAddress(null);
        validate(address, OBJECT_NAME_ADDRESS, Objects.requireNonNull(getAddressValidatorByCustomerType(customerType, isBillingAddress)));
        getDataMapper().map(address, addressData, addressHelper.getAddressMappingByCustomerType(customerType, Boolean.FALSE), false);

        try {
            boolean saveInErp = customerType != CustomerType.B2E;
            AddressData updatedData = getUserFacade().editAddress(addressData, saveInErp);
            return getDataMapper().map(updatedData, AddressWsDTO.class, fields);
        } catch (final ErpCommunicationException e) {
            LOG.error("Can not edit address for the customer with Email: {}", addressData.getEmail(), e);
            throw new ErpCommunicationException(getErrorMessage("form.global.error.erpcommunication"));
        } catch (final Exception e) {
            LOG.error("Can not edit address for the customer with Email: {}", addressData.getEmail(), e);
            throw new Exception(getErrorMessage("text.account.addresses.editAddressError"));
        }
    }

    private Validator getAddressValidatorByCustomerType(CustomerType type, boolean isBillingAddress) {
        switch (type) {
            case B2C:
                return b2cUserAddressValidator;
            case B2B:
            case B2B_KEY_ACCOUNT:
                return isBillingAddress ? b2bBillingAddressValidator : b2bShippingAddressValidator;
            case B2E:
                return b2eUserAddressValidator;
            default:
                return null;
        }
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @B2ERestricted
    @RequestMapping(value = "/{addressId}", method = RequestMethod.DELETE)
    @Operation(operationId = "removeAddress", summary = "Delete customer's address.", description = "Removes customer's address.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void removeAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId) throws Exception {
        LOG.debug("removeAddress: id={}", sanitize(addressId));
        final AddressData address = getAddressData(addressId);
        try {
            getUserFacade().removeAddress(address);
        } catch (final ErpCommunicationException e) {
            LOG.error("Can not remove address for the customer with Email: {'" + address.getEmail() + "'} and with addressId: {'" + addressId + "'}");
            throw new ErpCommunicationException(getErrorMessage("form.global.error.erpcommunication"));
        } catch (final Exception e) {
            LOG.error("Can not remove address for the customer with Email: {'" + address.getEmail() + "'} and with addressId: {'" + addressId + "'}");
            throw new Exception(getErrorMessage("account.confirmation.address.error"));
        }
    }

    private AddressData getAddressData(final String addressId) {
        final AddressData addressData = getUserFacade().getAddressForCode(addressId);
        if (addressData == null) {
            throw new RequestParameterException(String.format(ADDRESS_DOES_NOT_EXIST, sanitize(addressId)), RequestParameterException.INVALID,
                                                OBJECT_NAME_ADDRESS_ID);
        }
        return addressData;
    }

    /**
     * @deprecated since 2005. Please use {@link AddressController#validateAddress(AddressWsDTO, String)} instead.
     */
    @Deprecated(since = "2005", forRemoval = true)
    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/verification", method = RequestMethod.POST)
    @Operation(hidden = true, summary = "Verifies the address", description = "Verifies the address.")
    @Parameter(name = "baseSiteId", description = "Base site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "userId", description = "User identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "country.isocode", description = "Country isocode. This parameter is required and have influence on how rest of parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "line1", description = "First part of address. If this parameter is required depends on country (usually it is required).", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "line2", description = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "town", description = "Town name. If this parameter is required depends on country (usually it is required)", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "postalCode", description = "Postal code. Isocode for region. If this parameter is required depends on country.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    @Parameter(name = "region.isocode", description = "Isocode for region. If this parameter is required depends on country.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    public AddressValidationWsDTO validateAddress(final HttpServletRequest request,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final AddressData addressData = new AddressData();
        final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
        AddressValidationData validationData = new AddressValidationData();

        httpRequestAddressDataPopulator.populate(request, addressData);
        if (isAddressValid(addressData, errors, validationData)) {
            validationData = verifyAddresByService(addressData, errors, validationData);
        }
        return getDataMapper().map(validationData, AddressValidationWsDTO.class, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/verification", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "validateAddress", summary = "Verifies address.", description = "Verifies address.")
    @ApiBaseSiteIdAndUserIdParam
    public AddressValidationWsDTO validateAddress(@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        // validation is a bit different here
        final AddressData addressData = getDataMapper().map(address, AddressData.class, ADDRESS_MAPPING);
        final Errors errors = new BeanPropertyBindingResult(addressData, OBJECT_NAME_ADDRESS_DATA);
        AddressValidationData validationData = new AddressValidationData();

        if (isAddressValid(addressData, errors, validationData)) {
            validationData = verifyAddresByService(addressData, errors, validationData);
        }
        return getDataMapper().map(validationData, AddressValidationWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/{addressId}/setDefaultAddress", method = RequestMethod.POST)
    @Operation(operationId = "setDefaultAddress", summary = "Sets default address", description = "Set default shipping address")
    @ApiBaseSiteIdAndUserIdParam
    public AddressWsDTO setDefaultAddress(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                          @Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId) throws OperationsException {
        AddressData addressData = getAddressData(addressId);
        final boolean billingAddress = addressData.isBillingAddress();
        final boolean shippingAddress = addressData.isShippingAddress();

        if (billingAddress || shippingAddress) {
            try {
                addressData.setDefaultAddress(true);
                addressData.setDefaultBilling(billingAddress);
                addressData.setDefaultShipping(shippingAddress);
                getUserFacade().setDefaultAddress(addressData);
                return getDataMapper().map(addressData, AddressWsDTO.class, fields);
            } catch (final Exception exp) {
                throw new RequestParameterException(getErrorMessage("general.unknown.identifier"),
                                                    RequestParameterException.UNKNOWN_IDENTIFIER);
            }
        } else {
            throw new OperationsException("Field 'billingAddress' or 'shippingAddress' needs to be true!");
        }
    }

    @ReadOnly
    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @Operation(operationId = "isAddressBlocked", summary = "Check if address is blocked", description = "Check if address is blocked")
    @RequestMapping(value = "/blocked", method = RequestMethod.GET)
    @ApiBaseSiteIdAndUserIdParam
    public boolean isAddressBlocked(@Parameter(description = "Postal code", required = true) @RequestParam String postalCode,
                                    @Parameter(description = "Street", required = true) @RequestParam String streetName,
                                    @Parameter(description = "Street number") @RequestParam(required = false) String streetNumber,
                                    @Parameter(description = "City") @RequestParam String city,
                                    @Parameter(description = "Country ISO") @RequestParam String countryIso) {
        return getDistCheckoutFacade().isUserAddressBlocked(postalCode, streetName, streetNumber, city, countryIso);
    }

    /**
     * Checks if address is valid by a validators
     *
     * @return <code>true</code> - address is valid , <code>false</code> - address is invalid
     * @formparam addressData
     * @formparam errors
     * @formparam validationData
     */
    protected boolean isAddressValid(final AddressData addressData, final Errors errors, final AddressValidationData validationData) {
        addressValidator.validate(addressData, errors);

        if (errors.hasErrors()) {
            validationData.setDecision(AddressVerificationDecision.REJECT.toString());
            validationData.setErrors(createResponseErrors(errors));
            return false;
        }
        return true;
    }

    /**
     * Verifies address by commerce service
     *
     * @return object with verification errors and suggested addresses list
     * @formparam addressData
     * @formparam errors
     * @formparam validationData
     */
    protected AddressValidationData verifyAddresByService(final AddressData addressData, final Errors errors, final AddressValidationData validationData) {
        final AddressVerificationResult<AddressVerificationDecision> verificationDecision = addressVerificationFacade.verifyAddressData(addressData);
        if (verificationDecision.getErrors() != null && !verificationDecision.getErrors().isEmpty()) {
            populateErrors(errors, verificationDecision);
            validationData.setErrors(createResponseErrors(errors));
        }

        validationData.setDecision(verificationDecision.getDecision().toString());

        if (verificationDecision.getSuggestedAddresses() != null && !verificationDecision.getSuggestedAddresses().isEmpty()) {
            final AddressDataList addressDataList = new AddressDataList();
            addressDataList.setAddresses(verificationDecision.getSuggestedAddresses());
            validationData.setSuggestedAddressesList(addressDataList);
        }

        return validationData;
    }

    protected ErrorListWsDTO createResponseErrors(final Errors errors) {
        final List<ErrorWsDTO> webserviceErrorDto = new ArrayList<>();
        validationErrorConverter.convert(errors, webserviceErrorDto);
        final ErrorListWsDTO webserviceErrorList = new ErrorListWsDTO();
        webserviceErrorList.setErrors(webserviceErrorDto);
        return webserviceErrorList;
    }

    /**
     * Populates Errors object
     *
     * @param errors
     * @param addressVerificationResult
     */
    protected void populateErrors(final Errors errors, final AddressVerificationResult<AddressVerificationDecision> addressVerificationResult) {
        addressDataErrorsPopulator.populate(addressVerificationResult, errors);
    }

    private void getListFiltered(AddressTypeWsDTO type, List<AddressData> addressList, List<AddressData> filteredAddressList) {
        for (AddressData addressData : addressList) {
            if (AddressTypeWsDTO.ALL.equals(type)) {
                filteredAddressList.add(addressData);
            }
            if (AddressTypeWsDTO.BILLING.equals(type) && addressData.isBillingAddress()) {
                filteredAddressList.add(addressData);
            }
            if (AddressTypeWsDTO.SHIPPING.equals(type) && addressData.isShippingAddress()) {
                filteredAddressList.add(addressData);
            }
        }
    }

}
