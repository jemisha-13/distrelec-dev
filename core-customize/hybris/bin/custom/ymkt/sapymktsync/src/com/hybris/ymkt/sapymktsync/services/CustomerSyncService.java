/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.sapymktsync.services;

import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.common.user.UserContextService;

import static com.hybris.ymkt.sapymktsync.util.UserUtil.getUserOriginalUid;


public class CustomerSyncService extends AbstractImportHeaderSyncService<CustomerModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSyncService.class);

	protected Map<String, Object> convertAddressModelToMap(AddressModel address)
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="City" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="City"/>
		this.optionalPut(map, "City", address, AddressModel::getTown);

		//<Property Name="CountryDescription" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Country"/>
		this.optionalPut(map, "CountryDescription", address, AddressModel::getCountry, CountryModel::getIsocode);

		//<Property Name="DateOfBirth" Type="Edm.DateTime" Precision="0" sap:display-format="Date" sap:unicode="false" sap:label="Date of Birth"/>
		this.optionalPut(map, "DateOfBirth", address, AddressModel::getDateOfBirth);

		//<Property Name="FirstName" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="First name"/>
		this.optionalPut(map, "FirstName", address, AddressModel::getFirstname);

		//<Property Name="LastName" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Last name"/>
		this.optionalPut(map, "LastName", address, AddressModel::getLastname);

		//<Property Name="GenderDescription" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Gender"/>
		this.optionalPut(map, "GenderDescription", address, AddressModel::getGender, Gender::getCode);

		//<Property Name="HouseNumber" Type="Edm.String" MaxLength="10" sap:unicode="false" sap:label="House Number"/>
		this.optionalPut(map, "HouseNumber", address, AddressModel::getStreetnumber);

		// yMKT seems to validate phone number according to https://en.wikipedia.org/wiki/E.123

		//<Property Name="MobilePhoneNumber" Type="Edm.String" MaxLength="30" sap:unicode="false" sap:label="Telephone no."/>
		this.optionalPut(map, "MobilePhoneNumber", address, AddressModel::getCellphone);

		//<Property Name="PhoneNumber" Type="Edm.String" MaxLength="30" sap:unicode="false" sap:label="Telephone no."/>
		this.optionalPut(map, "PhoneNumber", address, AddressModel::getPhone1);

		//<Property Name="RegionDescription" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Region"/>
		this.optionalPut(map, "RegionDescription", address, AddressModel::getRegion, RegionModel::getIsocodeShort);

		//<Property Name="Street" Type="Edm.String" MaxLength="60" sap:unicode="false" sap:label="Street"/>
		this.optionalPut(map, "Street", address, AddressModel::getStreetname);

		//<Property Name="TitleDescription" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Title"/>
		this.optionalPut(map, "TitleDescription", address, AddressModel::getTitle, TitleModel::getCode);

		return map;
	}

	protected Optional<AddressModel> selectAddressForCustomer(CustomerModel customer)
	{
		AddressModel defaultPaymentAddress = customer.getDefaultPaymentAddress();
		if (defaultPaymentAddress != null)
		{
			return Optional.of(defaultPaymentAddress);
		}

		return Optional.ofNullable(customer.getAddresses()) //
				.flatMap(addresses -> addresses.stream().min(Comparator.comparing(ItemModel::getPk)));
	}

	@Override
	protected Map<String, Object> convertModelToMap(CustomerModel customer, Map<String, Object> parameters)
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="255" sap:unicode="false" sap:label="Text"/>
		map.put("Id", getUserOriginalUid(customer));

		//<Property Name="IdOrigin" Type="Edm.String" Nullable="false" MaxLength="20" sap:unicode="false" sap:label="ID Origin"/>
		map.put("IdOrigin", UserContextService.getOriginIdSapHybrisConsumer());

		//<Property Name="EMailAddress" Type="Edm.String" MaxLength="241" sap:unicode="false" sap:label="E-Mail Address"/>
		this.optionalPut(map, "EMailAddress", customer, CustomerModel::getContactEmail);

		//<Property Name="Timestamp" Type="Edm.DateTime" Nullable="false" Precision="7" sap:unicode="false" sap:label="Time Stamp"/>
		map.put("Timestamp", new Date()); // if address is modified or non-default address is deleted, customer isn't updated

		//<Property Name="FullName" Type="Edm.String" MaxLength="80" sap:unicode="false" sap:label="Name"/>
		this.optionalPut(map, "FullName", customer, CustomerModel::getName);

		//<Property Name="IsConsumer" Type="Edm.Boolean" sap:unicode="false" sap:label="TRUE"/>
		map.put("IsConsumer", Boolean.TRUE);

		this.selectAddressForCustomer(customer) //
				.map(this::convertAddressModelToMap) //
				.ifPresent(map::putAll);

		return map;
	}

	@Override
	protected String getImportHeaderNavigationProperty()
	{
		return "Contacts";
	}
}
