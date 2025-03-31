package org.example;

import org.example.calculator.PriceCalculator;
import org.example.enums.CurrencyEnum;
import org.example.enums.PriceModel;
import org.example.pojos.PriceConfig;
import org.example.pojos.PriceTier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @Test
    @DisplayName("End-to-end test for price calculation with mixed pricing models")
    void endToEndTestForPriceCalculationWithMixedPricingModels() throws Exception {
        // Arrange - Setup the price configuration
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        priceConfig.addPriceTier(new PriceTier(11, 20, BigDecimal.valueOf(150.00), PriceModel.VOLUME));
        priceConfig.addPriceTier(new PriceTier(21, 30, BigDecimal.valueOf(250.00), PriceModel.VOLUME));
        
        PriceCalculator priceCalculator = new PriceCalculator();
        
        // Act - Calculate prices for different quantities
        BigDecimal price1 = priceCalculator.calculatePrice(priceConfig, 5);
        BigDecimal price2 = priceCalculator.calculatePrice(priceConfig, 10);
        BigDecimal price3 = priceCalculator.calculatePrice(priceConfig, 15);
        BigDecimal price4 = priceCalculator.calculatePrice(priceConfig, 25);
        
        // Assert - Verify the calculated prices
        assertEquals(BigDecimal.valueOf(100.00), price1);
        assertEquals(BigDecimal.valueOf(100.00), price2);
        assertEquals(BigDecimal.valueOf(2250.00), price3); // 15 * 150.00
        assertEquals(BigDecimal.valueOf(6250.00), price4); // 25 * 250.00
        
        // Verify the output formatting with currency
        assertEquals("USD " + BigDecimal.valueOf(100.00), CurrencyEnum.USD + " " + price1);
        assertEquals("USD " + BigDecimal.valueOf(6250.00), CurrencyEnum.USD + " " + price4);
    }
    
    @Test
    @DisplayName("End-to-end test for graduated pricing model")
    void endToEndTestForGraduatedPricingModel() throws Exception {
        // Arrange - Setup the price configuration with graduated pricing
        PriceConfig priceConfig = new PriceConfig("p2");
        priceConfig.addPriceTier(new PriceTier(1, 5, BigDecimal.valueOf(10.00), PriceModel.GRADUATED));
        priceConfig.addPriceTier(new PriceTier(6, 15, BigDecimal.valueOf(8.00), PriceModel.GRADUATED));
        priceConfig.addPriceTier(new PriceTier(16, 25, BigDecimal.valueOf(6.00), PriceModel.GRADUATED));
        
        PriceCalculator priceCalculator = new PriceCalculator();
        
        // Act - Calculate price for a quantity that spans multiple tiers
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 20);
        
        // Assert - Verify the calculated price
        // First 5 units at 10.00 each = 50.00
        // Next 10 units at 8.00 each = 80.00
        // Next 5 units at 6.00 each = 30.00
        // Total = 160.00
        assertEquals(BigDecimal.valueOf(160.00), price);
    }
    
    @Test
    @DisplayName("End-to-end test for error handling")
    void endToEndTestForErrorHandling() {
        // Arrange - Setup the price configuration
        PriceConfig priceConfig = new PriceConfig("p3");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        
        PriceCalculator priceCalculator = new PriceCalculator();
        
        // Act & Assert - Verify error handling for invalid quantity
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> 
            priceCalculator.calculatePrice(priceConfig, -1)
        );
        assertTrue(exception1.getMessage().contains("Quantity cannot be negative"));
        
        // Act & Assert - Verify error handling for quantity outside of tier range
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> 
            priceCalculator.calculatePrice(priceConfig, 15)
        );
        assertTrue(exception2.getMessage().contains("Quantity exceeds maximum tier range."));
    }
}