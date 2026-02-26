package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import com.sistem.conversion.utils.PrecisionMath;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import org.apache.commons.math3.fraction.BigFraction;

@Component
public class KgStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.KG;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Convertimos a fracciones.
        // Aunque no hay conversión de unidad, esto "limpia" el ruido de entrada (ej: 9.999999)
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        // 3. Recalculamos subtotal para asegurar coherencia con el precio "limpio"
        BigFraction finalSubtotal = rawQty.multiply(rawPrice);

        // 4. Retorno con Snap (Si el precio era 9.999999, aquí se convierte en 10.0)
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(rawQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(rawNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(rawPrice, 6).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(ConversionUnit.KG.name())
                .build();
    }
}