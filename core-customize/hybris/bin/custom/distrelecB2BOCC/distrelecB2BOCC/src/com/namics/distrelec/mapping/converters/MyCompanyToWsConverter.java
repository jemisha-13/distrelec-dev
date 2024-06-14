package com.namics.distrelec.mapping.converters;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.user.ws.dto.MyCompanyWsDTO;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import ma.glasnost.orika.MapperFactory;

public class MyCompanyToWsConverter implements DataToWsConverter<B2BUnitModel, MyCompanyWsDTO> {

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public Class getDataClass() {
        return B2BUnitModel.class;
    }

    @Override
    public Class getWsClass() {
        return MyCompanyWsDTO.class;
    }

    @Override
    public Predicate<Object> canConvert() {
        return null;
    }

    /**
     * Method to convert a source object to a target object.
     *
     * @param source
     *            the source object
     * @param fields
     *            the fields that should be populated in target object
     * @return the target object
     */
    @Override
    public MyCompanyWsDTO convert(B2BUnitModel source, String fields) {
        final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
        MyCompanyWsDTO target = new MyCompanyWsDTO();
        target.setVatId(source.getVatID());
        var erpCustomerId = source.getErpCustomerID();
        target.setCustomerId(erpCustomerId);
        final B2BUnitModel unit = currentCustomer.getDefaultB2BUnit();

        if (CollectionUtils.isNotEmpty(source.getAddresses())) {
            Optional<AddressModel> companyAddr = Optional.empty();
            final CustomerType unitType = currentCustomer.getDefaultB2BUnit().getCustomerType();
            if (CustomerType.B2B.equals(unitType) || CustomerType.B2B_KEY_ACCOUNT.equals(unitType)) {
                companyAddr = source.getAddresses().stream().filter(addr -> Boolean.TRUE.equals(addr.getBillingAddress()) && erpCustomerId != null
                        && erpCustomerId.equals(addr.getErpAddressID())).findFirst();
            }
            if (companyAddr.isPresent()) {
                target.setCompanyName(StringUtils.isNotEmpty(companyAddr.get().getCompany()) ? companyAddr.get().getCompany() : unit.getName());
                target.setCompanyName2(StringUtils.isNotEmpty(companyAddr.get().getCompanyName2()) ? companyAddr.get().getCompanyName2()
                                                                                                   : unit.getCompanyName2());
                target.setCompanyName3(StringUtils.isNotEmpty(companyAddr.get().getCompanyName3()) ? companyAddr.get().getCompanyName3()
                                                                                                   : unit.getCompanyName3());
            } else {
                // fill the details from the random address
                companyAddr = source.getAddresses().stream().findFirst();
                if (companyAddr.isPresent()) {
                    target.setCompanyName(StringUtils.isNotEmpty(companyAddr.get().getCompany()) ? companyAddr.get().getCompany() : unit.getName());
                    target.setCompanyName2(StringUtils.isNotEmpty(companyAddr.get().getCompanyName2()) ? companyAddr.get().getCompanyName2()
                                                                                                       : unit.getCompanyName2());
                    target.setCompanyName3(StringUtils.isNotEmpty(companyAddr.get().getCompanyName3()) ? companyAddr.get().getCompanyName3()
                                                                                                       : unit.getCompanyName3());
                }
            }
        }

        return target;
    }

    @Override
    public void customize(MapperFactory factory) {

    }

    protected UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected DataMapper getDataMapper() {
        return dataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }
}
