package com.namics.distrelec.b2b.core.util;


import com.namics.distrelec.b2b.core.model.payment.HopSupportingPaymentTransactionEntryModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentUtilsTest {

    AbstractOrderModel order;

    PaymentTransactionModel failedPaymentTransaction;

    PaymentTransactionModel successfulPaymentTransaction;

    HopSupportingPaymentTransactionEntryModel requestEntry;

    HopSupportingPaymentTransactionEntryModel failedNotifyEntry;

    HopSupportingPaymentTransactionEntryModel failureEntry;

    HopSupportingPaymentTransactionEntryModel successOkNotifyEntry;

    HopSupportingPaymentTransactionEntryModel successAuthorizedNotifyEntry;

    HopSupportingPaymentTransactionEntryModel successOkEntry;

    HopSupportingPaymentTransactionEntryModel successAuthorizedEntry;

    String refNr = "007CRBBC210721100028";

    @Before
    public void setUp() throws Exception {
        order = mock(AbstractOrderModel.class);
        failedPaymentTransaction = mock(PaymentTransactionModel.class);
        successfulPaymentTransaction = mock(PaymentTransactionModel.class);
        requestEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        failedNotifyEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        failureEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        successOkNotifyEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        successAuthorizedNotifyEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        successOkEntry = mock(HopSupportingPaymentTransactionEntryModel.class);
        successAuthorizedEntry = mock(HopSupportingPaymentTransactionEntryModel.class);


        when(requestEntry.getAmount()).thenReturn(new BigDecimal("28.55"));
        when(requestEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860d");
        when(requestEntry.getEncryptedTransaction()).thenReturn("TransID=8877881982980&RefNr=" + refNr + "&Amount=2855&Currency=CHF&UserData=orderCode:007CRBBC;Amount:2855;Currency:CHF;PaymentModeCode:CreditCard&OrderDesc=30099461,7.05;&Response=encrypt&Capture=Manual&AddrStreet=Test&AddrStreetNr=12&AddrZip=1234&AddrCity=Test&AddrCountryCode=ch&Salutation=Mr&Title=Mr&IPZone=756,438&Zone=756,438&CompanyOrPerson=F&SDCompanyOrPerson=F&E-Mail=test@distrelec.com&SDStreet=Test&SDHouseNumber=12&SDZipCode=1234&SDCity=Test&SDCountryCode=756&SDSalutation=Mr&SDTitle=Mr&SDEmail=test@distrelec.com");
        when(requestEntry.getTransId()).thenReturn("8877881982980");
        when(requestEntry.getType()).thenReturn(PaymentTransactionType.PAYMENT_REQUEST);

        when(failedNotifyEntry.getAmount()).thenReturn(new BigDecimal("22.55"));
        when(failedNotifyEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860dc");
        when(failedNotifyEntry.getTransactionStatus()).thenReturn("FAILED");
        when(failedNotifyEntry.getTransId()).thenReturn("8877881982980_failedNotify");
        when(failedNotifyEntry.getType()).thenReturn(PaymentTransactionType.NOTIFY);

        when(failureEntry.getAmount()).thenReturn(new BigDecimal("22.55"));
        when(failureEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860dc");
        when(failureEntry.getTransId()).thenReturn("8877881982980_failure");
        when(failureEntry.getTransactionStatus()).thenReturn("FAILED");
        when(failureEntry.getType()).thenReturn(PaymentTransactionType.FAILED_RESPONSE);

        when(successOkNotifyEntry.getAmount()).thenReturn(new BigDecimal("28.55"));
        when(successOkNotifyEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860d");
        when(successOkNotifyEntry.getEncryptedTransaction()).thenReturn("test");
        when(successOkNotifyEntry.getTransId()).thenReturn("8877881982980_ok");
        when(successOkNotifyEntry.getTransactionStatus()).thenReturn("OK");
        when(successOkNotifyEntry.getType()).thenReturn(PaymentTransactionType.NOTIFY);

        when(successAuthorizedNotifyEntry.getAmount()).thenReturn(new BigDecimal("28.55"));
        when(successAuthorizedNotifyEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860d");
        when(successAuthorizedNotifyEntry.getEncryptedTransaction()).thenReturn("test");
        when(successAuthorizedNotifyEntry.getTransId()).thenReturn("8877881982980_authorized");
        when(successAuthorizedNotifyEntry.getTransactionStatus()).thenReturn("AUTHORIZED");
        when(successAuthorizedNotifyEntry.getType()).thenReturn(PaymentTransactionType.NOTIFY);

        when(successOkEntry.getAmount()).thenReturn(new BigDecimal("28.55"));
        when(successOkEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860d");
        when(successOkEntry.getEncryptedTransaction()).thenReturn("test");
        when(successOkEntry.getTransId()).thenReturn("8877881982980_success");
        when(successOkEntry.getTransactionStatus()).thenReturn("OK");
        when(successOkEntry.getType()).thenReturn(PaymentTransactionType.FAILED_RESPONSE);

        when(successAuthorizedEntry.getAmount()).thenReturn(new BigDecimal("28.55"));
        when(successAuthorizedEntry.getPayId()).thenReturn("2006832aa5ab46df8f4bc0afc2c6860d");
        when(successAuthorizedEntry.getEncryptedTransaction()).thenReturn("test");
        when(successAuthorizedEntry.getTransId()).thenReturn("8877881982980_success");
        when(successAuthorizedEntry.getTransactionStatus()).thenReturn("OK");
        when(successAuthorizedEntry.getType()).thenReturn(PaymentTransactionType.FAILED_RESPONSE);
    }

    @Test
    public void testGetEvoPayIDWhenTransactionOk() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successOkNotifyEntry.getPayId()).isEqualTo(PaymentUtils.getEvoPayID(order));
    }

    @Test
    public void testGetEvoPayIDAuthorized() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successAuthorizedNotifyEntry, successAuthorizedEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successAuthorizedNotifyEntry.getPayId()).isEqualTo(PaymentUtils.getEvoPayID(order));
    }

    @Test
    public void testGetEvoPayIDReverseEntryOrder() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(successfulPaymentTransaction, failedPaymentTransaction));
        assertThat(successOkNotifyEntry.getPayId()).isEqualTo(PaymentUtils.getEvoPayID(order));
    }

    @Test
    public void testGetEvoRefIDWhenTransactionOk() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(refNr).isEqualTo(PaymentUtils.getEvoRefID(order));
    }

    @Test
    public void testGetEvoRefIDWhenTransactionAuthorized() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successAuthorizedNotifyEntry, successAuthorizedEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(refNr).isEqualTo(PaymentUtils.getEvoRefID(order));
    }

    @Test
    public void testGetEvoRefIDReverseEntryOrder() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(successfulPaymentTransaction, failedPaymentTransaction));
        assertThat(refNr).isEqualTo(PaymentUtils.getEvoRefID(order));
    }


    @Test
    public void testGetBillingTokenWhenTransactionOk() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successOkNotifyEntry.getTransId()).isEqualTo(PaymentUtils.getBillingToken(order));
    }

    @Test
    public void testGetBillingTokenWhenTransactionAuthorized() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successAuthorizedNotifyEntry, successAuthorizedEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successAuthorizedNotifyEntry.getTransId()).isEqualTo(PaymentUtils.getBillingToken(order));
    }

    @Test
    public void testGetBillingTokenReverseEntryOrder() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(successfulPaymentTransaction, failedPaymentTransaction));
        assertThat(successOkNotifyEntry.getTransId()).isEqualTo(PaymentUtils.getBillingToken(order));
    }


    @Test
    public void testGetAuthorizedAmountWhenTransactionOk() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successOkNotifyEntry.getAmount().doubleValue()).isEqualTo(PaymentUtils.getAuthorizedAmount(order));
    }

    @Test
    public void testGetAuthorizedAmountWhenTransactionAuthorized() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successAuthorizedNotifyEntry, successAuthorizedEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(failedPaymentTransaction, successfulPaymentTransaction));
        assertThat(successAuthorizedNotifyEntry.getAmount().doubleValue()).isEqualTo(PaymentUtils.getAuthorizedAmount(order));
    }

    @Test
    public void testGetAuthorizedAmountReverseEntryOrder() {
        when(failedPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, failedNotifyEntry, failureEntry));
        when(successfulPaymentTransaction.getEntries()).thenReturn(Arrays.asList(requestEntry, successOkNotifyEntry, successOkEntry));

        when(order.getPaymentTransactions()).thenReturn(Arrays.asList(successfulPaymentTransaction, failedPaymentTransaction));
        assertThat(successOkNotifyEntry.getAmount().doubleValue()).isEqualTo(PaymentUtils.getAuthorizedAmount(order));
    }

    @Test
    public void testIsCorrectPaymentTransactionSuccessWhenTransactionOk() {
        assertThat(PaymentUtils.isCorrectPaymentTransaction(successOkNotifyEntry)).isTrue();
    }

    @Test
    public void testIsCorrectPaymentTransactionWhenTransactionFailed() {
        assertThat(PaymentUtils.isCorrectPaymentTransaction(failedNotifyEntry)).isFalse();
    }

    @Test
    public void testIsCorrectPaymentTransactionSuccessWhenTransactionAuthorized() {
        assertThat(PaymentUtils.isCorrectPaymentTransaction(successAuthorizedEntry)).isTrue();
    }
}
