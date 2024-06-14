package com.namics.distrelec.b2b.core.feedback.dao;

import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;

import java.util.Date;
import java.util.List;

public interface DistFeedbackDao {

    void createFeedback(DistFeedbackModel feedback);

    List<DistFeedbackModel> findFeedbacks(Date startDate, Date endDate);
}
