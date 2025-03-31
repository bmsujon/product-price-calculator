package org.example.calculator.impl;

import org.example.calculator.intf.PriceModelCalculator;
import org.example.pojos.PriceTier;
import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for graduated pricing model.
 */
public class GraduatedPriceCalculator implements PriceModelCalculator {
    @Override
    public BigDecimal calculatePrice(PriceTier tier, int quantity) {
        // This method won't be used directly as graduated pricing requires all tiers
        throw new UnsupportedOperationException("Graduated pricing requires all tiers");
    }
    
    /**
     * Calculates the graduated price across multiple tiers.
     *
     * @param tiers List of price tiers
     * @param quantity The quantity of items
     * @return The calculated price
     */
    public BigDecimal calculateGraduatedPrice(List<PriceTier> tiers, int quantity) {
        BigDecimal total = BigDecimal.ZERO;
        int remaining = quantity;

        for (PriceTier tier : tiers) {
            if (remaining <= 0) break;

            int tierQuantity = Math.min(remaining, tier.getTo() - tier.getFrom() + 1);

            // Handle the edge case where the first tier doesn't start at 1.
            if (tiers.indexOf(tier) == 0 && tier.getFrom() != 1) {
                tierQuantity = Math.min(remaining, tier.getTo());
            }

            total = total.add(tier.getPriceValue().multiply(BigDecimal.valueOf(tierQuantity)));
            remaining -= tierQuantity;
        }
        return total;
    }
}