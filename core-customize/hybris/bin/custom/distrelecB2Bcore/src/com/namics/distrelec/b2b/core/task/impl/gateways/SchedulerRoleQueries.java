package com.namics.distrelec.b2b.core.task.impl.gateways;

import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

public interface SchedulerRoleQueries {

    boolean updateSchedulerRow(Instant now, Instant oldTimestamp);

    long addTasks(String insertTasksQuery, String insertExpiredTasksQuery, Object[] args);
}
