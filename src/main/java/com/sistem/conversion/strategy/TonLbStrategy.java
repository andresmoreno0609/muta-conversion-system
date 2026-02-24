package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TonLbStrategy implements ConversionStrategy {

    // Factor: 1 Tonelada (US/Short) = 2000 Libras
    // Factor Libras a KG: 0.45359237
    private static final BigDecimal TON_LB_FACTOR = new BigDecimal("2000");
    private static final BigDecimal LB_TO_KG = new BigDecimal("0.45359237");

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.TONLB;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        BigDecimal rawQty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal rawNet = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal rawPrice = BigDecimal.valueOf(entity.getUnitPrice());

        BigDecimal finalQty, finalNet, finalPrice, finalSubtotal;

        if (entity.getContainer() != null && entity.getContainer()) {
            // Contenedor: Convertimos peso neto (KG <-> TON LB)
            // KG -> LB -> TON LB
            if (isExiting) {
                BigDecimal lbs = rawNet.divide(LB_TO_KG, 10, RoundingMode.HALF_UP);
                finalNet = lbs.divide(TON_LB_FACTOR, 10, RoundingMode.HALF_UP);
            } else {
                BigDecimal lbs = rawNet.multiply(TON_LB_FACTOR);
                finalNet = lbs.multiply(LB_TO_KG);
            }
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalSubtotal = finalQty.multiply(finalPrice);
        } else {
            // Material Normal: Conversión total
            if (isExiting) {
                // De KG a TON LB
                BigDecimal lbs = rawQty.divide(LB_TO_KG, 10, RoundingMode.HALF_UP);
                finalQty = lbs.divide(TON_LB_FACTOR, 10, RoundingMode.HALF_UP);

                BigDecimal netLbs = rawNet.divide(LB_TO_KG, 10, RoundingMode.HALF_UP);
                finalNet = netLbs.divide(TON_LB_FACTOR, 10, RoundingMode.HALF_UP);

                finalPrice = rawPrice.multiply(LB_TO_KG).multiply(TON_LB_FACTOR);
            } else {
                // De TON LB a KG
                BigDecimal lbs = rawQty.multiply(TON_LB_FACTOR);
                finalQty = lbs.multiply(LB_TO_KG);

                BigDecimal netLbs = rawNet.multiply(TON_LB_FACTOR);
                finalNet = netLbs.multiply(LB_TO_KG);

                finalPrice = rawPrice.divide(TON_LB_FACTOR, 10, RoundingMode.HALF_UP).divide(LB_TO_KG, 10, RoundingMode.HALF_UP);
            }
            finalSubtotal = finalQty.multiply(finalPrice);
        }

        return DTOConvert.builder()
                .quantityCollected(finalQty.setScale(3, RoundingMode.HALF_UP).doubleValue())
                .netQuantityCollected(finalNet.setScale(3, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(finalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .subtotal(finalSubtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.TONLB.name() : ConversionUnit.KG.name())
                .build();
    }
}
