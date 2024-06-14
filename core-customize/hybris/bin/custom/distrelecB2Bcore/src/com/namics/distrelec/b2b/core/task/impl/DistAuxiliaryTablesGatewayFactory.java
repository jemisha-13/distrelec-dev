package com.namics.distrelec.b2b.core.task.impl;

import com.namics.distrelec.b2b.core.task.impl.gateways.DistHanaSchedulerStateGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.DistHanaTasksQueueGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.DistMsSqlSchedulerStateGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.DistMsSqlTasksQueueGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.DistOracleSchedulerStateGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.DistOracleTasksQueueGateway;
import com.namics.distrelec.b2b.core.task.impl.gateways.SchedulerRoleQueries;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.impl.AuxiliaryTablesGatewayFactory;
import de.hybris.platform.task.impl.gateways.SchedulerStateGateway;
import de.hybris.platform.task.impl.gateways.TasksQueueGateway;
import de.hybris.platform.util.Config;
import org.springframework.jdbc.core.JdbcTemplate;

public class DistAuxiliaryTablesGatewayFactory extends AuxiliaryTablesGatewayFactory {

    private JdbcTemplate jdbcTemplate;
    private TypeService typeService;
    private SchedulerRoleQueries schedulerRoleQueries;

    @Override
    public TasksQueueGateway getTasksQueueGateway() {
        switch (Config.getDatabaseName()) {
            case HANA:
                return new DistHanaTasksQueueGateway(jdbcTemplate, typeService, schedulerRoleQueries);
            case ORACLE:
                return new DistOracleTasksQueueGateway(jdbcTemplate, typeService, schedulerRoleQueries);
            case SQLSERVER:
                return new DistMsSqlTasksQueueGateway(jdbcTemplate, typeService, schedulerRoleQueries);
        }
        return super.getTasksQueueGateway();
    }

    @Override
    public SchedulerStateGateway getSchedulerStateGateway() {
        switch (Config.getDatabaseName()) {
            case HANA:
                return new DistHanaSchedulerStateGateway(jdbcTemplate, schedulerRoleQueries);
            case ORACLE:
                return new DistOracleSchedulerStateGateway(jdbcTemplate, schedulerRoleQueries);
            case SQLSERVER:
                return new DistMsSqlSchedulerStateGateway(jdbcTemplate, schedulerRoleQueries);
        }
        return super.getSchedulerStateGateway();
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        super.setJdbcTemplate(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setTypeService(TypeService typeService) {
        super.setTypeService(typeService);
        this.typeService = typeService;
    }

    public void setSchedulerRoleQueries(SchedulerRoleQueries schedulerRoleQueries) {
        this.schedulerRoleQueries = schedulerRoleQueries;
    }
}
