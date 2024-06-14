import de.hybris.platform.core.Registry
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.util.Config
import de.hybris.platform.servicelayer.cronjob.PerformResult
import groovy.transform.Field

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager

import java.sql.Statement;

TEST_AUTOMATION_PREFIX = "distrelec.automation";
TEST_ACCOUNT_PREFIX = "distrelec.testing";
TEST_ACCOUNT_POSTFIX = "@gmail.com";
GENERATED_MAIL_PREFIX = "distrelec.scramble+";
GENERATED_MAIL_POSTFIX = "@gmail.com";
NEW_PASSWORD = "1:vk2zh6piqmZKAoSZF6BKTg==npErRvbcMCtHn24AmXIQvVRstuJ4j8JdFdFtC1F/W1j8kLuPy5+le2RZs1ZPDFdV9u7TzNoZtePt4BYfnU4ihMYr4oVhJeM1/kbR9sCVv24="; // distrelec
PASSWD_ENCODE = "sha_256";
PAYMENT_NOTIFY_PROCESS = "paymentnotifyprocess-";

LOG = LogManager.getLogger("ScrambleDataJob-sql");

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();

@Field int batchSize = 10000;

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant();
}

boolean isProd = Config.getBoolean("environment.isprod", true);

if (isProd) {
    throw new IllegalArgumentException("can be run on testing environments only");
}


boolean success = true;
success &= scrambleCustomerErpContactId()
success &= scrambleNps();
success &= scrambleReturnRequests();
success &= scrambleCustomers();
success &= scrambleB2BUnits();
success &= scrambleB2BUnitsLp();
success &= scramblePaymentInfos();
success &= scrambleAddresses();
success &= scramblePaymentNotifyInfos();

if (success) {
    LOG.info("Successfully finished")
} else {
    LOG.warn("Did not complete successfully")
}

return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.FAILURE, CronJobStatus.FINISHED);

def getLimitQuery() {
    if (Config.isHanaUsed()) {
        return " LIMIT $batchSize";
    } else {
        return " AND rownum <= $batchSize";
    }
}

String concat(String sql1, String sql2) {
    return "concat(" + sql1 + "," + sql2 + ")";
}

String isNumber(String sql) {
    return sql + " like_regexpr '^\\d+\$'";
}
String removeLeadingZeros(String sql) {
    return "replace_regexpr('^0+(\\d+)' in " + sql + " with '\\1')";
}

String generateEmailSql(String innerSql) {
    return "concat('" + GENERATED_MAIL_PREFIX + "',concat(" + innerSql + ",'" + GENERATED_MAIL_POSTFIX + "'))";
}

enum JoinType {
    ORDER,
    PAYMENT_INFO,
    USER,
    USERGROUPS
}

String generateCustomerIdSql(boolean buildSelection, boolean onlyContactId, JoinType joinType, String whereStmt) {
    String sql = "(select "
    if (buildSelection) {
        if (onlyContactId) {
            sql += removeLeadingZeros("u.p_erpcontactid")
        } else {
            sql += concat(removeLeadingZeros("ug.p_erpcustomerid"), concat("'_'", removeLeadingZeros("u.p_erpcontactid")))
        }
    } else {
        sql += "*"
    }
    if (JoinType.USERGROUPS.equals(joinType)) {
        sql += " from usergroups ug"
    } else {
        switch (joinType) {
            case JoinType.ORDER:
                sql += " from orders o join users u on u.pk=o.userpk"
                break;
            case JoinType.PAYMENT_INFO:
                sql += " from paymentinfos pi join users u on u.pk=pi.userpk"
                break;
            case JoinType.USER:
                sql += " from users u"
                break;
        }
        sql += " join usergroups ug on ug.pk=u.p_defaultb2bunit"
    }
    sql += " where " + whereStmt
    sql += " and " + isNumber("u.p_erpcontactid")
    sql += " and " + isNumber("ug.p_erpcustomerid")
    sql += ")"
    return sql;
}

String selectCaseIfNotNull(String attr, String thenSql) {
    return selectCaseIfNotNull(attr, thenSql, null);
}

String selectCaseIfNotNull(String attr, String thenSql, String elseSql) {
    String select = "case when " + attr + " is not null\n" +
            " then " + thenSql + "\n";

    if (elseSql != null) {
        select += " else " + elseSql;
    }
    select += " end";

    return select;
}

boolean scramble(String type, String sql) {
    LOG.info("Start scrambling " + type + ": " + sql)

    try {
        PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        statement.executeUpdate();
        LOG.info(type + " scrambled");
        return true;
    } catch (SQLException e) {
        LOG.error("Error during " + type + " scrambling", e);
        return false;
    }
}

boolean scrambleNps() {
    String generateCustomerIdSql = "case when " + isNumber("s.p_erpcustomerid") + " and " + isNumber("s.p_erpcontactid") +
            " then " + concat(removeLeadingZeros("s.p_erpcustomerid"),concat("'_'",removeLeadingZeros("s.p_erpcontactid"))) +
            " when " + isNumber("s.p_erpcustomerid") +
            " then " + removeLeadingZeros("s.p_erpcustomerid") +
            " else " + concat("'PK'","s.pk") + " end";

    String generateContactIdSql = "case when " + isNumber("s.p_erpcontactid") +
            " then " + removeLeadingZeros("s.p_erpcontactid") +
            " else " + concat("'PK'","s.pk") + " end";

    String sql = "update distnetpromoterscore s\n" +
            " set s.p_email=" + selectCaseIfNotNull("s.p_email", generateEmailSql(generateCustomerIdSql)) + "\n" +
            " , s.p_text=" + selectCaseIfNotNull("s.p_text", concat(generateContactIdSql,"'TEXT'")) + "\n" +
            " , s.p_companyname=" + selectCaseIfNotNull("s.p_companyname", concat(generateContactIdSql,"' NAME1'")) + "\n" +
            " , s.p_customername=" + selectCaseIfNotNull("s.p_customername", concat(generateContactIdSql,"' NAME1'")) + "\n" +
            " , s.p_firstname=" + selectCaseIfNotNull("s.p_firstname", concat(generateContactIdSql,"' NAME_FIRST'")) + "\n" +
            " , s.p_lastname=" + selectCaseIfNotNull("s.p_lastname", concat(generateContactIdSql,"' NAME_LAST'")) + "\n" +
            " where s.p_email is null or (s.p_email not like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'\n" +
            "   and s.p_email not like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "')\n"

    return scramble("NPS", sql);
}

boolean scramblePaymentNotifyInfos() {
    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, false, JoinType.USER, "u.pk=p.p_user") +
            " then " + generateCustomerIdSql(true, false, JoinType.USER, "u.pk=p.p_user") +
            " else " + concat("'PK'","p.pk") + " end";

    String sql = "update processes p \n" +
            " set p.p_code=concat('" + PAYMENT_NOTIFY_PROCESS + "',concat(" + generateEmailSql(generateCustomerIdSql) + ",p.p_cartCode))" +
            "  where exists (select * from composedtypes ct where p.typepkstring=ct.pk and ct.internalcode='PaymentNotifyProcess')";

    return scramble("PaymentNotifyInfos", sql);
}

String generateCustomerIdSqlForReturnRequests(boolean onlyContactId) {
    String sql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.USER, "u.p_customerid=r.p_customerid") +
            " then " + generateCustomerIdSql(true, onlyContactId, JoinType.USER, "u.p_customerid=r.p_customerid") +
            " else " + concat("'PK'","r.pk") + " end";

    return sql;
}

boolean scrambleReturnRequests() {
    String generateCustomerIdSql = generateCustomerIdSqlForReturnRequests(false);
    String generateContactIdSql = generateCustomerIdSqlForReturnRequests(true);

    String sql = "update sapreturnrequest r\n" +
            " set r.p_email=" + selectCaseIfNotNull("r.p_email", generateEmailSql(generateCustomerIdSql)) +
            " , r.p_firstname=" + selectCaseIfNotNull("r.p_firstname", concat(generateContactIdSql,"' NAME_FIRST'")) +
            " , r.p_lastname=" + selectCaseIfNotNull("r.p_lastname", concat(generateContactIdSql,"' NAME_LAST'")) +
            " , r.p_phonenumber=" + selectCaseIfNotNull("r.p_phonenumber", generateContactIdSql) +
            " , r.p_company=" + selectCaseIfNotNull("r.p_company", concat(generateContactIdSql,"' NAME1'")) +
            " where r.p_email is null or (r.p_email not like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'\n" +
            "   and r.p_email not like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "')\n"

    return scramble("Return requests", sql);
}

boolean scrambleCustomerErpContactId() {
    String rankSql = "(select rank.erpcontactidrank from " +
            "  (select rankusers.pk, rank() over (partition by " +
            removeLeadingZeros("rankusergroups.p_erpcustomerid") + "," + removeLeadingZeros("rankusers.p_erpcontactid") +
            " order by rankusers.pk) as erpcontactidrank " +
            "     from users rankusers" +
            "     join usergroups rankusergroups on rankusergroups.pk=rankusers.p_defaultb2bunit" +
            "     where " + removeLeadingZeros("rankusers.p_erpcontactid") + "=" + removeLeadingZeros("u.p_erpcontactid") +
            "       and " + isNumber("rankusergroups.p_erpcustomerid") +
            "  ) rank " +
            "  where u.pk=rank.pk";

    String selectRankSql = rankSql + ")";
    String existsRankSql = "exists" + rankSql + " and rank.erpcontactidrank>1)";

    String sql = "update users u " +
            "\n set u.p_erpcontactid=concat(u.p_erpcontactid, " + selectRankSql + ")" +
            "\n where " + isNumber("u.p_erpcontactid") + " and " + existsRankSql;

    return scramble("Customer erpcontactid", sql);
}

String generateCustomerIdSqlForCustomers(boolean onlyContactId) {
    String sql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.USERGROUPS, "ug.pk=u.p_defaultb2bunit") +
            " then " + generateCustomerIdSql(true, onlyContactId, JoinType.USERGROUPS, "ug.pk=u.p_defaultb2bunit") +
            " else " + concat("'PK'","u.pk") + " end";

    return sql;
}

boolean scrambleCustomers() {
    String generateCustomerIdSql = generateCustomerIdSqlForCustomers(false);
    String generateContactIdSql = generateCustomerIdSqlForCustomers(true);

    String sql = "update users u \n" +
            " set u.p_originaluid=" + generateEmailSql(generateCustomerIdSql) +
            "\n, u.p_email=" + generateEmailSql(generateCustomerIdSql) +
            "\n, u.name=" + concat(concat(generateContactIdSql,"' NAME_FIRST '"),concat(generateContactIdSql,"' NAME_LAST'")) +
            "\n, u.UniqueId=" + generateEmailSql(generateCustomerIdSql) +
            "\n, u.description=" + selectCaseIfNotNull("u.description",concat(generateContactIdSql,"' DESCRIPTION'")) +
            "\n, u.passwd='" + NEW_PASSWORD + "'" +
            "\n, u.encode='" + PASSWD_ENCODE + "'" +
            "\n, u.p_passwordquestion=" + selectCaseIfNotNull("u.p_passwordquestion",concat(generateContactIdSql,"' PASSWORDQUESTION'")) +
            "\n, u.p_passwordanswer=" + selectCaseIfNotNull("u.p_passwordanswer",concat(generateContactIdSql,"' PASSWORDANSWER'")) +
            "\nwhere u.pk in (select u2.pk from users u2 where u2.UniqueID not like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'" +
            "    and u2.UniqueID not like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'" +
            "    and u2.UniqueID not like '" + GENERATED_MAIL_PREFIX + "%" + GENERATED_MAIL_POSTFIX + "'" +
            "    and exists (select * from composedtypes ct where u2.typepkstring=ct.pk and ct.internalcode='B2BCustomer')" +
            getLimitQuery() + ")";

    LOG.info("Start scrambling users: " + sql);

    while (true) {
        int updateRows = 0;
        Statement statement = connection.prepareStatement(sql);
        try {
            updateRows = statement.executeUpdate();
        } finally {
            statement.close();
        }

        LOG.info("Scrambled " + updateRows + " users");
        if (updateRows == 0) {
            break;
        }
    }

    return true;
}

boolean scrambleB2BUnits() {
    String generateCustomerIdSql = selectCaseIfNotNull("ug.p_erpcustomerid", removeLeadingZeros("ug.p_erpcustomerid"), concat("'PK'","ug.pk"))

    String sql = "update usergroups ug \n" +
            " set ug.name=" + selectCaseIfNotNull("ug.name", concat(generateCustomerIdSql,"' NAME1'")) +
            " , ug.p_companyname2=" + selectCaseIfNotNull("ug.p_companyname2", concat(generateCustomerIdSql,"' NAME2'")) +
            " , ug.p_companyname3=" + selectCaseIfNotNull("ug.p_companyname3", concat(generateCustomerIdSql,"' NAME3'")) +
            " , ug.p_vatid=" + selectCaseIfNotNull("ug.p_vatid", generateCustomerIdSql) +
            " where exists (select * from composedtypes ct where ug.typepkstring=ct.pk and ct.internalcode='B2BUnit')" +
            "  and not exists (select * from addresses a where ug.p_shippingaddress=a.pk " +
            "  and (a.p_email like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'" +
            "  or a.p_email like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'))";

    return scramble("B2B units", sql);
}

boolean scrambleB2BUnitsLp() {
    String generateCustomerIdSql = "(select " +
            selectCaseIfNotNull("ug.p_erpcustomerid", removeLeadingZeros("ug.p_erpcustomerid"), concat("'PK'","ug.pk")) +
            " from usergroups ug join composedtypes ct on ug.typepkstring=ct.pk" +
            " where ug.pk=uglp.itempk and ct.internalcode='B2BUnit')";

    String sql = "update usergroupslp uglp \n" +
            " set uglp.p_locname=" + selectCaseIfNotNull("uglp.p_locname",concat(generateCustomerIdSql,"' NAME1'")) +
            " where exists (select * from usergroups ug join composedtypes ct on ug.typepkstring=ct.pk where ug.pk=uglp.itempk and ct.internalcode='B2BUnit')" +
            " and not exists (select * from usergroups ug join composedtypes ct on ug.typepkstring=ct.pk join addresses a on ug.p_shippingaddress=a.pk " +
            " where ug.pk=uglp.itempk and ct.internalcode='B2BUnit'" +
            "  and (a.p_email like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'" +
            "  or a.p_email like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'))";

    return scramble("B2B units lp", sql);
}

boolean scramblePaymentInfos() {
    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, false, JoinType.USER, "u.pk=pi.userpk") +
            " then " + generateCustomerIdSql(true, false, JoinType.USER, "u.pk=pi.userpk") +
            " else " + concat("'PK'","pi.pk") + " end";

    String sql = "update paymentinfos pi\n" +
            "  set pi.code=" + selectCaseIfNotNull("pi.code", concat(concat("'PK'","pi.pk"),"'CODE'")) +
            "  , pi.p_ccowner=" + selectCaseIfNotNull("pi.p_ccowner",concat(generateCustomerIdSql,"'CCOWNER'")) +
            " where not exists (select * from users u where u.pk=pi.userpk " +
            "  and u.p_email like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'\n" +
            "  and u.p_email like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "')";

    return scramble("Payment infos", sql);
}

String generateCustomerIdSqlForAddresses(boolean onlyContactId) {
    // do not know how use a.p_erpaddressid
/*
    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.ORDER, "o.pk=a.ownerpkstring") +
            " then " + generateCustomerIdSql(true, onlyContactId, JoinType.ORDER, "o.pk=a.ownerpkstring") +
            " when exists " +  generateCustomerIdSql(false, onlyContactId, JoinType.PAYMENT_INFO, "pi.pk=a.ownerpkstring") +
            " then " +  generateCustomerIdSql(true, onlyContactId, JoinType.PAYMENT_INFO, "pi.pk=a.ownerpkstring") +
            " when exists " + generateCustomerIdSql(false, onlyContactId, JoinType.USER, "u.pk=a.ownerpkstring") +
            " then " + generateCustomerIdSql(true, onlyContactId, JoinType.USER, "u.pk=a.ownerpkstring") +
            " else " + concat("'PK'","a.pk") +
            " end";*/

    // temporary just use address pk
    String generateCustomerIdSql = concat("'PK'","a.pk");

    return generateCustomerIdSql;
}

boolean scrambleAddresses() {
    String generateCustomerIdSql = generateCustomerIdSqlForAddresses(false);
    String generateContactIdSql = generateCustomerIdSqlForAddresses(true);

    String sql = "update addresses a \n" +
            " set a.p_email=" + generateEmailSql(generateCustomerIdSql) +
            " , a.p_company=" + selectCaseIfNotNull("a.p_company", concat(generateContactIdSql,"' NAME1'")) +
            " , a.p_companyname2=" + selectCaseIfNotNull("a.p_companyname2", concat(generateContactIdSql,"' NAME2'")) +
            " , a.p_companyname3=" + selectCaseIfNotNull("a.p_companyname3", concat(generateContactIdSql,"' NAME3'")) +
            " , a.p_firstname=" + selectCaseIfNotNull("a.p_firstname", concat(generateContactIdSql,"' NAME_FIRST'")) +
            " , a.p_lastname=" + selectCaseIfNotNull("a.p_lastname", concat(generateContactIdSql,"' NAME_LAST'")) +
            " , a.p_phone1=" + selectCaseIfNotNull("a.p_phone1", generateContactIdSql) +
            " , a.p_phone2=" + selectCaseIfNotNull("a.p_phone2", generateContactIdSql) +
            " , a.p_fax=" + selectCaseIfNotNull("a.p_fax", generateContactIdSql) +
            " , a.p_cellphone=" + selectCaseIfNotNull("a.p_cellphone", generateContactIdSql) +
            " , a.p_streetname=" + selectCaseIfNotNull("a.p_streetname", concat(generateContactIdSql,"' STREET'")) +
            " , a.p_streetnumber=" + selectCaseIfNotNull("a.p_streetnumber", generateContactIdSql) +
            " , a.p_pobox=" + selectCaseIfNotNull("a.p_pobox", generateContactIdSql) +
            " where a.pk in (select a2.pk from addresses a2 " +
            "   where a2.p_email is null or (a2.p_email not like '" + TEST_ACCOUNT_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'\n" +
            "     and a2.p_email not like '" + TEST_AUTOMATION_PREFIX + "%" + TEST_ACCOUNT_POSTFIX + "'\n" +
            "     and a2.p_email not like '" + GENERATED_MAIL_PREFIX + "%" + GENERATED_MAIL_POSTFIX + "')\n" +
            getLimitQuery() + ")";

    LOG.info("Start scrambling addresses: " + sql);

    while (true) {
        int updateRows = 0;
        Statement statement = connection.prepareStatement(sql);
        try {
            updateRows = statement.executeUpdate();
        } finally {
            statement.close();
        }

        LOG.info("Scrambled " + updateRows + " addresses");
        if (updateRows == 0) {
            break;
        }
    }

    return true;
}
