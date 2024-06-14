package com.namics.distrelec.b2b.core.task.impl.gateways;

import de.hybris.platform.task.impl.gateways.OracleSchedulerStateGateway;
import de.hybris.platform.task.impl.gateways.SchedulerState;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class DistOracleSchedulerStateGateway extends OracleSchedulerStateGateway {

    private final SchedulerRoleQueries schedulerRoleQueries;

    public DistOracleSchedulerStateGateway(JdbcTemplate jdbcTemplate, SchedulerRoleQueries schedulerRoleQueries) {
        super(jdbcTemplate);
        this.schedulerRoleQueries = schedulerRoleQueries;
    }

    @Override
    public boolean updateSchedulerRow(Instant now, Instant oldTimestamp) {
        return schedulerRoleQueries.updateSchedulerRow(now, oldTimestamp);
    }

    @Override
    public boolean updateSchedulerRow(Duration duration) {
        Optional<SchedulerState> result = getSchedulerTimestamp();
        if(result.isPresent()){
            Instant schedulerTimestamp = result.get().getLastActiveTs();
            Instant dbNow = result.get().getDbNow();
            return this.updateSchedulerRow(dbNow, schedulerTimestamp);
        }
        return super.updateSchedulerRow(duration);
    }
}
