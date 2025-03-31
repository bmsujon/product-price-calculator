package org.example.calculator.intf;

import org.example.pojos.PriceTier;
import java.math.BigDecimal;

/**
 * Strategy interface for different pricing models.
 */
public interface PriceModelCalculator {
    /**
     * Calculates the price based on the pricing model.
     *
     * @param tier The price tier containing pricing information
     * @param quantity The quantity of items
     * @return The calculated price
     */
    BigDecimal calculatePrice(PriceTier tier, int quantity);
}