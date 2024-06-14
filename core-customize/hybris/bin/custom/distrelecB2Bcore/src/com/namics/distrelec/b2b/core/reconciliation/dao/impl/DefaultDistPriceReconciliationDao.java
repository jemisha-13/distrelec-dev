package com.namics.distrelec.b2b.core.reconciliation.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.reconciliation.dao.DistPriceReconciliationDao;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;



public class DefaultDistPriceReconciliationDao extends AbstractItemDao implements DistPriceReconciliationDao{

	@Autowired
	private DistSqlUtils distSqlUtils;

	protected String getFetchPriceRowsQuery() {
		return "select innertable.SalesOrg, "+
				" innertable.ErpPriceConditionId, "+
				" innertable.MaterialNumber, "+
				" innertable.PriceList, "+
				" innertable.CurrencyISO as Currency, "+
				" innertable.PriceCondition, "+
				" innertable.UnitFactor, "+
				distSqlUtils.stringAgg("ISNULl(concat(concat(concat(COALESCE(innertable.minQty,0),','),COALESCE(innertable.price,0)),concat(',',COALESCE(innertable.ppx,0))),'NA')", ",", "innertable.minQty ASC")   + "AS \"S1,P1,PPX1,S2,P2,PPX2,S3,P3,PPX3,S4,P4,PPX4,S5,P5,PPX5,S6,P6,PPX6,S7,P7,PPX7,S8,P8,PPX8,S9,P9,PPX9,S10,P10,PPX10\" "+
				" FROM ({{ "+
				" SELECT "+
				" distinct {sorg.code} as SalesOrg, "+
				" {p.code} as MaterialNumber, "+
				" {ug.code} as PriceList, "+
				" {cur.isocode} as CurrencyISO, "+
				" {epct.code} as PriceCondition, "+
				" {pr2.priceConditionIdErp} AS erpPriceConditionId, "+
				" {pr2.price} AS price, "+
				" {pr2.unitFactor} AS unitFactor, "+
				" {pr2.minqtd} AS minQty, "+
				" {pr2.pricePerX} AS ppx "+
				" from { "+
				" DistPriceRow as pr "+
				" join Product as p on {pr.product}={p.pk} "+
				" join CMSSite as cms on {pr.ug}={cms.userpricegroup} "+
				" join DistSalesOrg as sorg on {sorg.pk}={cms.salesOrg} "+
				" join Currency as cur on {cur.pk}={pr.currency} "+
				" join EnumerationValue as ug on {pr.ug}={ug.pk} "+
				" join DistErpPriceConditionType as epct on {epct.pk}={pr.erpPriceConditionType} "+
				" join DistPriceRow as pr2 on {pr2.pk}={pr.pk} "+
				" } "+
				" where {sorg.code}=?salesOrg "+
				" AND {pr2.product}={pr.product} and {pr2.ug}={pr.ug} and {pr2.priceConditionIdErp}={pr.priceConditionIdErp} "+
				" AND {pr2.endTime} >= " + distSqlUtils.now() +
				" AND {pr2.startTime} <= " + distSqlUtils.now() +
				" }}) AS innertable "+
				" GROUP BY innertable.erpPriceConditionId, "+
				" innertable.MaterialNumber, "+
				" innertable.SalesOrg, "+
				" innertable.PriceList, "+
				" innertable.erpPriceConditionId, "+
				" innertable.unitFactor, "+
				" innertable.CurrencyISO, "+
				" innertable.PriceCondition ";
	}
	
	@Autowired
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;
	
    @Autowired
    private ConfigurationService configurationService;

	@Override
	public ResultSet fetchAllPriceRows(String salesOrg) {
		final Map<String, Object> flexibleSearchParameters = new HashMap<String, Object>();
		flexibleSearchParameters.put("salesOrg",salesOrg);
		final ResultSet resultSet = flexibleSearchExecutionService.execute(getFetchPriceRowsQuery(), flexibleSearchParameters);
		return resultSet;
	}


}
