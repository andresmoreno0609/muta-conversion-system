package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class KgStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.KG;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // En KG los valores entran y salen de la BD sin cambios de escala numérica,
        // pero normalizamos a los decimales estándar del sistema.

        return DTOConvert.builder()
                .quantityCollected(scale(entity.getQuantityCollected(), 2))
                .netQuantityCollected(scale(entity.getNetQuantityCollected(), 2))
                .unitPrice(scale(entity.getUnitPrice(), 6))
                .subtotal(scale(entity.getSubtotal(), 2))
                .measureLabel(ConversionUnit.KG.name())
                .build();
    }

    private double scale(double value, int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
