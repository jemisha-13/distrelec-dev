package com.namics.distrelec.b2b.core.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.util.Config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

/**
 * Utilities for writing db agnostic queries.
 */
public class DistSqlUtilsImpl implements DistSqlUtils {

    public static final String ORACLE_DATE_TIME_PATTERN = "YYYY-MM-DD HH24:MI:SS";

    @Override
    public String chr(String character) {
        StringBuilder sb = new StringBuilder(" ");

        if (isOracleUsed()) {
            sb = sb.append("chr(");
        } else {
            sb = sb.append("char(");
        }

        sb = sb.append(character)
            .append(") ");

        return sb.toString();
    }

    @Override
    public String concat(String... elements) {
        if (isSQLServerUsed()) {
            return new StringBuilder("CONCAT(")
                    .append(StringUtils.join(elements, ','))
                    .append(")")
                    .toString();
        } else {
            return StringUtils.join(elements, " || ");
        }
    }

    @Override
    public String locate(String haystack, String needle, int startPos, int occurrences) {
        StringBuilder sb = new StringBuilder(" ");
        if (isSQLServerUsed()) {
            sb.append("charindex('")
                    .append(needle)
                    .append("',")
                    .append(haystack)
                    .append(",")
                    .append(startPos)
                    .append(") ");
        } else {
            if (isOracleUsed()) {
                sb = sb.append("instr(");
            } else {
                sb = sb.append("locate(");
            }
            sb = sb.append(haystack)
                    .append(",'")
                    .append(needle)
                    .append("',")
                    .append(startPos)
                    .append(",")
                    .append(occurrences)
                    .append(") ");
        }

        return sb.toString();
    }

    @Override
    public String getIncreaseVarcharColumnQuery(AttributeDescriptorModel attrDescriptor, int newSize) {
        String tableName = getTableName(attrDescriptor);
        String columnName = attrDescriptor.getDatabaseColumn();

        if (isOracleUsed()) {
            return String.format("alter table %s modify %s varchar2(%d)", tableName, columnName, newSize);
        } else if (isMySQLUsed()) {
            return String.format("alter table %s modify %s varchar(%d)", tableName, columnName, newSize);
        } else if (isSQLServerUsed()) {
            return String.format("alter table %s alter column %s varchar(%d)", tableName, columnName, newSize);
        } else {
            throw new IllegalArgumentException("Unsupported db type");
        }
    }

    @Override
    public String isNull(String expression, String defaultValue) {
        StringBuilder sb = new StringBuilder();
        if (isOracleUsed()) {
            sb.append(" NVL");
        } else {
            sb.append(" ISNULL");
        }
        sb.append("(")
                .append(expression)
                .append(",")
                .append(defaultValue)
                .append(")");

        return sb.toString();
    }

    @Override
    public String now() {
        if (isOracleUsed()) {
            return " SYSDATE ";
        } else if (isSQLServerUsed()) {
            return " GETDATE() ";
        } else {
            return " NOW() ";
        }
    }

    @Override
    public  String substring(String expression, String startPos, String length) {
        StringBuilder sb = new StringBuilder(" ");

        if (isOracleUsed()) {
            sb = sb.append("substr(");
        } else {
            sb = sb.append("substring(");
        }
        sb = sb.append(expression)
            .append(",")
            .append(startPos)
            .append(",")
            .append(length)
            .append(") ");
        return sb.toString();
    }

    @Override
    public  String replaceRegexp(String expression, String pattern, String replacement) {
        StringBuilder sb = new StringBuilder(" ");
        if (isOracleUsed()) {
            sb = sb.append("regexp_replace(")
                .append(expression)
                .append(",'")
                .append(pattern)
                .append("','")
                .append(replacement);
        } else {
            sb = sb.append("replace_regexpr('")
                .append(pattern)
                .append("' in ")
                .append(expression)
                .append(" with '")
                .append(replacement);
        }

        sb = sb.append("') ");
        return sb.toString();
    }

    @Override
    public String stringAgg(String expression) {
        return stringAgg(expression, null, null);
    }

    @Override
    public String stringAgg(String expression, String connector, String orderBy) {
        StringBuilder sb = new StringBuilder(" ");
        if (isOracleUsed()) {
            sb.append("listagg(");
        } else {
            sb.append("string_agg(");
        }
        sb.append(expression);
        if (connector != null) {
            sb.append(",'")
                .append(connector)
                .append("'");
        } else if (isSQLServerUsed()) {
            sb.append(",''");
        }

        if (orderBy != null) {
            if (isOracleUsed() || isSQLServerUsed()) {
                sb.append(") WITHIN GROUP (ORDER BY ");
                sb.append(orderBy);
            } else {
                sb.append(" ORDER BY ");
                sb.append(orderBy);
            }
        }

        sb.append(") ");
        return sb.toString();
    }

    @Override
    public String toNvarchar(String character) {
        return toNvarchar(character, null);
    }

    @Override
    public String toNvarchar(String character, String format) {
        StringBuilder sb = new StringBuilder(" ");
        boolean isSqlDateTime = isSQLServerUsed() && ORACLE_DATE_TIME_PATTERN.equals(format);

        if (isOracleUsed()) {
            sb.append("to_char(");
        } else if (isSQLServerUsed()) {
            if (isSqlDateTime) {
                sb.append("convert(datetime2(0),");
            } else {
                sb.append("convert(nvarchar,");
            }
        } else {
            sb.append("to_nvarchar(");
        }

        sb.append(character);

        if (format != null && !isSqlDateTime) {
            sb.append(",'")
                .append(format)
                .append("'");
        }

        sb.append(") ");

        return sb.toString();
    }

    @Override
    public String toTimestamp(String date, String format) {
        StringBuilder sb = new StringBuilder(" ");

        if (isOracleUsed()) {
            sb = sb.append("to_date(");
        } else if (isSQLServerUsed()){
            sb = sb.append("CONVERT(datetime, '")
                .append(date)
                    .append("', 113) ");
            return sb.toString();
        }
        else {
            sb = sb.append("to_timestamp(");
        }

        sb = sb.append(date)
            .append(",'")
            .append(format)
            .append("') ");

        return sb.toString();
    }

    @Override
    public int booleanToTinyint(Boolean value) {
        if (isFalse(value)) {
            return 0;
        }

        return 1;
    }

    @Override
    public String where(String where)
    {
        StringBuilder sb = new StringBuilder(" where ");
        if (isOracleUsed())
        {
            sb = sb.append("1=1 and ");
        }
        sb = sb.append(where)
                .append(" ");
        return sb.toString();
    }

    @Override
    public String length(String lengthOf)
    {
        if (isOracleUsed())
        {
            return "LENGTH(" + lengthOf + ")";
        }
        else
        {
            return "LEN(" + lengthOf + ")";
        }
    }

    /**
     * Adds N prefix for mssql to correctly translate special characters so they can be correctly used in conditions.
     * Otherwise, a greek ohm character will be considered as 'O' etc.
     */
    @Override
    public String utf8(String stringExpression)
    {
        if (isSQLServerUsed())
        {
            return "N".concat(stringExpression);
        }
        return stringExpression;
    }

    protected boolean isMySQLUsed() {
        return Config.isMySQLUsed();
    }

    protected boolean isOracleUsed() {
        return Config.isOracleUsed();
    }

    protected boolean isSQLServerUsed() {
        return Config.isSQLServerUsed();
    }

    protected String getTableName(AttributeDescriptorModel attrDescriptor) {
        String tableName = getTenantAwareTableName(attrDescriptor.getEnclosingType().getTable());

        if (Boolean.TRUE.equals(attrDescriptor.getLocalized())) {
            tableName += "lp";
        }
        return tableName;
    }

    protected String getTenantAwareTableName(String tableName) {
        String currentTenantId = Registry.getCurrentTenant().getTenantID();
        return "master".equals(currentTenantId) ? tableName : currentTenantId + "_" + tableName;
    }
}
