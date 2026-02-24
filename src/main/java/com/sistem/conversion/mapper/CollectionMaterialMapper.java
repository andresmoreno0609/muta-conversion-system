package com.sistem.conversion.mapper;

import com.sistem.conversion.dto.CollectionMaterialDTO;
import com.sistem.conversion.dto.DTOConvert;
import com.sistem.conversion.entity.CollectionMaterial;
import com.sistem.conversion.service.ConversionContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectionMaterialMapper {

    private final ConversionContextService conversionService;

    public CollectionMaterialDTO toDTO(CollectionMaterial entity) {
        if (entity == null) return null;

        DTOConvert converted = conversionService.getConvertedData(entity, true);

        return CollectionMaterialDTO.builder()
                .id(entity.getId().longValue())
                .collectionId(entity.getCollection() != null ? entity.getCollection().getId() : null)
                .materialId(entity.getMaterial() != null ? entity.getMaterial().getId().intValue() : null)
                .quantityCollected(converted.getQuantityCollected())
                .netQuantityCollected(converted.getNetQuantityCollected())
                .unitPrice(converted.getUnitPrice())
                .subtotal(converted.getSubtotal())
                .displayMeasure(converted.getMeasureLabel())
                .materialMeasure(entity.getMaterial() != null ? entity.getMaterial().getMeasure().name() : "KG")
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}