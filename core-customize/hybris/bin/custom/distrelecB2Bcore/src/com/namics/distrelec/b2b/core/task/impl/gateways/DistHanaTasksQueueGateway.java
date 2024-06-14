package com.namics.distrelec.b2b.core.task.impl.gateways;

import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.impl.gateways.HanaTasksQueueGateway;
import de.hybris.platform.util.MessageFormatUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

public class DistHanaTasksQueueGateway extends HanaTasksQueueGateway {

    private final SchedulerRoleQueries schedulerRoleQueries;

    public DistHanaTasksQueueGateway(JdbcTemplate jdbcTemplate, TypeService typeService,
            SchedulerRoleQueries schedulerRoleQueries) {
        super(jdbcTemplate, typeService);
        this.schedulerRoleQueries = schedulerRoleQueries;
    }

    @Override
    public long addTasks(String tasksQuery, String expiredTasksQuery, Instant now, int rangeStart, int rangeEnd) {
        String random = this.getRangeSQLExpression(rangeStart, rangeEnd);
        Object[] args = new Object[]{now.toEpochMilli()};
        String insertTasksQuery = getInsertTaskRowStatement(MessageFormatUtils.format(tasksQuery, new Object[]{random}));
        String insertExpiredTasksQuery = getInsertTaskRowStatement(MessageFormatUtils.format(expiredTasksQuery, new Object[]{random}));
        return schedulerRoleQueries.addTasks(insertTasksQuery, insertExpiredTasksQuery, args);
    }
}
