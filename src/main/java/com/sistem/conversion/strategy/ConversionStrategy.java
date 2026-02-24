package com.sistem.conversion.strategy;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;

public interface ConversionStrategy {
    boolean appliesTo(ConversionUnit targetUnit);
    DTOConvert execute(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting);
}
