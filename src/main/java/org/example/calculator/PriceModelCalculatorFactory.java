package org.example.calculator;

import org.example.calculator.impl.FlatPriceCalculator;
import org.example.calculator.impl.GraduatedPriceCalculator;
import org.example.calculator.impl.VolumePriceCalculator;
import org.example.calculator.intf.PriceModelCalculator;
import org.example.enums.PriceModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating price model strategies.
 */
public class PriceModelCalculatorFactory {
    private static final Map<PriceModel, PriceModelCalculator> priceModelCalculatorMap = new HashMap<>();
    
    static {
        priceModelCalculatorMap.put(PriceModel.FLAT, new FlatPriceCalculator());
        priceModelCalculatorMap.put(PriceModel.VOLUME, new VolumePriceCalculator());
        // Note: GRADUATED is handled separately
    }
    
    /**
     * Gets the appropriate strategy for a price model.
     *
     * @param priceModel The price model
     * @return The corresponding strategy
     * @throws IllegalArgumentException if the price model is not supported
     */
    public static PriceModelCalculator getPriceModelCalculator(PriceModel priceModel) {
        PriceModelCalculator calculator = priceModelCalculatorMap.get(priceModel);
        if (calculator == null) {
            throw new IllegalArgumentException("Unsupported pricing model: " + priceModel);
        }
        return calculator;
    }
    
    /**
     * Gets the graduated price strategy.
     *
     * @return The graduated price strategy
     */
    public static GraduatedPriceCalculator getGraduatedModelCalculator() {
        return new GraduatedPriceCalculator();
    }
}