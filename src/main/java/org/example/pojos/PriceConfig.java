package org.example.pojos;

import org.example.enums.PriceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PriceConfig {

    private String productId;
    private List<PriceTier> priceTiers;

    // Constructor for bulk creation of priceTiers
    public PriceConfig(String product, List<PriceTier> priceTiers) {
        if (product == null || product.isEmpty()) {
            throw new IllegalArgumentException("productId can't be null or empty");
        }
        this.productId = product;
        if (priceTiers == null || priceTiers.isEmpty()) {
            throw new IllegalArgumentException("priceTiers can't be empty");
        }
        this.priceTiers = new ArrayList<>(priceTiers);
        Collections.sort(this.priceTiers, Comparator.comparingInt(pT -> pT.getFrom()));
        validatePriceTiers();
    }

    // Constructor for adding priceTiers one at a time
    public PriceConfig(String product) {
        if (product == null || product.isEmpty()) {
            throw new IllegalArgumentException("productId can't be null or empty");
        }
        this.productId = product;
        this.priceTiers = new ArrayList<>();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<PriceTier> getPriceTiers() {
        return priceTiers;
    }

    public void setPriceTiers(List<PriceTier> priceTiers) {
        this.priceTiers = priceTiers;
        Collections.sort(this.priceTiers, Comparator.comparingInt(pT -> pT.getFrom()));
        validatePriceTiers();
    }

    public void addPriceTier(PriceTier priceTier) {
        int i = 0;
        while (i < priceTiers.size() && priceTiers.get(i).getFrom() < priceTier.getFrom()) {
            i++;
        }
        priceTiers.add(i, priceTier);
        validatePriceTiers();
    }

    public void removePriceTier(PriceTier priceTier) {
        if (priceTier == null) {
            throw new IllegalArgumentException("PriceTier to remove cannot be null.");
        }

        // Check if the PriceTier exists in the list before attempting to remove it
        if (!priceTiers.contains(priceTier)) {
            throw new IllegalArgumentException("PriceTier does not exist in the configuration.");
        }
        // Remove the PriceTier from the list if it's in first or last position
        if(priceTiers.getFirst().equals(priceTier) || priceTiers.getLast().equals(priceTier))
            priceTiers.remove(priceTier);
        else {
            throw new IllegalArgumentException("You can remove only from first or last position");
        }
    }


    public void validatePriceTiers() {
        if (priceTiers.isEmpty()) {
            throw new IllegalArgumentException("priceTiers can't be empty");
        }

        boolean hasGraduated = priceTiers.get(0).getPriceModel() == PriceModel.GRADUATED;
        for (PriceTier tier : priceTiers) {
            if (tier.getPriceModel() == PriceModel.GRADUATED != hasGraduated) {
                throw new IllegalArgumentException("You can't have graduated and other price models together");
            }
        }

        PriceTier prev = priceTiers.get(0);
        for (int i = 1; i < priceTiers.size(); i++) {
            PriceTier curr = priceTiers.get(i);
            if (prev.getTo() >= curr.getFrom() || curr.getFrom() != prev.getTo() + 1) {
                throw new IllegalArgumentException("Invalid price configuration. Please check that ranges are contiguous and non-overlapping");
            }
            prev = curr;
        }
    }
}
