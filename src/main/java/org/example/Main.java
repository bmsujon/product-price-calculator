package org.example;

import org.example.enums.CurrencyEnum;
import org.example.enums.PriceModel;
import org.example.pojos.PriceConfig;
import org.example.pojos.PriceTier;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        PriceConfig priceConfig = new PriceConfig("p1");

        priceConfig.addPriceTier(new PriceTier(11, 20, BigDecimal.valueOf(150.00), PriceModel.VOLUME));
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        priceConfig.addPriceTier(new PriceTier(21, 30, BigDecimal.valueOf(250.00), PriceModel.VOLUME));

        PriceCalculator priceCalculator = new PriceCalculator();

        try {
            System.out.println("Total Cost for 10 units: " + CurrencyEnum.USD + " " + priceCalculator.calculatePrice(priceConfig, 10));
            System.out.println("Total Cost for 18 units: " + CurrencyEnum.USD + " " + priceCalculator.calculatePrice(priceConfig, 18));
            System.out.println("Total Cost for 25 units: " + CurrencyEnum.USD + " " + priceCalculator.calculatePrice(priceConfig, 25));
        } catch (Exception exception) {
            System.out.println("Error occurred: " + exception.getMessage());
        }
    }
}