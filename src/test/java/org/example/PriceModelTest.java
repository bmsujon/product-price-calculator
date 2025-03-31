package org.example;

import org.example.enums.PriceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PriceModelTest {

    @Test
    @DisplayName("PriceModel should have correct values")
    void priceModelShouldHaveCorrectValues() {
        assertEquals(3, PriceModel.values().length);
        assertEquals(PriceModel.FLAT, PriceModel.values()[0]);
        assertEquals(PriceModel.VOLUME, PriceModel.values()[1]);
        assertEquals(PriceModel.GRADUATED, PriceModel.values()[2]);
    }
    
    @Test
    @DisplayName("PriceModel valueOf should return correct enum")
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(PriceModel.FLAT, PriceModel.valueOf("FLAT"));
        assertEquals(PriceModel.VOLUME, PriceModel.valueOf("VOLUME"));
        assertEquals(PriceModel.GRADUATED, PriceModel.valueOf("GRADUATED"));
    }
    
    @Test
    @DisplayName("PriceModel valueOf should throw exception for invalid name")
    void valueOfShouldThrowExceptionForInvalidName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            PriceModel.valueOf("INVALID")
        );
        
        assertTrue(exception.getMessage().contains("No enum constant"));
    }
}