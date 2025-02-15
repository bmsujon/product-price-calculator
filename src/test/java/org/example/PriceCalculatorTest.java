package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.enums.PriceModel;
import org.example.pojos.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    private PriceCalculator priceCalculator;
    private PriceConfig priceConfig;

    @BeforeEach
    public void setUp() {
        priceCalculator = new PriceCalculator();
    }

    @Test
    public void testCalculatePriceWithGraduatedModel() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);
        priceConfig = new PriceConfig("product1", tiers);

        // 3 items in the first tier, should be 3 * 10.0 = 30.0
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 3);
        assertEquals(0, price.compareTo(new BigDecimal("30.0")));

        // 7 items, 5 in the first tier and 2 in the second tier, should be 50.0 + 16.0 = 66.0
        price = priceCalculator.calculatePrice(priceConfig, 7);
        assertEquals(0, price.compareTo(new BigDecimal("66.0")));

        // 10 items, fully in both tiers, should be 50.0 + 40.0 = 90.0
        price = priceCalculator.calculatePrice(priceConfig, 10);
        assertEquals(0, price.compareTo(new BigDecimal("90.0")));
    }

    @Test
    public void testCalculatePriceWithNonGraduatedFlatModel() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("20.0"), PriceModel.FLAT);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("15.0"), PriceModel.FLAT);
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);
        priceConfig = new PriceConfig("product2", tiers);

        // Flat rate should return the same price for any quantity in the range, let's test with 4 items (within tier1)
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 4);
        assertEquals(0, price.compareTo(new BigDecimal("20.0")));

        // Flat rate for tier2 should return 15.0, let's test with 6 items (within tier2)
        price = priceCalculator.calculatePrice(priceConfig, 6);
        assertEquals(0, price.compareTo(new BigDecimal("15.0")));
    }

    @Test
    public void testCalculatePriceWithNonGraduatedVolumeModel() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.VOLUME);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.VOLUME);
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);
        priceConfig = new PriceConfig("product3", tiers);

        // Volume rate should multiply by quantity. Testing 4 items, should be 4 * 10.0 = 40.0
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 4);
        assertEquals(0, price.compareTo(new BigDecimal("40.0")));

        // Testing 6 items in tier2, should be 6 * 8.0 = 48.0
        price = priceCalculator.calculatePrice(priceConfig, 6);
        assertEquals(0, price.compareTo(new BigDecimal("48.0")));
    }

    @Test
    public void testInvalidQuantityThrowsException() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);
        priceConfig = new PriceConfig("product4", tiers);

        // Negative quantity should throw an exception
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, -1);
        });
        assertEquals("Quantity cannot be negative.", thrown.getMessage());

        // Quantity greater than the max tier range should throw an exception
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 11);
        });
        assertEquals("Quantity exceeds maximum tier range.", thrown.getMessage());
    }

    @Test
    public void testInvalidTierRangeThrowsException() {
        // Create an invalid tier where the range overlaps
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(5, 10, new BigDecimal("8.0"), PriceModel.GRADUATED); // Overlapping range
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PriceConfig("product5", tiers); // This should throw an exception
        });
        assertEquals("Invalid price configuration. Please check that ranges are contiguous and non-overlapping", thrown.getMessage());
    }

    @Test
    public void testPriceTiersAreSorted() {
        // Check that adding tiers in non-sorted order still results in a sorted list
        PriceTier tier1 = new PriceTier(6, 10, new BigDecimal("12.0"), PriceModel.FLAT);
        PriceTier tier2 = new PriceTier(1, 5, new BigDecimal("15.0"), PriceModel.FLAT);
        priceConfig = new PriceConfig("product6");
        priceConfig.addPriceTier(tier1);
        priceConfig.addPriceTier(tier2);

        // Tiers should be sorted correctly
        assertEquals(1, priceConfig.getPriceTiers().get(0).getFrom());
        assertEquals(6, priceConfig.getPriceTiers().get(1).getFrom());
    }

    @Test
    public void testFindApplicableTier() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);
        priceConfig = new PriceConfig("product7", tiers);

        // Testing binary search functionality
        PriceTier applicableTier = priceConfig.getPriceTiers().get(0);
        assertEquals(tier1, applicableTier);

        applicableTier = priceConfig.getPriceTiers().get(1);
        assertEquals(tier2, applicableTier);
    }

    @Test
    public void testEmptyPriceTiersThrowsException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PriceConfig("product8", new ArrayList<>());
        });
        assertEquals("priceTiers can't be empty", thrown.getMessage());
    }

    @Test
    public void testEmptyProductIdThrowsException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PriceConfig("", Arrays.asList(new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED)));
        });
        assertEquals("productId can't be null or empty", thrown.getMessage());
    }

    @Test
    public void testInvalidPriceTierThrowsException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PriceTier(10, 5, new BigDecimal("15.0"), PriceModel.GRADUATED); // Invalid range
        });
        assertEquals("Invalid tier range.", thrown.getMessage());
    }

    @Test
    public void testInconsistentPriceModelsThrowsException() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("15.0"), PriceModel.FLAT); // Mixing GRADUATED and FLAT
        List<PriceTier> tiers = Arrays.asList(tier1, tier2);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PriceConfig("product9", tiers);
        });
        assertEquals("You can't have graduated and other price models together", thrown.getMessage());
    }

    @Test
    public void testSingleItemPriceTier() {
        PriceTier tier = new PriceTier(1, 1, new BigDecimal("5.0"), PriceModel.FLAT);
        List<PriceTier> tiers = Arrays.asList(tier);
        priceConfig = new PriceConfig("product10", tiers);

        // Only 1 item in the range, price should be 5.0
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 1);
        assertEquals(0, price.compareTo(new BigDecimal("5.0")));
    }

    @Test
    public void testNoApplicableTierThrowsException() {
        PriceTier tier = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        List<PriceTier> tiers = Arrays.asList(tier);
        priceConfig = new PriceConfig("product11", tiers);

        // Quantity is 6, which is not within the range of the only tier (1-5)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 6);
        });
        assertEquals("Quantity exceeds maximum tier range.", thrown.getMessage());
    }

    @Test
    public void testAddAndRemovePriceTiers() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        priceConfig = new PriceConfig("product12", Arrays.asList(tier1));

        // Adding a new tier
        priceConfig.addPriceTier(tier2);
        assertEquals(2, priceConfig.getPriceTiers().size());

        // Removing a tier
        priceConfig.removePriceTier(tier1);
        assertEquals(1, priceConfig.getPriceTiers().size());
    }


    @Test
    public void testQuantityNotInAnyRange() {
        PriceTier tier1 = new PriceTier(2, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        PriceConfig priceConfig = new PriceConfig("product1", Arrays.asList(tier1, tier2));

        // This will throw an exception since the quantity 11 does not fall into any range
        assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 11);
        }, "Quantity exceeds maximum tier range.");

        // Test case 2: Quantity 1 is less than the minimum range (tier1 starts at 2)
        assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 1);
        }, "Quantity is below the available min range.");
    }

    @Test
    public void testCalculatePriceWithValidQuantity() {
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        PriceConfig priceConfig = new PriceConfig("product1", Arrays.asList(tier1, tier2));

        // Valid quantity within the tiers (e.g., 3 falls in the first tier)
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 3);
        assertEquals(new BigDecimal("30.0"), price, "Price for 3 items should be based on tier1 rate");

        // Valid quantity that spans across tiers (e.g., 7 falls in the first and second tiers)
        // So, it will be 5*10 + 2*8 = 66
        price = priceCalculator.calculatePrice(priceConfig, 7);
        assertEquals(new BigDecimal("66.0"), price, "Price for 7 items should be based on tier1 and tier2 rates");
    }


    @Test
    public void testAllFeaturesAfterRemovingTier() {
        // Create initial price tiers with BigDecimal values
        PriceTier tier1 = new PriceTier(1, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceTier tier2 = new PriceTier(6, 10, new BigDecimal("8.0"), PriceModel.GRADUATED);
        PriceTier tier3 = new PriceTier(11, 15, new BigDecimal("6.0"), PriceModel.GRADUATED);

        // Add tiers to the price config
        priceConfig = new PriceConfig("product13", new ArrayList<>(Arrays.asList(tier1, tier2, tier3)));

        // Assert initial tier count
        assertEquals(3, priceConfig.getPriceTiers().size(), "Initial tier count should be 3");

        // Remove tier1
        priceConfig.removePriceTier(tier1);

        // Assert tier count after removal
        assertEquals(2, priceConfig.getPriceTiers().size(), "Tier count after removal should be 2");

        // Assert that tier1 is removed
        assertFalse(priceConfig.getPriceTiers().contains(tier1), "Tier1 should be removed");

        // Test price calculation after tier removal:

        // 1. For 3 items, no tier should apply as it is below the minimum range (6), so it should throw an exception.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 3);
        });
        assertEquals("Quantity is below the available min range.", thrown.getMessage(), "Expected exception for quantity below the available range.");

        // 2. For 8 items, as tier2 has become the 1st tier now, and it ranges between 5 to 10,
        // and 8 is within the range, we can calculate all 8 at once
        BigDecimal price = priceCalculator.calculatePrice(priceConfig, 8);
        assertEquals(new BigDecimal("64.0"), price, "Price for 8 items should be based on tier2 rates");

        // 3. For 12 items, tier2 is acting as tier1
        // So, tier2 for the first 10 items, and tier3 for the next 2 items.
        price = priceCalculator.calculatePrice(priceConfig, 12);
        assertEquals(new BigDecimal("80.0").add(new BigDecimal("12.0")), price, "Price for 12 items should be the sum of tier2 and tier3 rates (80.0 + 12.0)");


        // Verify that removing a tier does not break the overall functionality
        thrown = assertThrows(IllegalArgumentException.class, () -> {
            priceCalculator.calculatePrice(priceConfig, 20);  // This should throw an exception as there is no tier for quantity > 15
        });
        assertEquals("Quantity exceeds maximum tier range.", thrown.getMessage(), "Expected exception for quantity greater than the available range.");


        //Now test that over-writing price configs works as expected.
        List<PriceTier> priceTiers = new ArrayList<>();
        priceTiers.add(tier1);
        priceTiers.add(tier2);
        priceTiers.add(tier3);
        priceConfig.setPriceTiers(priceTiers);

        assertEquals(3, priceConfig.getPriceTiers().size(), "Initial tier count should be 3");
        //now all 3 are present, and calculation should be 5*10 + 5*8 + 2*6 = 102
        price = priceCalculator.calculatePrice(priceConfig, 12);
        assertEquals(new BigDecimal("102.0"), price, "Price for 12 items should be the sum of tier2 and tier3 rates (5*10 + 5*8 + 2*6 = 102)");

    }

    @Test
    public void testGraduatedTierStartingAfterOne() {
        PriceTier tier = new PriceTier(2, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceConfig config = new PriceConfig("product14", List.of(tier));

        // Quantity 4 (2-5): 4 *10 =40.0
        BigDecimal price = priceCalculator.calculatePrice(config, 4);
        assertEquals(new BigDecimal("40.0"), price);

        // Quantity 5: 5 *10 (
        price = priceCalculator.calculatePrice(config, 5);
        assertEquals(new BigDecimal("50.0"), price); // Correct if tier is 2-5 (4 items)
    }

    @Test
    public void testTierStartingAtZero() {
        PriceTier tier0 = new PriceTier(0, 5, new BigDecimal("10.0"), PriceModel.GRADUATED);
        PriceConfig config = new PriceConfig("product0", List.of(tier0));

        // Quantity 0 should return 0.0
        BigDecimal price = priceCalculator.calculatePrice(config, 0);
        assertEquals(BigDecimal.ZERO, price);

        // Quantity 3: 3*10 =30.0
        price = priceCalculator.calculatePrice(config, 3);
        assertEquals(new BigDecimal("30.0"), price);
    }
}
