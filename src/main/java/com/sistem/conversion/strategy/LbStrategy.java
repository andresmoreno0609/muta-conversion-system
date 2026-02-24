package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class LbStrategy implements ConversionStrategy {

    private static final BigDecimal LB_FACTOR = new BigDecimal("0.45359237");

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.LB;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        BigDecimal rawQty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal rawNet = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal rawPrice = BigDecimal.valueOf(entity.getUnitPrice());

        BigDecimal finalQty;
        BigDecimal finalNet;
        BigDecimal finalPrice;
        BigDecimal finalSubtotal;

        if (entity.getContainer() != null && entity.getContainer()) {
            // Solo convertimos el peso neto (KG <-> LB)
            finalNet = isExiting ? rawNet.divide(LB_FACTOR, 10, RoundingMode.HALF_UP) : rawNet.multiply(LB_FACTOR);
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalSubtotal = finalQty.multiply(finalPrice);
        } else {
            if (isExiting) {
                finalQty = rawQty.divide(LB_FACTOR, 10, RoundingMode.HALF_UP);
                finalNet = rawNet.divide(LB_FACTOR, 10, RoundingMode.HALF_UP);
                finalPrice = rawPrice.multiply(LB_FACTOR);
            } else {
                finalQty = rawQty.multiply(LB_FACTOR);
                finalNet = rawNet.multiply(LB_FACTOR);
                finalPrice = rawPrice.divide(LB_FACTOR, 10, RoundingMode.HALF_UP);
            }
            finalSubtotal = finalQty.multiply(finalPrice);
        }

        return DTOConvert.builder()
                .quantityCollected(finalQty.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .netQuantityCollected(finalNet.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(finalPrice.setScale(6, RoundingMode.HALF_UP).doubleValue())
                .subtotal(finalSubtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.LB.name() : ConversionUnit.KG.name())
                .build();
    }
}