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
public class TonKgStrategy implements ConversionStrategy {

    // Factor exacto: 1000
    private static final BigFraction TON_FACTOR = new BigFraction(new BigInteger("1000"), BigInteger.ONE);

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.TONKG;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Preparación de entradas en fracciones
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        BigFraction finalQty, finalNet, finalPrice;
        boolean isContainer = Boolean.TRUE.equals(entity.getContainer());

        // 2. Lógica de conversión (Masa <-> Masa)
        if (isContainer) {
            // Caso Contenedor: Cantidad y Precio fijos. Neto: KG / 1000 = TON
            finalQty = rawQty;
            finalPrice = rawPrice;
            finalNet = isExiting ? rawNet.divide(TON_FACTOR) : rawNet.multiply(TON_FACTOR);
        } else {
            // Caso Granel: Conversión de escala
            if (isExiting) { // SALIDA: BD(KG) -> APP(TON)
                finalQty = rawQty.divide(TON_FACTOR);
                finalNet = rawNet.divide(TON_FACTOR);
                finalPrice = rawPrice.multiply(TON_FACTOR);
            } else { // ENTRADA: APP(TON) -> BD(KG)
                finalQty = rawQty.multiply(TON_FACTOR);
                finalNet = rawNet.multiply(TON_FACTOR);
                finalPrice = rawPrice.divide(TON_FACTOR);
            }
        }

        // 3. Cálculo de subtotal exacto antes de redondear
        BigFraction finalSubtotal = finalQty.multiply(finalPrice);

        // 4. Salida con Snap y escalas sugeridas para Toneladas (3 decimales para peso)
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 2).doubleValue()) // Precio TON suele llevar 2
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.TONKG.name() : ConversionUnit.KG.name())
                .build();
    }
}