package de.hybris.platform.task.impl.gateways;

import com.namics.distrelec.b2b.core.task.impl.gateways.SchedulerRoleQueries;
import de.hybris.platform.core.Registry;
import de.hybris.platform.persistence.property.JDBCValueMappings;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.MessageFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DefaultSchedulerRoleQueries extends DefaultBaseGateway implements SchedulerRoleQueries {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSchedulerRoleQueries.class);

    private final JDBCValueMappings.ValueWriter<Date, ?> dateWriter = JDBCValueMappings.getJDBCValueWriter(Date.class);

    public DefaultSchedulerRoleQueries(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public boolean updateSchedulerRow(Instant now, Instant oldTimestamp) {
        int updatedRows = jdbcTemplate.update(getUpdateSchedulerRowStatement(), (preparedStatement) -> {
            this.dateWriter.setValue(preparedStatement, 1, Date.from(now));
            preparedStatement.setString(2, String.valueOf(Registry.getClusterID()));
            preparedStatement.setString(3, "scheduler");
            this.dateWriter.setValue(preparedStatement, 4, Date.from(oldTimestamp));
            preparedStatement.setString(5, "scheduler");
            this.dateWriter.setValue(preparedStatement, 6, Date.from(now.minus(10, ChronoUnit.SECONDS)));
        });
        return updatedRows == 1;
    }

    @Override
    public long addTasks(String insertTasksQuery, String insertExpiredTasksQuery, Object[] args) {
        Transaction.current().begin();
        long count = 0L;

        try {
            count += jdbcTemplate.update(insertTasksQuery, args);
            count += jdbcTemplate.update(insertExpiredTasksQuery, args);
            boolean releasedScheduledRoleLock = releaseSchedulerRoleLock();
            if (releasedScheduledRoleLock) {
                Transaction.current().commit();
                return count;
            } else {
                Transaction.current().rollback();
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("error while adding tasks to queue", e);
            Transaction.current().rollback();
            throw e;
        }
    }

    protected boolean releaseSchedulerRoleLock() {
        int updatedRows = jdbcTemplate.update(getReleaseSchedulerRoleLockStatement(), (preparedStatement -> {
            preparedStatement.setString(1, "scheduler");
            preparedStatement.setString(2, String.valueOf(Registry.getClusterID()));
        }));
        return updatedRows == 1;
    }

    private String getUpdateSchedulerRowStatement() {
        return MessageFormatUtils.format("UPDATE {0} SET LAST_ACTIVITY_TS= ?, ID = ? WHERE (ID = ? AND LAST_ACTIVITY_TS=?) OR (ID <> ? AND LAST_ACTIVITY_TS < ? )", getTableName());
    }

    private String getReleaseSchedulerRoleLockStatement() {
        return MessageFormatUtils.format("UPDATE {0} SET ID = ? WHERE ID = ?", getTableName());
    }

    public String getTableName() {
        return getTenantAwareTableName("tasks_aux_scheduler");
    }

    @Override
    protected String getCreateTableStatement() {
        throw new IllegalArgumentException("Must not be called");
    }
}
