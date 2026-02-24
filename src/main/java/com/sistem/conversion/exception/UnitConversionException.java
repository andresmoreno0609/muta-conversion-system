package com.sistem.conversion.exception;


/**
 * Excepción de negocio para fallos en la conversión de unidades.
 * Se lanza cuando no existe una estrategia válida para convertir
 * entre dos unidades o cuando la unidad es desconocida.
 */
public class UnitConversionException extends RuntimeException {

    private final String fromUnit;
    private final String toUnit;

    public UnitConversionException(String fromUnit, String toUnit) {
        super("Error al convertir unidad de medida: de " + fromUnit + " a " + toUnit);
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }
}


