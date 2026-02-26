package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import com.sistem.conversion.utils.PrecisionMath;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import org.apache.commons.math3.fraction.BigFraction;

@Component
public class LStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.L;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Invocamos la utilidad centralizada (ahora devuelve BigFraction directamente)
        BigFraction density = PrecisionMath.getSafeDensity(entity);

        // 2. Resto de entradas
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        BigFraction finalQty, finalNet, finalPrice;
        boolean isContainer = Boolean.TRUE.equals(entity.getContainer());

        // 3. Lógica de conversión (Usando la densidad centralizada)
        if (isContainer) {
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalNet = isExiting ? rawNet.divide(density) : rawNet.multiply(density);
        } else {
            if (isExiting) {
                finalQty = rawQty.divide(density);
                finalNet = rawNet.divide(density);
                finalPrice = rawPrice.multiply(density);
            } else {
                finalQty = rawQty.multiply(density);
                finalNet = rawNet.multiply(density);
                finalPrice = rawPrice.divide(density);
            }
        }

        // 4. Subtotal y DTO (Igual que antes...)
        BigFraction finalSubtotal = finalQty.multiply(finalPrice);
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 6).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(isExiting ? targetUnit.name() : ConversionUnit.KG.name())
                .build();
    }
}