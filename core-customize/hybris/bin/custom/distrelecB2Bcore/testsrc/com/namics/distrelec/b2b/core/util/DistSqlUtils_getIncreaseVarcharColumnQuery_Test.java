package com.namics.distrelec.b2b.core.util;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class DistSqlUtils_getIncreaseVarcharColumnQuery_Test extends AbstractDistSqlUtilsTest {

    final String tableName = "tableName";
    final String columnName = "columnName";
    final int newSize = 100;

    @Mock
    AttributeDescriptorModel attrDescriptor;

    @Before
    public void setUp() {
        doReturn(tableName).when(distSqlUtils).getTableName(attrDescriptor);
        doReturn(columnName).when(attrDescriptor).getDatabaseColumn();
    }

    @Test
    public void testGetIncreaseVarcharColumnQueryMySql() {
        setMySQL();

        String sql = distSqlUtils.getIncreaseVarcharColumnQuery(attrDescriptor, newSize);

        assertEquals("alter table tableName modify columnName varchar(100)", sql);
    }

    @Test
    public void testGetIncreaseVarcharColumnQueryOracle() {
        setOracle();

        String sql = distSqlUtils.getIncreaseVarcharColumnQuery(attrDescriptor, newSize);

        assertEquals("alter table tableName modify columnName varchar2(100)", sql);
    }

    @Test
    public void testGetIncreaseVarcharColumnQuerySQLServer() {
        setSQLServer();

        String sql = distSqlUtils.getIncreaseVarcharColumnQuery(attrDescriptor, newSize);

        assertEquals("alter table tableName alter column columnName varchar(100)", sql);
    }
}
