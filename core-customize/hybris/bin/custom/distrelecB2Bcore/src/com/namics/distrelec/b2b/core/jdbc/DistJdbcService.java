package com.namics.distrelec.b2b.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface DistJdbcService {

    boolean isReadReplicaEnabled();

    Connection acquireMasterReplicaConnection() throws SQLException;

    Connection acquireReadReplicaConnection() throws SQLException;

}
