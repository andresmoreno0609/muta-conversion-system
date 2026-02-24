package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class UnitStrategy implements ConversionStrategy {

    @Override
    public boolean appliesTo(ConversionUnit targetUnit) {
        return targetUnit == ConversionUnit.UNI;
    }

    @Override
    public DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        BigDecimal qty = BigDecimal.valueOf(entity.getQuantityCollected());
        BigDecimal net = BigDecimal.valueOf(entity.getNetQuantityCollected());
        BigDecimal price = BigDecimal.valueOf(entity.getUnitPrice());

        // En UNI, el subtotal siempre es qty * price sin importar si es contenedor o no
        BigDecimal subtotal = qty.multiply(price);

        return DTOConvert.builder()
                .quantityCollected(qty.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .netQuantityCollected(net.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .unitPrice(price.setScale(6, RoundingMode.HALF_UP).doubleValue())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .measureLabel(ConversionUnit.UNI.name())
                .build();
    }
}