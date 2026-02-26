package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import com.sistem.conversion.utils.PrecisionMath;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import org.apache.commons.math3.fraction.BigFraction;

@Component
public class UnitStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.UNI;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Conversión de entradas a fracciones (Normalización)
        BigFraction finalQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction finalNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction finalPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        // 2. Cálculo del Subtotal exacto (independiente de si es contenedor o no en UNI)
        BigFraction finalSubtotal = finalQty.multiply(finalPrice);

        // 3. Retorno con Snap (Limpieza de 9.999999 -> 10.0)
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 2).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 2).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 6).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(ConversionUnit.UNI.name())
                .build();
    }
}