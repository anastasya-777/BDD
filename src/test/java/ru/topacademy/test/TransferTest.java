package ru.topacademy.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.topacademy.data.DataHelper;
import ru.topacademy.page.DashboardPage;
import ru.topacademy.page.LoginPage;
import ru.topacademy.page.VerificationPage;
import ru.topacademy.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.topacademy.data.DataHelper.*;

public class TransferTest {
    DashboardPage dashboardPage;
    @BeforeEach
    public void setUp() {
        open("http://localhost:9999/");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    public void shouldTransferMoneyOneToTwo() {
        int amount = 5000;
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;
        var transfer = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transfer.validTransfer(String.valueOf(amount), firstCard);
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        Assertions.assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        Assertions.assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    public void shouldTransferMoneyTwoToOne() {
        int amount = 10000;
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;
        var transfer = dashboardPage.selectCardToTransfer(firstCard);
        dashboardPage = transfer.validTransfer(String.valueOf(amount), secondCard);
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        Assertions.assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        Assertions.assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    public void shouldTransferMoneyOneToOne() {
        int amount = 5000;
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var transfer = dashboardPage.selectCardToTransfer(firstCard);
        transfer.validTransfer(String.valueOf(amount), firstCard);
        transfer.invalidCard();
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        Assertions.assertEquals(firstCardBalance, actualFirstCardBalance);
        Assertions.assertEquals(secondCardBalance, actualSecondCardBalance);
    }

    @Test
    public void shouldErrorTransferMoreLimit() {
        int amount = 50000;
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var transfer = dashboardPage.selectCardToTransfer(firstCard);
        transfer.validTransfer(String.valueOf(amount), secondCard);
        transfer.errorLimit();
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        Assertions.assertEquals(firstCardBalance, actualFirstCardBalance);
        Assertions.assertEquals(secondCardBalance, actualSecondCardBalance);
    }
}
