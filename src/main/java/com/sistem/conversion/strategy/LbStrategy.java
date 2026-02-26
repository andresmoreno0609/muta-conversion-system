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
public class LbStrategy implements ConversionStrategy {

    // Factor exacto: 0.45359237 (45359237 / 100,000,000)
    private static final BigFraction LB_TO_KG = new BigFraction(
            new BigInteger("45359237"), new BigInteger("100000000"));

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.LB;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Preparación de entradas en fracciones
        BigFraction rawQty = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getQuantityCollected()));
        BigFraction rawNet = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getNetQuantityCollected()));
        BigFraction rawPrice = PrecisionMath.fromBigDecimal(BigDecimal.valueOf(entity.getUnitPrice()));

        BigFraction finalQty, finalNet, finalPrice;
        boolean isContainer = Boolean.TRUE.equals(entity.getContainer());

        // 2. Ejecución de lógica según el checklist
        if (isContainer) {
            // Caso Contenedor: Cantidad y Precio no varían, el neto se convierte de KG a LB
            finalQty = rawQty;
            finalPrice = rawPrice;
            // isExiting (BD->APP): KG / Factor = LB | !isExiting (APP->BD): LB * Factor = KG
            finalNet = isExiting ? rawNet.divide(LB_TO_KG) : rawNet.multiply(LB_TO_KG);
        } else {
            // Caso Granel: Conversión de escala completa
            if (isExiting) { // SALIDA: BD(KG) -> APP(LB)
                finalQty = rawQty.divide(LB_TO_KG);
                finalNet = rawNet.divide(LB_TO_KG);
                finalPrice = rawPrice.multiply(LB_TO_KG);
            } else { // ENTRADA: APP(LB) -> BD(KG)
                finalQty = rawQty.multiply(LB_TO_KG);
                finalNet = rawNet.multiply(LB_TO_KG);
                finalPrice = rawPrice.divide(LB_TO_KG);
            }
        }

        // 3. Cálculo de subtotal exacto
        BigFraction finalSubtotal = finalQty.multiply(finalPrice);

        // 4. Salida limpia con Snap
        return DTOConvert.builder()
                .quantityCollected(PrecisionMath.toDecimal(finalQty, 3).doubleValue())
                .netQuantityCollected(PrecisionMath.toDecimal(finalNet, 3).doubleValue())
                .unitPrice(PrecisionMath.toDecimal(finalPrice, 6).doubleValue())
                .subtotal(PrecisionMath.toDecimal(finalSubtotal, 2).doubleValue())
                .measureLabel(isExiting ? ConversionUnit.LB.name() : ConversionUnit.KG.name())
                .build();
    }
}