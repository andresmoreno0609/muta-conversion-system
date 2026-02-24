package com.sistem.conversion.service;

import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.enums.ConversionUnit;
import com.sistem.conversion.strategy.ConversionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversionContextService {

    private final List<ConversionStrategy> strategies;

    public DTOConvert convert(CollectionMaterial entity, ConversionUnit targetUnit, boolean isExiting) {
        // 1. Prioridad Máxima: Si el material en BD es "UNI", forzamos UnitStrategy
        if (entity.getMaterial() != null && ConversionUnit.UNI.equals(entity.getMaterial().getMeasure())) {
            return findStrategy(ConversionUnit.UNI).execute(entity, ConversionUnit.UNI, isExiting);
        }

        // 2. Determinar unidad destino con fallback a KG
        ConversionUnit unitToUse = (targetUnit != null) ? targetUnit : ConversionUnit.KG;

        // 3. Buscar estrategia. Si por algún motivo no existe, usamos KgStrategy como salvavidas
        ConversionStrategy strategy = strategies.stream()
                .filter(s -> s.appliesTo(unitToUse))
                .findFirst()
                .orElseGet(() -> findStrategy(ConversionUnit.KG)); // Fallback definitivo a KG

        return strategy.execute(entity, unitToUse, isExiting);
    }

    private ConversionStrategy findStrategy(ConversionUnit unit) {
        return strategies.stream()
                .filter(s -> s.appliesTo(unit))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error crítico: No existe KgStrategy"));
    }

    public DTOConvert getConvertedData(CollectionMaterial entity, boolean isExiting) {
        return convert(entity, null, isExiting);
    }
}