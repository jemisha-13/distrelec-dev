package com.namics.distrelec.b2b.core.feedback.dao.impl;

import com.namics.distrelec.b2b.core.feedback.dao.DistFeedbackDao;
import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class DefaultDistFeedbackDao implements DistFeedbackDao {

    private static final String FIND_FEEDBACKS = "SELECT {" + DistFeedbackModel.PK + "} FROM {" + DistFeedbackModel._TYPECODE + "}"
                                                   + " WHERE {" + DistFeedbackModel.CREATIONTIME + "} >= ?startDate"
                                                   + " AND {" + DistFeedbackModel.CREATIONTIME + "} < ?endDate";

    @Autowired
    private ModelService modelService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void createFeedback(DistFeedbackModel feedback) {
        if (feedback != null) {
            getModelService().save(feedback);
            getModelService().refresh(feedback);
        }
    }

    @Override
    public List<DistFeedbackModel> findFeedbacks(Date startDate, Date endDate) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_FEEDBACKS);
        searchQuery.addQueryParameter("startDate", startDate);
        searchQuery.addQueryParameter("endDate", endDate);
        return getFlexibleSearchService().<DistFeedbackModel>search(searchQuery).getResult();
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
