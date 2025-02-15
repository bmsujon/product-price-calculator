package org.example;

import org.example.enums.PriceModel;
import org.example.pojos.PriceConfig;
import org.example.pojos.PriceTier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;

public class PriceCalculator {

    public BigDecimal calculatePrice(PriceConfig priceConfig, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        List<PriceTier> tiers = priceConfig.getPriceTiers();
        int minRange = tiers.get(0).getFrom();
        if(quantity < minRange) {
            throw new IllegalArgumentException("Quantity is below the available min range.");
        }
        int maxRange = tiers.get(tiers.size() - 1).getTo();
        if (quantity > maxRange) {
            throw new IllegalArgumentException("Quantity exceeds maximum tier range.");
        }



        if (tiers.get(0).getPriceModel() == PriceModel.GRADUATED) {
            return calculateGraduatedPrice(tiers, quantity);
        } else {
            return calculateNonGraduatedPrice(tiers, quantity);
        }
    }

    private BigDecimal calculateGraduatedPrice(List<PriceTier> tiers, int quantity) {
        BigDecimal total = BigDecimal.ZERO;
        int remaining = quantity;

        for (int i = 0; i < tiers.size(); i++) {
            if (remaining <= 0) break;
            PriceTier tier = tiers.get(i);
            int tierQuantity = Math.min(remaining, tier.getTo() - tier.getFrom() + 1);
            //Special case to handle
            if(i == 0 && tier.getFrom() != 1) { // that means it starts either with 0 or greater than 1.
                tierQuantity = Math.min(remaining, tier.getTo());
            }

            total = total.add(tier.getPriceValue().multiply(BigDecimal.valueOf(tierQuantity)));
            remaining -= tierQuantity;
        }
        return total;
    }

    private BigDecimal calculateNonGraduatedPrice(List<PriceTier> tiers, int quantity) {
        PriceTier tier = findApplicableTier(tiers, quantity);
        switch (tier.getPriceModel()) {
            case FLAT: return tier.getPriceValue();
            case VOLUME: return tier.getPriceValue().multiply(BigDecimal.valueOf(quantity));
            default: throw new IllegalArgumentException("Unsupported pricing model.");
        }
    }

    private PriceTier findApplicableTier(List<PriceTier> tiers, int quantity) {
        int low = 0, high = tiers.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            PriceTier midTier = tiers.get(mid);
            if (quantity < midTier.getFrom()) {
                high = mid - 1;
            } else if (quantity > midTier.getTo()) {
                low = mid + 1;
            } else {
                return midTier;  // Found the tier within the range
            }
        }

        // If no tier is found within the valid range, throw an exception
        throw new IllegalArgumentException("No applicable tier found for the given quantity.");
    }
}
