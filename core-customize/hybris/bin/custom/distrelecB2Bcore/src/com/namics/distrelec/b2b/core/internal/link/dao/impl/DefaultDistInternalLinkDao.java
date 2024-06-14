package com.namics.distrelec.b2b.core.internal.link.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.namics.distrelec.b2b.core.internal.link.dao.DistInternalLinkDao;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalLinkModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;

import de.hybris.platform.core.Registry;
import de.hybris.platform.persistence.property.JDBCValueMappings;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.tx.Transaction;

public class DefaultDistInternalLinkDao extends AbstractItemDao implements DistInternalLinkDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistInternalLinkDao.class);

    private static final String FIND_BY_CODE_AND_SITE_AND_TYPE = "SELECT {" + DistInternalLinkModel.PK + "} FROM {" + DistInternalLinkModel._TYPECODE + "}"
                                                                 + " WHERE {" + DistInternalLinkModel.CODE + "}=?" + DistInternalLinkModel.CODE
                                                                 + " AND {" + DistInternalLinkModel.SITE + "}=?" + DistInternalLinkModel.SITE
                                                                 + " AND {" + DistInternalLinkModel.TYPE + "}=?" + DistInternalLinkModel.TYPE;

    private static final String FIND_BY_CODE_AND_SITE_AND_TYPE_AND_LANGUAGE = "SELECT DISTINCT {il." + DistInternalLinkModel.PK + "} FROM {"
                                                                              + DistRelatedDataModel._TYPECODE + " AS rd JOIN "
                                                                              + DistInternalLinkModel._TYPECODE + " AS il ON {rd."
                                                                              + DistRelatedDataModel.INTERNALLINK + "}={il." + DistInternalLinkModel.PK
                                                                              + "}} WHERE {il." + DistInternalLinkModel.CODE + "}=?"
                                                                              + DistInternalLinkModel.CODE
                                                                              + " AND {il." + DistInternalLinkModel.SITE + "}=?" + DistInternalLinkModel.SITE
                                                                              + " AND {il." + DistInternalLinkModel.TYPE + "}=?" + DistInternalLinkModel.TYPE
                                                                              + " AND {rd." + DistRelatedDataModel.LANGUAGE + "}=?"
                                                                              + DistRelatedDataModel.LANGUAGE;

    private static final String CLUSTER_NODE = "cluster.id";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final JDBCValueMappings.ValueWriter<Date, ?> dateWriter = JDBCValueMappings.getJDBCValueWriter(Date.class);

    @Override
    public DistInternalLinkModel findInternalLinkByLanguage(String code, String site, String type, String language) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BY_CODE_AND_SITE_AND_TYPE_AND_LANGUAGE);
        searchQuery.addQueryParameter(DistInternalLinkModel.CODE, code);
        searchQuery.addQueryParameter(DistInternalLinkModel.SITE, site);
        searchQuery.addQueryParameter(DistInternalLinkModel.TYPE, type);
        searchQuery.addQueryParameter(DistRelatedDataModel.LANGUAGE, language);
        try {
            return getFlexibleSearchService().searchUnique(searchQuery);
        } catch (ModelNotFoundException e) {
            return null;
        } catch (AmbiguousIdentifierException e) {
            LOG.error("More than one internal link found for code '{}', site '{}', type '{}' and language '{}'", code, site, type, language);
            return null;
        }
    }

    @Override
    public DistInternalLinkModel findInternalLink(String code, String site, String type) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BY_CODE_AND_SITE_AND_TYPE);
        searchQuery.addQueryParameter(DistInternalLinkModel.CODE, code);
        searchQuery.addQueryParameter(DistInternalLinkModel.SITE, site);
        searchQuery.addQueryParameter(DistInternalLinkModel.TYPE, type);
        try {
            return getFlexibleSearchService().searchUnique(searchQuery);
        } catch (ModelNotFoundException e) {
            return null;
        } catch (AmbiguousIdentifierException e) {
            LOG.error("More than one internal link found for code '{}', site '{}', and type '{}'", code, site, type);
            return null;
        }
    }

    @Override
    public boolean createInternalLink(DistInternalLinkModel internalLink) {
        try {
            getModelService().save(internalLink);
            return true;
        } catch (Exception e) {
            LOG.warn("Unable to create internal link", e);
            return false;
        }
    }

    @Override
    public void updateInternalLink(DistInternalLinkModel storedLink, Set<DistRelatedDataModel> relatedDataList, String language) {
        relatedDataList.addAll(getStoredRelatedDataList(storedLink, language));
        storedLink.setDatas(relatedDataList);
        getModelService().save(storedLink);
    }

    private List<DistRelatedDataModel> getStoredRelatedDataList(DistInternalLinkModel storedLink, String language) {
        List<DistRelatedDataModel> storedRelatedDataList = new ArrayList<>();
        if (storedLink.getDatas() != null) {
            for (DistRelatedDataModel relatedData : storedLink.getDatas()) {
                if (language.equals(relatedData.getLanguage())) {
                    getModelService().remove(relatedData);
                } else {
                    storedRelatedDataList.add(relatedData);
                }
            }
        }
        return storedRelatedDataList;
    }

    @Override
    public boolean lockInternalLink(DistInternalLinkModel internalLink) {
        Transaction.current().begin();
        int count = 0;
        try {
            Map<String, Object> workerMap = jdbcTemplate.queryForMap("SELECT p_worker, p_workertimestamp FROM " + getTenantAwareTableName("distinternallink")
                                                                     + " WHERE pk=?",
                                                                     internalLink.getPk().getLong());

            if (!workerMap.isEmpty()) {
                BigDecimal worker = new BigDecimal(workerMap.get("p_worker").toString());
                Timestamp workerTimestamp = (Timestamp) workerMap.get("p_workertimestamp");

                if (worker.compareTo(BigDecimal.ZERO) < 0 || workerTimestamp.before(DateUtils.addMinutes(new Date(), -30))) {
                    count = jdbcTemplate.update("UPDATE " + getTenantAwareTableName("distinternallink")
                                                + " SET p_worker=?, p_workerTimestamp=? WHERE p_worker=? and pk=?", (preparedStatement) -> {
                                                    preparedStatement.setInt(1, getClusterId());
                                                    dateWriter.setValue(preparedStatement, 2, new Date());
                                                    preparedStatement.setInt(3, worker.intValue());
                                                    preparedStatement.setLong(4, internalLink.getPk().getLong());
                                                });
                }
            }
            Transaction.current().commit();
        } catch (Exception e) {
            LOG.warn("error while locking internal link", e);
            Transaction.current().rollback();
        }
        if (count > 0) {
            return true;
        } else {
            LOG.warn(String.format("unable to lock internal link: %s %s %s", internalLink.getCode(), internalLink.getSite(), internalLink.getType()));
            return false;
        }
    }

    @Override
    public boolean unlockInternalLink(DistInternalLinkModel internalLink) {
        Transaction.current().begin();

        // reset timestamp
        internalLink.setTimestamp(new Date());
        getModelService().save(internalLink);

        int count = 0;
        try {
            count = jdbcTemplate.update("UPDATE " + getTenantAwareTableName("distinternallink") + " SET p_worker=? WHERE p_worker=? and pk=?",
                                        (preparedStatement) -> {
                                            preparedStatement.setInt(1, -1);
                                            preparedStatement.setInt(2, getClusterId());
                                            preparedStatement.setLong(3, internalLink.getPk().getLong());
                                        });
            Transaction.current().commit();
        } catch (Exception e) {
            LOG.warn("error while unlocking internal link", e);
            Transaction.current().rollback();
        }
        if (count > 0) {
            return true;
        } else {
            LOG.warn(String.format("unable to unlock internal link: %s %s %s", internalLink.getCode(), internalLink.getSite(), internalLink.getType()));
            return false;
        }
    }

    private String getTenantAwareTableName(String tableName) {
        return "master".equals(Registry.getCurrentTenant().getTenantID()) ? tableName : Registry.getCurrentTenant().getTenantID() + "_" + tableName;
    }

    private int getClusterId() {
        return configurationService.getConfiguration().getInt(CLUSTER_NODE);
    }
}
