package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import com.sistem.conversion.utils.PrecisionMath;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import org.apache.commons.math3.fraction.BigFraction;
import java.math.BigInteger;

@Component
public class UkGalStrategy implements ConversionStrategy {

    private static final BigFraction GAL_UK_TO_L = new BigFraction(
            new BigInteger("454609"), new BigInteger("100000"));

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.UK_GAL;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Obtención centralizada de densidad y valores base
        BigFraction density = PrecisionMath.getSafeDensity(entity);
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        BigFraction finalQty, finalNet, finalPrice;
        boolean isContainer = Boolean.TRUE.equals(entity.getContainer());

        // 2. Lógica de Conversión
        if (isContainer) {
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalNet = isExiting ? rawNet.divide(density).divide(GAL_UK_TO_L)
                    : rawNet.multiply(GAL_UK_TO_L).multiply(density);
        } else {
            if (isExiting) {
                finalQty = rawQty.divide(density).divide(GAL_UK_TO_L);
                finalNet = rawNet.divide(density).divide(GAL_UK_TO_L);
                finalPrice = rawPrice.multiply(density).multiply(GAL_UK_TO_L);
            } else {
                finalQty = rawQty.multiply(GAL_UK_TO_L).multiply(density);
                finalNet = rawNet.multiply(GAL_UK_TO_L).multiply(density);
                finalPrice = rawPrice.divide(GAL_UK_TO_L).divide(density);
            }
        }

        BigFraction finalSubtotal = finalQty.multiply(finalPrice);

        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 6).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.UK_GAL.name() : ConversionUnit.KG.name())
                .build();
    }
}