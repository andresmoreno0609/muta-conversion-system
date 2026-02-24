package com.sistem.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor    // <--- Agrega esto para permitir "new CollectionMaterialDTO()"
@AllArgsConstructor   // <--- Agrega esto para que @Builder no falle
public class CollectionMaterialDTO {
    private Long id;
    private Long collectionId;
    private Integer materialId;
    private Double quantityCollected;
    private Double netQuantityCollected;
    private Double unitPrice;
    private Double subtotal;
    private String displayMeasure;
    private String materialMeasure;
    private Double materialDensity;
    private Map<String, String> materialTranslations;
    private Boolean isContainer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
