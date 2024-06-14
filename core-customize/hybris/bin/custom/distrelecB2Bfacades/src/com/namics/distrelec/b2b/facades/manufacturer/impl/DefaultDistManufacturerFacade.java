/*
 * Copyright 2000-2021 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.manufacturer.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerMenuData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import com.namics.distrelec.b2b.facades.smartedit.SmarteditManufacturerData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link DistManufacturerFacade}
 *
 * @author pbueschi, Namics AG
 */
@CacheConfig(cacheManager = "manufacturerMenuCacheManager")
public class DefaultDistManufacturerFacade implements DistManufacturerFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistManufacturerFacade.class);

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    @Cacheable(cacheNames = "manufacturerMenu", key = "@cmsSiteService.getCurrentSite().getUid()")
    public Map<String, List<DistMiniManufacturerData>> getManufactures() {
        Instant start = Instant.now();
        final List<List<String>> manufacturers = distManufacturerService.getMiniManufacturerList();
        final Map<String, List<DistMiniManufacturerData>> manufacturerMap = new TreeMap<>(ManufacturerComparator.INSTANCE);

        if (CollectionUtils.isNotEmpty(manufacturers)) {
            for (final List<String> manufacturerRow : manufacturers) {
                final DistMiniManufacturerData manufacturerData = createMiniManufacturerData(manufacturerRow);
                final String key = getKey(manufacturerData);
                manufacturerMap.putIfAbsent(key, new ArrayList<>());
                manufacturerMap.get(key).add(manufacturerData);
            }
        }

        Duration duration = Duration.between(start, Instant.now());

        LOG.info("Manufacturer menu data created in {}ms", duration.toMillis());

        return manufacturerMap;
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForManufacturer(final DistManufacturerModel manufacturer) {
        return getDistManufacturerService().getAvailableCMSSitesForManufacturer(manufacturer);
    }

    @Override
    public List<SmarteditManufacturerData> searchManufacturers(String term, int page, int pageSize) {
        List<DistManufacturerModel> manufacturers = distManufacturerService.searchByCodeOrName(term, page, pageSize);
        return manufacturers.stream()
                .map(manufacturerModel -> {
                    SmarteditManufacturerData manufacturerData = new SmarteditManufacturerData();
                    manufacturerData.setCode(manufacturerModel.getCode());
                    manufacturerData.setName(manufacturerModel.getName());
                    return manufacturerData;
                }).collect(Collectors.toList());
    }

    @Override
    public SmarteditManufacturerData findManufacturerByCode(String code) {
        DistManufacturerModel manufacturerModel = distManufacturerService.getManufacturerByCode(code);

        if(manufacturerModel == null) {
            return null;
        }

        SmarteditManufacturerData manufacturerData = new SmarteditManufacturerData();
        manufacturerData.setCode(manufacturerModel.getCode());
        manufacturerData.setName(manufacturerModel.getName());
        return manufacturerData;
    }

    @Override
    public boolean isManufacturerExcluded(String manufacturerCode){
        return distManufacturerService.getManufacturerByCustomerPunchout(manufacturerCode) != null;
    }

    protected String getKey(final DistMiniManufacturerData manufacturerData) {
        final String name = manufacturerData.getName();
        final String key = StringUtils.left(name, 1);
        return StringUtils.isNumeric(key) ? "#" : StringUtils.upperCase(key);
    }

    protected DistMiniManufacturerData createMiniManufacturerData(final List<String> manufacturerRow) {
        final DistMiniManufacturerData manufacturerData = new DistMiniManufacturerData();
        final Iterator<String> iterator = manufacturerRow.iterator();
        // the order is important here. it has to be exactly as same as selected in the query.
        manufacturerData.setCode(iterator.next());
        // manufacturerData.setName(DistUtils.encodeString(iterator.next()));
        manufacturerData.setName(iterator.next());
        manufacturerData.setUrlId(iterator.next());
        manufacturerData.setNameSeo(iterator.next());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/manufacturer/");
        stringBuilder.append(manufacturerData.getNameSeo());
        stringBuilder.append("/");
        stringBuilder.append(manufacturerData.getCode());
        manufacturerData.setUrl(stringBuilder.toString());

        return manufacturerData;
    }

    protected static class ManufacturerComparator extends AbstractComparator<String> {
        protected static final ManufacturerComparator INSTANCE = new ManufacturerComparator();

        @Override
        protected int compareInstances(final String key1, final String key2) {
            if ("#".equals(key1)) {
                return 1;
            }
            return key1.compareTo(key2);
        }
    }

    @Override
    public List<DistManufacturerMenuData> getManufacturesForOCC()
    {
        Map<String, List< DistMiniManufacturerData >> manufacturerOccMap =  getManufactures();
        List<DistManufacturerMenuData> menuDataList=new ArrayList<DistManufacturerMenuData>();
        Iterator<Map.Entry<String, List<DistMiniManufacturerData>>> itr = manufacturerOccMap.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, List<DistMiniManufacturerData>> entry=itr.next();
            DistManufacturerMenuData menuData =new DistManufacturerMenuData ();
            menuData.setKey(entry.getKey());
            menuData.setManufacturerList(entry.getValue());
            menuDataList.add(menuData);
        }

        return menuDataList;
    }

    protected String getKeyforOCC(final DistMiniManufacturerData manufacturerData) {
        final String name = manufacturerData.getUrl();
        final String key = StringUtils.left(name, 1);
        return StringUtils.isNumeric(key) ? "#" : StringUtils.upperCase(key);
    }


    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

}
