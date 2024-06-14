package com.namics.distrelec.b2b.core.task.impl.gateways;

import de.hybris.platform.task.impl.gateways.HanaSchedulerStateGateway;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

public class DistHanaSchedulerStateGateway extends HanaSchedulerStateGateway {

    private final SchedulerRoleQueries schedulerRoleQueries;

    public DistHanaSchedulerStateGateway(JdbcTemplate jdbcTemplate, SchedulerRoleQueries schedulerRoleQueries) {
        super(jdbcTemplate);
        this.schedulerRoleQueries = schedulerRoleQueries;
    }

    @Override
    public boolean updateSchedulerRow(Instant now, Instant oldTimestamp) {
        return schedulerRoleQueries.updateSchedulerRow(now, oldTimestamp);
    }
}
