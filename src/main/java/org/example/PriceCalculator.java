package org.example;

import org.example.enums.PriceModel;
import org.example.pojos.PriceConfig;
import org.example.pojos.PriceTier;

import java.math.BigDecimal;
import java.util.List;

/**
 * Calculates prices based on provided price configurations and quantities.
 */
public class PriceCalculator {

    /**
     * Calculates the price for a given quantity based on the provided price configuration.
     *
     * @param priceConfig The price configuration containing the price tiers.
     * @param quantity    The quantity for which to calculate the price.
     * @return The calculated price as a BigDecimal.
     * @throws IllegalArgumentException If the quantity is invalid or outside the defined price tier ranges,
     *                                  or if the priceConfig is invalid.
     */
    public BigDecimal calculatePrice(PriceConfig priceConfig, int quantity) {
        validateQuantity(quantity);
        validatePriceConfig(priceConfig);

        List<PriceTier> tiers = priceConfig.getPriceTiers();
        validateQuantityRange(tiers, quantity);

        if (tiers.get(0).getPriceModel() == PriceModel.GRADUATED) {
            return calculateGraduatedPrice(tiers, quantity);
        } else {
            return calculateNonGraduatedPrice(tiers, quantity);
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    private void validatePriceConfig(PriceConfig priceConfig) {
        if (priceConfig == null) {
            throw new IllegalArgumentException("Price configuration cannot be null.");
        }
    }

    private void validateQuantityRange(List<PriceTier> tiers, int quantity) {
        int minRange = tiers.get(0).getFrom();
        if (quantity < minRange) {
            throw new IllegalArgumentException("Quantity is below the available min range.");
        }
        int maxRange = tiers.get(tiers.size() - 1).getTo();
        if (quantity > maxRange) {
            throw new IllegalArgumentException("Quantity exceeds maximum tier range.");
        }
    }


    private BigDecimal calculateGraduatedPrice(List<PriceTier> tiers, int quantity) {
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

    private BigDecimal calculateNonGraduatedPrice(List<PriceTier> tiers, int quantity) {
        PriceTier tier = findApplicableTier(tiers, quantity);
        return switch (tier.getPriceModel()) {
            case FLAT -> tier.getPriceValue();
            case VOLUME -> tier.getPriceValue().multiply(BigDecimal.valueOf(quantity));
            default -> throw new IllegalArgumentException("Unsupported pricing model.");
        };
    }

    public PriceTier findApplicableTier(List<PriceTier> tiers, int quantity) {
        // Use binary search for efficiency.
        int low = 0;
        int high = tiers.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1; // Unsigned right shift for better average performance.
            PriceTier midTier = tiers.get(mid);

            if (quantity < midTier.getFrom()) {
                high = mid - 1;
            } else if (quantity > midTier.getTo()) {
                low = mid + 1;
            } else {
                return midTier; // Found the tier.
            }
        }

        throw new IllegalArgumentException("No applicable tier found for the given quantity.");
    }
}