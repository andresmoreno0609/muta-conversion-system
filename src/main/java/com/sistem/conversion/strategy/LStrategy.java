package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class LStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.L;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // Resolución de Densidad
        BigDecimal density = (entity.getMaterial() != null && entity.getMaterial().getDensity() != null &&
                entity.getMaterial().getDensity().compareTo(BigDecimal.ZERO) > 0)
                ? entity.getMaterial().getDensity()
                : BigDecimal.ONE;

        BigDecimal rawQty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal rawNet = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal rawPrice = BigDecimal.valueOf(entity.getUnitPrice());

        BigDecimal finalQty;
        BigDecimal finalNet;
        BigDecimal finalPrice;
        BigDecimal finalSubtotal;

        // --- REGLA DE CONTENEDOR ---
        if (entity.getContainer() != null && entity.getContainer()) {
            // 1. El peso Neto SIEMPRE se convierte (KG <-> L)
            finalNet = isExiting ? rawNet.multiply(density) : rawNet.divide(density, 10, RoundingMode.HALF_UP);

            // 2. Cantidad y Precio NO se convierten (son por unidad de contenedor)
            finalQty = rawQty;
            finalPrice = rawPrice;

            // 3. Subtotal es directo
            finalSubtotal = finalQty.multiply(finalPrice);
        } else {
            // --- REGLA MATERIAL NORMAL ---
            if (isExiting) {
                finalQty = rawQty.multiply(density);
                finalNet = rawNet.multiply(density);
                finalPrice = rawPrice.divide(density, 10, RoundingMode.HALF_UP);
            } else {
                finalQty = rawQty.divide(density, 10, RoundingMode.HALF_UP);
                finalNet = rawNet.divide(density, 10, RoundingMode.HALF_UP);
                finalPrice = rawPrice.multiply(density);
            }
            finalSubtotal = finalQty.multiply(finalPrice);
        }

        return DTOConvert.builder()
                .quantityCollected(finalQty.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .netQuantityCollected(finalNet.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(finalPrice.setScale(6, RoundingMode.HALF_UP).doubleValue())
                .subtotal(finalSubtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.L.name() : ConversionUnit.KG.name())
                .build();
    }
}