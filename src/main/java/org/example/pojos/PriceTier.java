package org.example.pojos;

import org.example.enums.PriceModel;
import java.math.BigDecimal;
import java.util.Objects;

public class PriceTier {
    private int from;
    private int to;
    private BigDecimal priceValue;  // Change from double to BigDecimal
    private PriceModel priceModel;

    public PriceTier(int from, int to, BigDecimal priceValue, PriceModel priceModel) {
        if (from > to || from < 0) {
            throw new IllegalArgumentException("Invalid tier range.");
        }
        this.from = from;
        this.to = to;
        this.priceValue = priceValue;
        this.priceModel = priceModel;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public BigDecimal getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(BigDecimal priceValue) {
        this.priceValue = priceValue;
    }

    public PriceModel getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(PriceModel priceModel) {
        this.priceModel = priceModel;
    }

    public boolean validQuantityForThisRange(int quantity) {
        return quantity >= this.from && quantity <= this.to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PriceTier that = (PriceTier) obj;
        return this.from == that.from && this.to == that.to && this.priceValue.equals(that.priceValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, priceValue);
    }

}
