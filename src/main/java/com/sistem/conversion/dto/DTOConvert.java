package com.sistem.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOConvert {
    private Double quantityCollected;
    private Double netQuantityCollected;
    private Double unitPrice;
    private Double subtotal;
    private String measureLabel;
}
