package com.sistem.conversion.mapper;

import com.sistem.conversion.dto.CollectionDTO;
import com.sistem.conversion.entity.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CollectionMapperV2 {

    // Inyectamos el mapper especializado en materiales
    private final CollectionMaterialMapper materialMapper;

    public CollectionDTO toDTO(Collection entity) {
        if (entity == null) return null;

        CollectionDTO dto = new CollectionDTO();
        dto.setId(entity.getId());
        dto.setConsecutive(entity.getConsecutive());

        if (entity.getMaterials() != null) {
            dto.setMaterials(entity.getMaterials().stream()
                    // Delegamos la responsabilidad al mapper especializado
                    .map(materialMapper::toDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setMaterials(new ArrayList<>());
        }

        return dto;
    }
}