import de.hybris.platform.core.Registry
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.util.Config;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import groovy.transform.Field;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager

TEST_AUTOMATION_PREFIX = "distrelec.automation";
TEST_ACCOUNT_PREFIX = "distrelec.testing";
TEST_PEFORMANCE_TESTING_PREFIX = "performance.testing";
TEST_ACCOUNT_POSTFIX = "@gmail.com";
GENERATED_MAIL_PREFIX = "distrelec.scramble+";
GENERATED_MAIL_POSTFIX = "@gmail.com";
NEW_PASSWORD = "1:vk2zh6piqmZKAoSZF6BKTg==npErRvbcMCtHn24AmXIQvVRstuJ4j8JdFdFtC1F/W1j8kLuPy5+le2RZs1ZPDFdV9u7TzNoZtePt4BYfnU4ihMYr4oVhJeM1/kbR9sCVv24="; // distrelec
PASSWD_ENCODE = "sha_256";
PAYMENT_NOTIFY_PROCESS = "paymentnotifyprocess-";

LOG = LogManager.getLogger("ScrambleDataJob-sql");

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection();

@Field int batchSize = 100;

if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant();
}

boolean isProd = Config.getBoolean("environment.isprod", true);

if (isProd) {
    throw new IllegalArgumentException("can be run on testing environments only");
}

boolean success = true;
try {
    success &= scrambleNps();
    success &= scrambleReturnRequests();
    success &= scrambleCustomers();
    success &= scrambleB2BUnits();
    success &= scrambleB2BUnitsLp();
    success &= scramblePaymentInfos();
    success &= scramblePaymentNotifyInfos();
    success &= scrambleAddresses();

    if (success) {
        LOG.info("Successfully finished")
    } else {
        LOG.warn("Did not complete successfully")
    }
} catch (Exception e) {
    LOG.error("Exception occurred", e);
    success = false;
}

return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.FAILURE, CronJobStatus.FINISHED);

boolean includeEmail(String email, boolean includeNull) {
    if (includeNull) {
        return email == null || includeEmailAddress(email);
    } else {
        return email != null && includeEmailAddress(email);
    }
}

boolean includeEmailAddress(String email) {
    return !((email.startsWith(TEST_ACCOUNT_PREFIX)
            || email.startsWith(TEST_AUTOMATION_PREFIX)
            || email.startsWith(TEST_PEFORMANCE_TESTING_PREFIX)
            || email.startsWith(GENERATED_MAIL_PREFIX)) && email.endsWith(TEST_ACCOUNT_POSTFIX));
}

String concat(String sql1, String sql2) {
    return "concat(" + sql1 + "," + sql2 + ")";
}

String convert(String sql) {
    return "convert(nvarchar(50), " +  sql + ")";
}

String isNumber(String sql) {
    return "isnumeric(" + sql + ")=1";
}
String removeLeadingZeros(String sql) {
    return "try_convert(bigint, " + sql + ")";
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
            sql += concat(removeLeadingZeros("ug.p_erpcustomerid"), concat("'_'", concat(removeLeadingZeros("u.p_erpcontactid"), concat(getCustomerRankSql("u"), getCustomerLength("u", "ug")))));
        }
    } else {
        sql += "1"
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
    String select = "case when " + attr + " is not null" +
            " then " + thenSql;

    if (elseSql != null) {
        select += " else " + elseSql;
    }
    select += " end";

    return select;
}

boolean scramble(String type, String selectSql, String updateSql, boolean includeNull) {
    LOG.info("Start scrambling: " + type);
    LOG.info("Selecting " +  type + " with: " + selectSql);
    LOG.info("Updating " +  type + " with: " + updateSql);

    int numOfScrambled = 0;
    int numOfUpdated = 0;
    def updateStmt;
    connection.prepareStatement(selectSql).withCloseable { selectStmt ->
        selectStmt.setFetchSize(batchSize);

        selectStmt.executeQuery().withCloseable { rs ->
            LOG.info("Start going through " + type + " record set");
            while (rs.next()) {
                String email = rs.getString("email")

                if (includeEmail(email, includeNull)) {
                    if (numOfScrambled == 0) {
                        updateStmt = connection.prepareStatement(updateSql);
                    }
                    updateStmt.setLong(1, rs.getLong("pk"));
                    updateStmt.addBatch();
                    numOfScrambled++;

                    if (numOfScrambled % batchSize == 0) {
                        int[] updatedBatch = updateStmt.executeBatch();
                        numOfUpdated += updatedBatch.sum();
                        LOG.info("Scrambled " + numOfScrambled  + " - " + numOfUpdated + " " + type);

                        try {
                            updateStmt.close();
                            updateStmt = connection.prepareStatement(updateSql);
                            connection.commit();
                        } catch (Exception e) {
                            LOG.error("Exception occurred", e);
                        }
                    }
                }
            }

            if (numOfScrambled % batchSize > 0) {
                int[] updatedBatch = updateStmt.executeBatch();
                numOfUpdated += updatedBatch.sum();
                LOG.info("Scrambled " + numOfScrambled  + " - " + numOfUpdated + " " + type);

                updateStmt.close();
                connection.commit();
            }
        }
    }

    LOG.info(type + " are scrambled: " + numOfScrambled + " - " + numOfUpdated);

    return true;
}

boolean scrambleNps() {
    String generateCustomerIdSql = "case when " + isNumber("s.p_erpcustomerid") + " and " + isNumber("s.p_erpcontactid") +
            " then " + convert(concat(removeLeadingZeros("s.p_erpcustomerid"),concat("'_'",removeLeadingZeros("s.p_erpcontactid")))) +
            " when " + isNumber("s.p_erpcustomerid") +
            " then " + convert(removeLeadingZeros("s.p_erpcustomerid")) +
            " else " + convert(concat("'PK'","s.pk")) + " end";

    String generateContactIdSql = "case when " + isNumber("s.p_erpcontactid") +
            " then " + convert(removeLeadingZeros("s.p_erpcontactid")) +
            " else " + convert(concat("'PK'","s.pk")) + " end";

    String selectSql = "select pk, p_email as email from distnetpromoterscore";

    String updateSql = "update s" +
            " set s.p_email=" + selectCaseIfNotNull("s.p_email", generateEmailSql(generateCustomerIdSql)) +
            " , s.p_text=" + selectCaseIfNotNull("s.p_text", concat(generateContactIdSql,"'TEXT'")) +
            " , s.p_companyname=" + selectCaseIfNotNull("s.p_companyname", concat(generateContactIdSql,"' NAME1'")) +
            " , s.p_customername=" + selectCaseIfNotNull("s.p_customername", concat(generateContactIdSql,"' NAME1'")) +
            " , s.p_firstname=" + selectCaseIfNotNull("s.p_firstname", concat(generateContactIdSql,"' NAME_FIRST'")) +
            " , s.p_lastname=" + selectCaseIfNotNull("s.p_lastname", concat(generateContactIdSql,"' NAME_LAST'")) +
            " from distnetpromoterscore s" +
            " where s.pk=?";

    return scramble("NPS", selectSql, updateSql, true);
}

boolean scramblePaymentNotifyInfos() {
    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, false, JoinType.USER, "u.pk=p.p_user") +
            " then " + convert(generateCustomerIdSql(true, false, JoinType.USER, "u.pk=p.p_user")) +
            " else " + convert(concat("'PK'","p.pk")) + " end";

    String selectSql = "select p.pk as pk, u.p_email as email " +
            " from processes p " +
            " left join users u on p.p_user=u.pk" +
            "  where exists (select 1 from composedtypes ct where p.typepkstring=ct.pk and ct.internalcode='PaymentNotifyProcess')";

    String updateSql = "update p " +
            " set p.p_code=concat('" + PAYMENT_NOTIFY_PROCESS + "',concat(" + generateEmailSql(generateCustomerIdSql) + ", p.p_cartCode))" +
            " from processes p " +
            " where p.pk=?";

    return scramble("PaymentNotifyInfos", selectSql, updateSql, true);
}

String generateCustomerIdSqlForReturnRequests(boolean onlyContactId) {
    String sql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.USER, "u.p_customerid=r.p_customerid") +
            " then " + convert(generateCustomerIdSql(true, onlyContactId, JoinType.USER, "u.p_customerid=r.p_customerid")) +
            " else " + convert(concat("'PK'","r.pk")) + " end";

    return sql;
}

boolean scrambleReturnRequests() {
    String generateCustomerIdSql = generateCustomerIdSqlForReturnRequests(false);
    String generateContactIdSql = generateCustomerIdSqlForReturnRequests(true);

    String selectSql = "select pk, p_email as email from sapreturnrequest";

    String updateSql = "update r" +
            " set r.p_email=" + selectCaseIfNotNull("r.p_email", generateEmailSql(generateCustomerIdSql)) +
            " , r.p_firstname=" + selectCaseIfNotNull("r.p_firstname", concat(generateContactIdSql,"' NAME_FIRST'")) +
            " , r.p_lastname=" + selectCaseIfNotNull("r.p_lastname", concat(generateContactIdSql,"' NAME_LAST'")) +
            " , r.p_phonenumber=" + selectCaseIfNotNull("r.p_phonenumber", generateContactIdSql) +
            " , r.p_company=" + selectCaseIfNotNull("r.p_company", concat(generateContactIdSql,"' NAME1'")) +
            " from sapreturnrequest r" +
            " where r.pk=?";

    return scramble("Return Request", selectSql, updateSql, true);
}

String getCustomerRankSql(String userAlias) {
    String rankSql = "(select case when rank.erpcontactidrank>1 then rank.erpcontactidrank end from " +
            "  (select rankusers.pk, rank() over (order by rankusers.pk) as erpcontactidrank " +
            "     from users rankusers" +
            "     join usergroups rankusergroups on rankusergroups.pk=rankusers.p_defaultb2bunit" +
            "     where " + "rankusers.p_erpcontactid=" + userAlias + ".p_erpcontactid" +
            "       and " + isNumber("rankusergroups.p_erpcustomerid") +
            "  ) rank " +
            "  where " + userAlias + ".pk=rank.pk)";
    return rankSql;
}

String getCustomerLength(String userAlias, String userGroupAlias) {
    String customerLength = "(case when len(" + userAlias + ".p_erpcontactid)<10 and len(" + userGroupAlias + ".p_erpcustomerid)<10 " +
            "then concat('_a', concat(10 - len(" + userAlias + ".p_erpcontactid), 10 - len(" + userGroupAlias + ".p_erpcustomerid))) " +
            "when len(" + userAlias + ".p_erpcontactid)<10" +
            "then concat('_b', 10 - len(" + userAlias + ".p_erpcontactid)) " +
            "when len(" + userGroupAlias + ".p_erpcustomerid)<10" +
            "then concat('_c', 10 - len(" + userGroupAlias + ".p_erpcustomerid)) " +
            "else '' end)";
    return customerLength
}

String generateCustomerIdSqlForCustomers(boolean onlyContactId) {
    String sql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.USERGROUPS, "ug.pk=u.p_defaultb2bunit") +
            " then " + convert(generateCustomerIdSql(true, onlyContactId, JoinType.USERGROUPS, "ug.pk=u.p_defaultb2bunit")) +
            " else " + convert(concat("'PK'","u.pk")) + " end";

    return sql;
}

boolean scrambleCustomers() {
    String generateCustomerIdSql = generateCustomerIdSqlForCustomers(false);
    String generateContactIdSql = generateCustomerIdSqlForCustomers(true);

    String selectSql = "select pk as pk, p_email as email from users";

    String updateSql = "update u set u.p_originaluid=" + generateEmailSql(generateCustomerIdSql) +
            ", u.p_email=" + generateEmailSql(generateCustomerIdSql) +
            ", u.name=" + concat(concat(generateContactIdSql, "' NAME_FIRST '"), concat(generateContactIdSql, "' NAME_LAST'")) +
            ", u.UniqueId=" + generateEmailSql(generateCustomerIdSql) +
            ", u.description=" + selectCaseIfNotNull("u.description", concat(generateContactIdSql, "' DESCRIPTION'")) +
            ", u.passwd='" + NEW_PASSWORD + "'" +
            ", u.encode='" + PASSWD_ENCODE + "'" +
            ", u.p_passwordquestion=" + selectCaseIfNotNull("u.p_passwordquestion", concat(generateContactIdSql, "' PASSWORDQUESTION'")) +
            ", u.p_passwordanswer=" + selectCaseIfNotNull("u.p_passwordanswer", concat(generateContactIdSql, "' PASSWORDANSWER'")) +
            " from users u" +
            " where u.pk=?";

    return scramble("Customer", selectSql, updateSql, false);
}

boolean scrambleB2BUnits() {
    String generateCustomerIdSql = selectCaseIfNotNull("ug.p_erpcustomerid", convert(removeLeadingZeros("ug.p_erpcustomerid")), convert(concat("'PK'","ug.pk")))

    String selectSql = getB2BUnitsSelectSql();

    String updateSql = "update ug" +
            " set ug.name=" + selectCaseIfNotNull("ug.name", concat(generateCustomerIdSql,"' NAME1'")) +
            " , ug.p_companyname2=" + selectCaseIfNotNull("ug.p_companyname2", concat(generateCustomerIdSql,"' NAME2'")) +
            " , ug.p_companyname3=" + selectCaseIfNotNull("ug.p_companyname3", concat(generateCustomerIdSql,"' NAME3'")) +
            " , ug.p_vatid=" + selectCaseIfNotNull("ug.p_vatid", generateCustomerIdSql) +
            " from usergroups ug" +
            " where ug.pk=?";

    return scramble("B2B Unit", selectSql, updateSql, true);
}

boolean scrambleB2BUnitsLp() {
    String generateCustomerIdSql = "(select " +
            selectCaseIfNotNull("ug.p_erpcustomerid", convert(removeLeadingZeros("ug.p_erpcustomerid")), convert(concat("'PK'","ug.pk"))) +
            " from usergroups ug " +
            " where ug.pk=uglp.itempk " +
            "   and exists (select 1 from composedtypes ct where ug.typepkstring=ct.pk and ct.internalcode='B2BUnit'))";

    String selectSql = getB2BUnitsSelectSql(); // same select can be used as for b2b units

    String updateSql = "update uglp" +
            " set uglp.p_locname=" + selectCaseIfNotNull("uglp.p_locname",concat(generateCustomerIdSql,"' NAME1'")) +
            " from usergroupslp uglp" +
            " where uglp.itempk=?";

    return scramble("B2B Unit Lp", selectSql, updateSql, true);
}

String getB2BUnitsSelectSql() {
    String selectSql = "select ug.pk, a.p_email as email " +
            "from usergroups ug " +
            "left join addresses a on ug.p_shippingaddress=a.pk" +
            " where exists (select 1 from composedtypes ct where ug.typepkstring=ct.pk and ct.internalcode='B2BUnit')";
    return selectSql;
}

boolean scramblePaymentInfos() {
    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, false, JoinType.USER, "u.pk=pi.userpk") +
            " then " + generateCustomerIdSql(true, false, JoinType.USER, "u.pk=pi.userpk") +
            " else " + concat("'PK'", "pi.pk") + " end";

    String selectSql = "select pi.pk as pk, u.p_email as email" +
            " from paymentinfos pi " +
            " left join users u on u.pk=pi.userpk";

    String updateSql = "update pi" +
            "  set pi.code=" + selectCaseIfNotNull("pi.code", concat(concat("'PK'", "pi.pk"), "'CODE'")) +
            "  , pi.p_ccowner=" + selectCaseIfNotNull("pi.p_ccowner", concat(generateCustomerIdSql, "'CCOWNER'")) +
            " from paymentinfos pi" +
            " where pi.pk=?";

    return scramble("Payment info", selectSql, updateSql, true);
}

String generateCustomerIdSqlForAddresses(boolean onlyContactId) {
    // do not know how use a.p_erpaddressid

    String generateCustomerIdSql = "case when exists" + generateCustomerIdSql(false, onlyContactId, JoinType.ORDER, "o.pk=a.ownerpkstring") +
            " then " + convert(generateCustomerIdSql(true, onlyContactId, JoinType.ORDER, "o.pk=a.ownerpkstring")) +
            " when exists " +  generateCustomerIdSql(false, onlyContactId, JoinType.PAYMENT_INFO, "pi.pk=a.ownerpkstring") +
            " then " +  convert(generateCustomerIdSql(true, onlyContactId, JoinType.PAYMENT_INFO, "pi.pk=a.ownerpkstring")) +
            " when exists " + generateCustomerIdSql(false, onlyContactId, JoinType.USER, "u.pk=a.ownerpkstring") +
            " then " + convert(generateCustomerIdSql(true, onlyContactId, JoinType.USER, "u.pk=a.ownerpkstring")) +
            " else " + convert(concat("'PK'","a.pk")) +
            " end";

    return generateCustomerIdSql;
}

boolean scrambleAddresses() {
    String generateCustomerIdSql = generateCustomerIdSqlForAddresses(false);
    String generateContactIdSql = generateCustomerIdSqlForAddresses(true);

    String selectSql = "select a.pk as pk, a.p_email as email " +
            " from addresses a ";

    String updateSql = "update a" +
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
            " from addresses a" +
            " where a.pk=?";

    return scramble("Addresses", selectSql, updateSql, true);
}
