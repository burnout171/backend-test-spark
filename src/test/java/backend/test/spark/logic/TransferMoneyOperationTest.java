package backend.test.spark.logic;

import backend.test.spark.ApiClient;
import backend.test.spark.Application;
import backend.test.spark.controller.AccountController;
import backend.test.spark.dao.AccountDao;
import backend.test.spark.dao.AccountDaoAdapter;
import backend.test.spark.model.Account;
import backend.test.spark.model.MoneyTransferRequest;
import backend.test.spark.model.Response;
import backend.test.spark.util.JsonUtils;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import spark.Spark;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static backend.test.spark.DataCreator.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferMoneyOperationTest {

    private final JsonUtils jsonUtils = new JsonUtils();
    private final ApiClient apiClient = new ApiClient("http://localhost:4567");

    private AccountDao accountDao;

    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        AccountDaoAdapter accountDaoAdapter = new AccountDaoAdapter(accountDao);
        TransferMoneyOperation transferMoneyOperation = new TransferMoneyOperation(jsonUtils, accountDaoAdapter);
        AccountController accountController = new AccountController(transferMoneyOperation);
        Application.init(accountController);
        Spark.awaitInitialization();
    }

    @AfterEach
    void tearDown() {
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    void successfulTransfer() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest();
        Account fromAccount = givenAccount(FROM_ID, AMOUNT);
        Account toAccount = givenAccount(TO_ID, AMOUNT);
        when(accountDao.getConnection()).thenReturn(mock(Connection.class));
        when(accountDao.getAccount(any(), eq(FROM_ID))).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(any(), eq(TO_ID))).thenReturn(Optional.of(toAccount));

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertTrue(actualResponse.getSuccess());
        ArgumentCaptor<Account> capture = ArgumentCaptor.forClass(Account.class);
        verify(accountDao).update(any(), capture.capture());
        List<Account> accounts = capture.getAllValues();
        assertAll(
                () -> assertEquals(2, accounts.size()),
                () -> assertEquals(FROM_ID, accounts.get(0).getId()),
                () -> assertEquals(BigDecimal.ZERO, accounts.get(0).getBalance()),
                () -> assertEquals(TO_ID, accounts.get(1).getId()),
                () -> assertEquals(AMOUNT.add(AMOUNT), accounts.get(1).getBalance())
        );
    }

    @Test
    void notEnoughMoney() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest();
        Account fromAccount = givenAccount(FROM_ID, AMOUNT.subtract(BigDecimal.ONE));
        Account toAccount = givenAccount(TO_ID, AMOUNT);
        when(accountDao.getAccount(any(), eq(FROM_ID))).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(any(), eq(TO_ID))).thenReturn(Optional.of(toAccount));

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertAll(
                () -> assertFalse(actualResponse.getSuccess()),
                () -> assertEquals("Not enough money!", actualResponse.getMessage())
        );
        verify(accountDao, never()).update(any());
    }

    @Test
    void chargeNegativeAmount() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest(AMOUNT.negate());
        Account fromAccount = givenAccount(FROM_ID, AMOUNT);
        Account toAccount = givenAccount(TO_ID, AMOUNT);
        when(accountDao.getAccount(any(), eq(FROM_ID))).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(any(), eq(TO_ID))).thenReturn(Optional.of(toAccount));

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertAll(
                () -> assertFalse(actualResponse.getSuccess()),
                () -> assertEquals(format("Not possible to charge negative value: %f", AMOUNT.negate()), actualResponse.getMessage())
        );
        verify(accountDao, never()).update(any());
    }

    @Test
    void transferWithinSameAccount() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest();
        givenMoneyTransferRequest.setTo(FROM_ID);

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertAll(
                () -> assertFalse(actualResponse.getSuccess()),
                () -> assertEquals("Charge operation under one account. Stop processing", actualResponse.getMessage())
        );
        verify(accountDao, never()).update(any(), any());
    }

    @Test
    void fromAccountNotFound() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest();
        Account toAccount = givenAccount(TO_ID, AMOUNT);
        when(accountDao.getAccount(any(), eq(FROM_ID))).thenReturn(Optional.empty());
        when(accountDao.getAccount(any(), eq(TO_ID))).thenReturn(Optional.of(toAccount));

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertAll(
                () -> assertFalse(actualResponse.getSuccess()),
                () -> assertEquals(format("Account %d not found", FROM_ID), actualResponse.getMessage())
        );
        verify(accountDao, never()).update(any());
    }


    @Test
    void toAccountNotFound() throws SQLException {
        MoneyTransferRequest givenMoneyTransferRequest = givenMoneyTransferRequest();
        Account fromAccount = givenAccount(FROM_ID, AMOUNT);
        when(accountDao.getAccount(any(), eq(FROM_ID))).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(any(), eq(TO_ID))).thenReturn(Optional.empty());

        String actualRawResponse = apiClient.request(
                HttpMethod.POST.asString(), "/accounts/transfer", jsonUtils.toJson(givenMoneyTransferRequest));
        Response actualResponse = jsonUtils.fromJson(actualRawResponse, Response.class);

        assertAll(
                () -> assertFalse(actualResponse.getSuccess()),
                () -> assertEquals(format("Account %d not found", TO_ID), actualResponse.getMessage())
        );
        verify(accountDao, never()).update(any(), any());
    }
}