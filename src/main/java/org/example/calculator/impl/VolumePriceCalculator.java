package org.example.calculator.impl;

import org.example.calculator.intf.PriceModelCalculator;
import org.example.pojos.PriceTier;
import java.math.BigDecimal;

/**
 * Strategy for volume pricing model.
 */
public class VolumePriceCalculator implements PriceModelCalculator {
    @Override
    public BigDecimal calculatePrice(PriceTier tier, int quantity) {
        return tier.getPriceValue().multiply(BigDecimal.valueOf(quantity));
    }
}