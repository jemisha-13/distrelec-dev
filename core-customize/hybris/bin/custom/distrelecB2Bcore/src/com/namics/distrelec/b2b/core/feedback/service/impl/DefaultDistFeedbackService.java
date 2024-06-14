package com.namics.distrelec.b2b.core.feedback.service.impl;

import com.namics.distrelec.b2b.core.feedback.dao.DistFeedbackDao;
import com.namics.distrelec.b2b.core.feedback.service.DistFeedbackService;
import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class DefaultDistFeedbackService implements DistFeedbackService {

    @Autowired
    private DistFeedbackDao distFeedbackDao;

    @Override
    public void createFeedback(DistFeedbackModel feedback) {
        getDistFeedbackDao().createFeedback(feedback);
    }

    @Override
    public List<DistFeedbackModel> findFeedbacks(Date startDate, Date endDate) {
        return getDistFeedbackDao().findFeedbacks(startDate, endDate);
    }

    public DistFeedbackDao getDistFeedbackDao() {
        return distFeedbackDao;
    }

    public void setDistFeedbackDao(final DistFeedbackDao distFeedbackDao) {
        this.distFeedbackDao = distFeedbackDao;
    }
}
