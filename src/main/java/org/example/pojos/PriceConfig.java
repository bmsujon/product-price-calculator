package org.example.pojos;

import org.example.enums.PriceModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PriceConfig {

    private String productId;
    private List<PriceTier> priceTiers;

    public PriceConfig(String productId) {
        this(productId, new ArrayList<>());
    }

    public PriceConfig(String productId, List<PriceTier> priceTiers) {
        this.productId = validateProductId(productId);
        this.priceTiers = new ArrayList<>(priceTiers); // Create a copy to prevent external modification

        sortAndValidateTiers();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = validateProductId(productId);
    }

    public List<PriceTier> getPriceTiers() {
        return Collections.unmodifiableList(priceTiers); // Return an unmodifiable list
    }

    public void setPriceTiers(List<PriceTier> priceTiers) {
        this.priceTiers = new ArrayList<>(priceTiers);
        sortAndValidateTiers();
    }

    public void addPriceTier(PriceTier priceTier) {
        Objects.requireNonNull(priceTier, "priceTier can't be null");
        priceTiers.add(priceTier);
        sortAndValidateTiers();
    }

    public void removePriceTier(PriceTier priceTier) {
        Objects.requireNonNull(priceTier, "priceTier can't be null");
        if (!priceTiers.contains(priceTier)) {
            throw new IllegalArgumentException("Price tier not found.");
        }
        if (priceTiers.size() <= 1) {
            throw new IllegalStateException("Cannot remove the last tier.");
        }

        priceTiers.remove(priceTier);
        sortAndValidateTiers();
    }

    private String validateProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId can't be null or empty");
        }
        return productId;
    }


    private void sortAndValidateTiers() {
        if(priceTiers.isEmpty())
            return;
        if(priceTiers.size() > 1)
            priceTiers.sort(Comparator.comparingInt(PriceTier::getFrom));
        validateTiers();
    }

    private void validateTiers() {
        if (priceTiers.isEmpty()) {
            throw new IllegalArgumentException("priceTiers can't be empty");
        }

        boolean hasGraduated = priceTiers.get(0).getPriceModel() == PriceModel.GRADUATED;

        for (int i = 0; i < priceTiers.size(); i++) {
            PriceTier tier = priceTiers.get(i);
            if (tier.getPriceModel() == PriceModel.GRADUATED != hasGraduated) {
                throw new IllegalArgumentException("All price models must be the same (either GRADUATED or non-GRADUATED)");
            }

            if (i > 0) {
                PriceTier prevTier = priceTiers.get(i - 1);
                if (tier.getFrom() != prevTier.getTo() + 1) {
                    throw new IllegalArgumentException("Price tiers must be contiguous and non-overlapping.");
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceConfig that = (PriceConfig) o;
        return Objects.equals(productId, that.productId) && Objects.equals(priceTiers, that.priceTiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, priceTiers);
    }
}