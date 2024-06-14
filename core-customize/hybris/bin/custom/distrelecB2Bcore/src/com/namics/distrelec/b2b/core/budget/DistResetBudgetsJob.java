/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.budget;

import java.util.Calendar;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.StandardDateRange;

/**
 * Resets all budgets to the budget of the year before.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistResetBudgetsJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistResetBudgetsJob.class);

    @Autowired
    private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2bBudgetService;

    @Override
    public PerformResult perform(final CronJobModel conJob) {
        final Set<B2BBudgetModel> budgets = getB2bBudgetService().getB2BBudgets();
        if (!budgets.isEmpty()) {
            LOG.info(budgets.size() + " bugets found for reset.");
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            for (final B2BBudgetModel budget : budgets) {
                if (budget instanceof DistB2BBudgetModel) {
                    final DistB2BBudgetModel distBudget = (DistB2BBudgetModel) budget;
                    // check if the buget has been already reset. The budget has not been reset if the end date of the budget is in the old
                    // year.
                    if (distBudget.getDateRange().getEnd().before(calendar.getTime())) {
                        LOG.info("Reset budget with code " + budget.getCode());

                        final Calendar startDate = Calendar.getInstance();
                        startDate.set(Calendar.DATE, 1);
                        startDate.set(Calendar.MONTH, Calendar.JANUARY);
                        startDate.set(Calendar.HOUR_OF_DAY, 0);
                        startDate.set(Calendar.MINUTE, 0);
                        startDate.set(Calendar.SECOND, 0);
                        startDate.set(Calendar.MILLISECOND, 0);

                        final Calendar endDate = Calendar.getInstance();
                        endDate.set(Calendar.DATE, 31);
                        endDate.set(Calendar.MONTH, Calendar.DECEMBER);
                        endDate.set(Calendar.HOUR_OF_DAY, 23);
                        endDate.set(Calendar.MINUTE, 59);
                        endDate.set(Calendar.SECOND, 59);
                        endDate.set(Calendar.MILLISECOND, 999);

                        final StandardDateRange newDateRange = new StandardDateRange(startDate.getTime(), endDate.getTime());
                        distBudget.setDateRange(newDateRange);
                        distBudget.setYearlyBudget(distBudget.getOriginalYearlyBudget());
                        modelService.save(distBudget);
                    }
                } else {
                    LOG.error("Budget with code " + budget.getCode() + " is not a DistB2BBudget. Can not reset budget!");
                    return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
                }
            }
        } else {
            LOG.info("No budgets found for reset.");
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public B2BBudgetService<B2BBudgetModel, B2BCustomerModel> getB2bBudgetService() {
        return b2bBudgetService;
    }

    public void setB2bBudgetService(final B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2bBudgetService) {
        this.b2bBudgetService = b2bBudgetService;
    }

}
