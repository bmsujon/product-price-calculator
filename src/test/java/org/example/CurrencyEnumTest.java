package org.example;

import org.example.enums.CurrencyEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyEnumTest {

    @Test
    @DisplayName("CurrencyEnum USD should have correct symbol")
    void usdShouldHaveCorrectSymbol() {
        assertEquals("$", CurrencyEnum.USD.getCurrencySymbol());
    }

    @Test
    @DisplayName("CurrencyEnum values should be accessible")
    void currencyEnumValuesShouldBeAccessible() {
        CurrencyEnum[] currencies = CurrencyEnum.values();
        assertTrue(currencies.length > 0);
        assertEquals(CurrencyEnum.USD, currencies[0]);
    }

    @Test
    @DisplayName("CurrencyEnum valueOf should return correct enum")
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(CurrencyEnum.USD, CurrencyEnum.valueOf("USD"));
    }

    @Test
    @DisplayName("CurrencyEnum valueOf should throw exception for invalid name")
    void valueOfShouldThrowExceptionForInvalidName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                CurrencyEnum.valueOf("INVALID")
        );

        assertTrue(exception.getMessage().contains("No enum constant"));
    }

    @Test
    @DisplayName("All currencies should have non-null symbols")
    void allCurrenciesShouldHaveNonNullSymbols() {
        for (CurrencyEnum currency : CurrencyEnum.values()) {
            assertNotNull(currency.getCurrencySymbol(),
                    "Currency " + currency.name() + " should have a non-null symbol");
        }
    }

    @Test
    @DisplayName("Each currency should have the correct symbol")
    void eachCurrencyShouldHaveCorrectSymbol() {
        assertEquals("$", CurrencyEnum.USD.getCurrencySymbol());
        assertEquals("€", CurrencyEnum.EUR.getCurrencySymbol());
        assertEquals("£", CurrencyEnum.GBP.getCurrencySymbol());
        assertEquals("¥", CurrencyEnum.JPY.getCurrencySymbol());
        assertEquals("¥", CurrencyEnum.CNY.getCurrencySymbol());
        assertEquals("₹", CurrencyEnum.INR.getCurrencySymbol());
    }
}