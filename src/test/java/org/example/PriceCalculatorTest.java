package org.example;

import org.example.calculator.PriceCalculator;
import org.example.enums.PriceModel;
import org.example.pojos.PriceConfig;
import org.example.pojos.PriceTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    private PriceCalculator priceCalculator;

    @BeforeEach
    void setUp() {
        priceCalculator = new PriceCalculator();
    }

    @Test
    @DisplayName("calculatePrice should return correct price for FLAT model")
    void calculatePriceShouldReturnCorrectPriceForFlatModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        
        // Act
        BigDecimal result = priceCalculator.calculatePrice(priceConfig, 5);
        
        // Assert
        assertEquals(BigDecimal.valueOf(100.00), result);
    }
    
    @Test
    @DisplayName("calculatePrice should return correct price for VOLUME model")
    void calculatePriceShouldReturnCorrectPriceForVolumeModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(10.00), PriceModel.VOLUME));
        
        // Act
        BigDecimal result = priceCalculator.calculatePrice(priceConfig, 5);
        
        // Assert
        assertEquals(BigDecimal.valueOf(50.00), result); // 5 * 10.00 = 50.00
    }
    
    @Test
    @DisplayName("calculatePrice should return correct price for GRADUATED model")
    void calculatePriceShouldReturnCorrectPriceForGraduatedModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 5, BigDecimal.valueOf(10.00), PriceModel.GRADUATED));
        priceConfig.addPriceTier(new PriceTier(6, 10, BigDecimal.valueOf(8.00), PriceModel.GRADUATED));
        
        // Act
        BigDecimal result = priceCalculator.calculatePrice(priceConfig, 8);
        
        // Assert
        // First 5 units at 10.00 each = 50.00
        // Next 3 units at 8.00 each = 24.00
        // Total = 74.00
        assertEquals(BigDecimal.valueOf(74.00), result);
    }
    
    @Test
    @DisplayName("calculatePrice should handle multiple tiers with FLAT model")
    void calculatePriceShouldHandleMultipleTiersWithFlatModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        priceConfig.addPriceTier(new PriceTier(11, 20, BigDecimal.valueOf(150.00), PriceModel.FLAT));
        priceConfig.addPriceTier(new PriceTier(21, 30, BigDecimal.valueOf(200.00), PriceModel.FLAT));
        
        // Act & Assert
        assertEquals(BigDecimal.valueOf(100.00), priceCalculator.calculatePrice(priceConfig, 5));
        assertEquals(BigDecimal.valueOf(100.00), priceCalculator.calculatePrice(priceConfig, 10));
        assertEquals(BigDecimal.valueOf(150.00), priceCalculator.calculatePrice(priceConfig, 15));
        assertEquals(BigDecimal.valueOf(150.00), priceCalculator.calculatePrice(priceConfig, 20));
        assertEquals(BigDecimal.valueOf(200.00), priceCalculator.calculatePrice(priceConfig, 25));
    }
    
    @Test
    @DisplayName("calculatePrice should handle multiple tiers with VOLUME model")
    void calculatePriceShouldHandleMultipleTiersWithVolumeModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(10.00), PriceModel.VOLUME));
        priceConfig.addPriceTier(new PriceTier(11, 20, BigDecimal.valueOf(9.00), PriceModel.VOLUME));
        priceConfig.addPriceTier(new PriceTier(21, 30, BigDecimal.valueOf(8.00), PriceModel.VOLUME));
        
        // Act & Assert
        assertEquals(BigDecimal.valueOf(50.00), priceCalculator.calculatePrice(priceConfig, 5)); // 5 * 10.00
        assertEquals(BigDecimal.valueOf(100.00), priceCalculator.calculatePrice(priceConfig, 10)); // 10 * 10.00
        assertEquals(BigDecimal.valueOf(135.00), priceCalculator.calculatePrice(priceConfig, 15)); // 15 * 9.00
        assertEquals(BigDecimal.valueOf(180.00), priceCalculator.calculatePrice(priceConfig, 20)); // 20 * 9.00
        assertEquals(BigDecimal.valueOf(200.00), priceCalculator.calculatePrice(priceConfig, 25)); // 25 * 8.00
    }
    
    @Test
    @DisplayName("calculatePrice should handle multiple tiers with GRADUATED model")
    void calculatePriceShouldHandleMultipleTiersWithGraduatedModel() throws Exception {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 5, BigDecimal.valueOf(10.00), PriceModel.GRADUATED));
        priceConfig.addPriceTier(new PriceTier(6, 10, BigDecimal.valueOf(8.00), PriceModel.GRADUATED));
        priceConfig.addPriceTier(new PriceTier(11, 15, BigDecimal.valueOf(6.00), PriceModel.GRADUATED));
        
        // Act & Assert
        // 3 units at 10.00 each = 30.00
        assertEquals(BigDecimal.valueOf(30.00), priceCalculator.calculatePrice(priceConfig, 3));
        
        // 5 units at 10.00 each = 50.00
        assertEquals(BigDecimal.valueOf(50.00), priceCalculator.calculatePrice(priceConfig, 5));
        
        // 5 units at 10.00 each = 50.00
        // 3 units at 8.00 each = 24.00
        // Total = 74.00
        assertEquals(BigDecimal.valueOf(74.00), priceCalculator.calculatePrice(priceConfig, 8));
        
        // 5 units at 10.00 each = 50.00
        // 5 units at 8.00 each = 40.00
        // Total = 90.00
        assertEquals(BigDecimal.valueOf(90.00), priceCalculator.calculatePrice(priceConfig, 10));
        
        // 5 units at 10.00 each = 50.00
        // 5 units at 8.00 each = 40.00
        // 3 units at 6.00 each = 18.00
        // Total = 108.00
        assertEquals(BigDecimal.valueOf(108.00), priceCalculator.calculatePrice(priceConfig, 13));
    }
    
//    @Test
//    @DisplayName("calculatePrice should throw exception when quantity is zero")
//    void calculatePriceShouldThrowExceptionWhenQuantityIsZero() {
//        // Arrange
//        PriceConfig priceConfig = new PriceConfig("p1");
//        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//            priceCalculator.calculatePrice(priceConfig, 0)
//        );
//
//        assertTrue(exception.getMessage().contains("Quantity must be positive"));
//    }
    
    @Test
    @DisplayName("calculatePrice should throw exception when quantity is negative")
    void calculatePriceShouldThrowExceptionWhenQuantityIsNegative() {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            priceCalculator.calculatePrice(priceConfig, -5)
        );
        
        assertTrue(exception.getMessage().contains("Quantity cannot be negative."));
    }
    
    @Test
    @DisplayName("calculatePrice should throw exception when priceConfig is null")
    void calculatePriceShouldThrowExceptionWhenPriceConfigIsNull() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            priceCalculator.calculatePrice(null, 5)
        );
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("priceConfig cannot be null"));
    }
    
    @Test
    @DisplayName("calculatePrice should throw exception when quantity exceeds maximum tier")
    void calculatePriceShouldThrowExceptionWhenQuantityExceedsMaximumTier() {
        // Arrange
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            priceCalculator.calculatePrice(priceConfig, 15)
        );
        System.out.println(exception.getMessage());
        
        assertTrue(exception.getMessage().contains("Quantity exceeds maximum tier range."));
    }
    
    @Test
    @DisplayName("calculatePrice should match the examples in Main.java")
    void calculatePriceShouldMatchExamplesInMain() throws Exception {
        // Arrange - recreate the example from Main.java
        PriceConfig priceConfig = new PriceConfig("p1");
        priceConfig.addPriceTier(new PriceTier(1, 10, BigDecimal.valueOf(100.00), PriceModel.FLAT));
        priceConfig.addPriceTier(new PriceTier(11, 20, BigDecimal.valueOf(150.00), PriceModel.VOLUME));
        priceConfig.addPriceTier(new PriceTier(21, 30, BigDecimal.valueOf(250.00), PriceModel.VOLUME));
        
        // Act & Assert
        // For 10 units: FLAT pricing at 100.00
        assertEquals(BigDecimal.valueOf(100.00), priceCalculator.calculatePrice(priceConfig, 10));
        
        // For 18 units: VOLUME pricing at 150.00 per unit = 18 * 150.00 = 2700.00
        assertEquals(BigDecimal.valueOf(2700.00), priceCalculator.calculatePrice(priceConfig, 18));
        
        // For 25 units: VOLUME pricing at 250.00 per unit = 25 * 250.00 = 6250.00
        assertEquals(BigDecimal.valueOf(6250.00), priceCalculator.calculatePrice(priceConfig, 25));
    }
}