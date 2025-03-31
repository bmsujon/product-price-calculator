package org.example.enums;

/**
 * Enum representing currency types with their associated symbols.
 */
public enum CurrencyEnum {
    USD("$"),
    EUR("€"),
    GBP("£"),
    JPY("¥"),
    CNY("¥"),
    INR("₹");

    private final String currencySymbol;

    /**
     * Constructor for currency enum.
     * 
     * @param symbol The currency symbol
     */
    CurrencyEnum(String symbol) {
        this.currencySymbol = symbol;
    }

    /**
     * Returns the currency symbol for this currency.
     * 
     * @return The currency symbol
     */
    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
