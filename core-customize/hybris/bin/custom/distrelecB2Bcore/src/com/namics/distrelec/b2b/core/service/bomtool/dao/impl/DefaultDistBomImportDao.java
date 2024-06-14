package com.namics.distrelec.b2b.core.service.bomtool.dao.impl;

import com.namics.distrelec.b2b.core.model.bomtool.BomImportModel;
import com.namics.distrelec.b2b.core.service.bomtool.dao.DistBomImportDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DefaultDistBomImportDao implements DistBomImportDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistBomImportDao.class);

    private static final String FIND_BY_CUSTOMER_AND_NAME = "SELECT {pk} FROM {BomImport} WHERE {customer}=?customer AND {name}=?name";
    private static final String FIND_BY_CUSTOMER = "SELECT {pk} FROM {BomImport} WHERE {customer}=?customer ORDER BY {creationtime} DESC";
    private static final String COUNT_BY_CUSTOMER = "SELECT COUNT(*) FROM {BomImport} WHERE {customer}=?customer";
    private static final String FIND_BY_LAST_USED_DATE = "SELECT {pk} FROM {BomImport} WHERE {lastUsed}<?date AND {lastAddedToCart}<?date" ;
    private static final String FIND_FILES_FOR_DELETION = "SELECT {pk} FROM {BomImport} WHERE {lastUsed}<?date AND {lastAddedToCart}<?date";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public Optional<BomImportModel> findBomImportByCustomerAndName(CustomerModel customer, String name) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BY_CUSTOMER_AND_NAME);
        query.addQueryParameter("customer", customer);
        query.addQueryParameter("name", name);

        try {
            return Optional.of(flexibleSearchService.searchUnique(query));
        } catch (ModelNotFoundException e) {
            return Optional.empty();
        } catch (AmbiguousIdentifierException e) {
            LOG.error("More than one BOM file found for customer '{}' with name '{}'", customer.getUid(), name);
            return Optional.empty();
        }
    }

    @Override
    public List<BomImportModel> findBomImportsByCustomer(CustomerModel customer) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BY_CUSTOMER);
        query.addQueryParameter("customer", customer);
        return flexibleSearchService.<BomImportModel>search(query).getResult();
    }

    @Override
    public long getCountOfBomFilesByCustomer(CustomerModel customer) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(COUNT_BY_CUSTOMER);
        query.addQueryParameter("customer", customer);
        query.setResultClassList(Arrays.asList(Long.class));
        return flexibleSearchService.searchUnique(query);
    }

    @Override
    public List<BomImportModel> getUnusedBomFiles(int unusedTimestamp, int deleteTimestamp) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BY_LAST_USED_DATE);
        DateTime dt = new DateTime();
        query.addQueryParameter("date", dt.minusWeeks(unusedTimestamp).toDate());
        FlexibleSearchQuery deleteQuery = new FlexibleSearchQuery(FIND_FILES_FOR_DELETION);
        deleteQuery.addQueryParameter("date", dt.minusWeeks(deleteTimestamp).toDate());
        List<BomImportModel> totalList= flexibleSearchService.<BomImportModel>search(query).getResult();
        List<BomImportModel> deleteList= flexibleSearchService.<BomImportModel>search(deleteQuery).getResult();
        List<BomImportModel> unUsedList= new ArrayList<BomImportModel>(); 
        for(BomImportModel model:totalList) {
        	if(!deleteList.contains(model))
        		unUsedList.add(model);		
        }
        return unUsedList;
        }

    @Override
    public List<BomImportModel> deleteUnusedBomFiles(int deleteTimestamp) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_FILES_FOR_DELETION);
        DateTime dt = new DateTime();
        query.addQueryParameter("date", dt.minusWeeks(deleteTimestamp).toDate());
        return flexibleSearchService.<BomImportModel>search(query).getResult();
        }
}
