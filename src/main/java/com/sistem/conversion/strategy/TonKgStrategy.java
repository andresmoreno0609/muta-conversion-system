package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class TonKgStrategy implements ConversionStrategy {

    private static final BigDecimal TON_FACTOR = new BigDecimal("1000");

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.TONKG;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        BigDecimal rawQty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal rawNet = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal rawPrice = BigDecimal.valueOf(entity.getUnitPrice());

        BigDecimal finalQty, finalNet, finalPrice, finalSubtotal;

        if (entity.getContainer() != null && entity.getContainer()) {
            // Contenedor: Solo convertimos peso neto (KG <-> TON)
            finalNet = isExiting ? rawNet.divide(TON_FACTOR, 10, RoundingMode.HALF_UP) : rawNet.multiply(TON_FACTOR);
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalSubtotal = finalQty.multiply(finalPrice);
        } else {
            if (isExiting) {
                finalQty = rawQty.divide(TON_FACTOR, 10, RoundingMode.HALF_UP);
                finalNet = rawNet.divide(TON_FACTOR, 10, RoundingMode.HALF_UP);
                finalPrice = rawPrice.multiply(TON_FACTOR);
            } else {
                finalQty = rawQty.multiply(TON_FACTOR);
                finalNet = rawNet.multiply(TON_FACTOR);
                finalPrice = rawPrice.divide(TON_FACTOR, 10, RoundingMode.HALF_UP);
            }
            finalSubtotal = finalQty.multiply(finalPrice);
        }

        return DTOConvert.builder()
                .quantityCollected(finalQty.setScale(3, RoundingMode.HALF_UP).doubleValue()) // Ton usa 3 decimales
                .netQuantityCollected(finalNet.setScale(3, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(finalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .subtotal(finalSubtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.TONKG.name() : ConversionUnit.KG.name())
                .build();
    }
}