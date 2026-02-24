package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class UsGalStrategy implements ConversionStrategy {

    private static final BigDecimal GAL_US_TO_L = new BigDecimal("3.78541");

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.US_GAL;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        BigDecimal density = (entity.getMaterial() != null && entity.getMaterial().getDensity() != null &&
                entity.getMaterial().getDensity().compareTo(BigDecimal.ZERO) > 0)
                ? entity.getMaterial().getDensity()
                : BigDecimal.ONE;

        BigDecimal rawQty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal rawNet = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal rawPrice = BigDecimal.valueOf(entity.getUnitPrice());

        BigDecimal finalQty, finalNet, finalPrice, finalSubtotal;

        if (entity.getContainer() != null && entity.getContainer()) {
            // Contenedor: Solo peso neto (KG -> L -> GAL)
            if (isExiting) {
                BigDecimal liters = rawNet.multiply(density);
                finalNet = liters.divide(GAL_US_TO_L, 10, RoundingMode.HALF_UP);
            } else {
                BigDecimal liters = rawNet.multiply(GAL_US_TO_L);
                finalNet = liters.divide(density, 10, RoundingMode.HALF_UP);
            }
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalSubtotal = finalQty.multiply(finalPrice);
        } else {
            if (isExiting) {
                BigDecimal liters = rawQty.multiply(density);
                finalQty = liters.divide(GAL_US_TO_L, 10, RoundingMode.HALF_UP);
                finalNet = rawNet.multiply(density).divide(GAL_US_TO_L, 10, RoundingMode.HALF_UP);
                finalPrice = rawPrice.divide(density, 10, RoundingMode.HALF_UP).multiply(GAL_US_TO_L);
            } else {
                BigDecimal liters = rawQty.multiply(GAL_US_TO_L);
                finalQty = liters.divide(density, 10, RoundingMode.HALF_UP);
                finalNet = rawNet.multiply(GAL_US_TO_L).divide(density, 10, RoundingMode.HALF_UP);
                finalPrice = rawPrice.divide(GAL_US_TO_L, 10, RoundingMode.HALF_UP).multiply(density);
            }
            finalSubtotal = finalQty.multiply(finalPrice);
        }

        return DTOConvert.builder()
                .quantityCollected(finalQty.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .netQuantityCollected(finalNet.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(finalPrice.setScale(6, RoundingMode.HALF_UP).doubleValue())
                .subtotal(finalSubtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.US_GAL.name() : ConversionUnit.KG.name())
                .build();
    }
}