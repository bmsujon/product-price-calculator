package org.example.calculator.impl;

import org.example.calculator.intf.PriceModelCalculator;
import org.example.pojos.PriceTier;
import java.math.BigDecimal;

/**
 * Strategy for flat pricing model.
 */
public class FlatPriceCalculator implements PriceModelCalculator {
    @Override
    public BigDecimal calculatePrice(PriceTier tier, int quantity) {
        return tier.getPriceValue();
    }
}