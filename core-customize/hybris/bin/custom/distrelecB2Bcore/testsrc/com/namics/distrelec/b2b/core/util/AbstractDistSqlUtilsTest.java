package com.namics.distrelec.b2b.core.util;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@UnitTest
public abstract class AbstractDistSqlUtilsTest {

    protected DistSqlUtilsImpl distSqlUtils;

    @Before
    public void setUpDistSqlUtils() {
        MockitoAnnotations.initMocks(this);

        DistSqlUtilsImpl distSqlUtils = new DistSqlUtilsImpl();
        this.distSqlUtils = spy(distSqlUtils);
    }

    protected void setMySQL() {
        setDb(true, false, false);
    }

    protected void setOracle() {
        setDb(false, true, false);
    }

    protected void setSQLServer() {
        setDb(false, false, true);
    }

    protected void setDb(boolean mysql, boolean oracle, boolean sqlserver) {
        doReturn(mysql).when(distSqlUtils).isMySQLUsed();
        doReturn(oracle).when(distSqlUtils).isOracleUsed();
        doReturn(sqlserver).when(distSqlUtils).isSQLServerUsed();
    }
}
