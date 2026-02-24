package com.sistem.conversion.enums;

import lombok.Getter;

@Getter
public enum ConversionUnit {
    KG("WEIGHT", "kg"),
    LB("WEIGHT", "lb"),
    TONKG("WEIGHT", "t"),
    TONLB("WEIGHT", "ton"),
    L("VOLUME", "L"),
    US_GAL("VOLUME", "gal (US)"),
    UK_GAL("VOLUME", "gal (UK)"),
    UNI("UNIT", "unit");

    private final String type;
    private final String description;

    ConversionUnit(String type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * Resuelve la unidad de medida.
     * Si el valor es nulo, vacío o no coincide exactamente con el Enum,
     * retorna KG por defecto.
     */
    public static ConversionUnit fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return KG;
        }
        try {
            return ConversionUnit.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return KG;
        }
    }
}