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
public class TonLbStrategy implements ConversionStrategy {

    // Factor LB a KG: 0.45359237
    private static final BigFraction LB_TO_KG = new BigFraction(
            new BigInteger("45359237"), new BigInteger("100000000"));

    // Factor TON a LB: 2000
    private static final BigFraction TON_TO_LB = new BigFraction(new BigInteger("2000"), BigInteger.ONE);

    // Factor Combinado TON_LB a KG = 2000 * 0.45359237
    private static final BigFraction TON_LB_TO_KG = TON_TO_LB.multiply(LB_TO_KG);

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.TONLB;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Preparación de entradas
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        BigFraction finalQty, finalNet, finalPrice;
        boolean isContainer = Boolean.TRUE.equals(entity.getContainer());

        // 2. Lógica de conversión
        if (isContainer) {
            finalQty = rawQty;
            finalPrice = rawPrice;
            // isExiting: KG -> LB -> TON (Dividir por el factor combinado)
            finalNet = isExiting ? rawNet.divide(TON_LB_TO_KG) : rawNet.multiply(TON_LB_TO_KG);
        } else {
            if (isExiting) { // SALIDA: BD(KG) -> APP(TON LB)
                finalQty = rawQty.divide(TON_LB_TO_KG);
                finalNet = rawNet.divide(TON_LB_TO_KG);
                finalPrice = rawPrice.multiply(TON_LB_TO_KG);
            } else { // ENTRADA: APP(TON LB) -> BD(KG)
                finalQty = rawQty.multiply(TON_LB_TO_KG);
                finalNet = rawNet.multiply(TON_LB_TO_KG);
                finalPrice = rawPrice.divide(TON_LB_TO_KG);
            }
        }

        // 3. Subtotal exacto
        BigFraction finalSubtotal = finalQty.multiply(finalPrice);

        // 4. Salida con Snap
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 2).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.TONLB.name() : ConversionUnit.KG.name())
                .build();
    }
}