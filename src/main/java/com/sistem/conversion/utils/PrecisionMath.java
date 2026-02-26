package com.sistem.conversion.utils;

import com.sistem.conversion.entity.CollectionMaterial;
import org.apache.commons.math3.fraction.BigFraction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class PrecisionMath {
    private static final BigDecimal SNAP_TOLERANCE = new BigDecimal("0.00001");

    /**
     * Extrae la densidad de la entidad de forma segura.
     * Si es nula o menor/igual a cero, retorna 1.
     */
    public static BigFraction getSafeDensity(CollectionMaterial entity) {
        BigDecimal density = BigDecimal.ONE;
        if (entity.getMaterial() != null && entity.getMaterial().getDensity() != null) {
            BigDecimal d = entity.getMaterial().getDensity();
            if (d.compareTo(BigDecimal.ZERO) > 0) {
                density = d;
            }
        }
        return fromBigDecimal(density);
    }

    public static BigFraction fromBigDecimal(BigDecimal bd) {
        if (bd == null) return BigFraction.ZERO;
        BigDecimal stripped = bd.stripTrailingZeros();
        int scale = stripped.scale();
        BigInteger unscaledValue = stripped.unscaledValue();

        if (scale < 0) {
            return new BigFraction(unscaledValue.multiply(BigInteger.TEN.pow(Math.abs(scale))), BigInteger.ONE);
        } else {
            return new BigFraction(unscaledValue, BigInteger.TEN.pow(scale));
        }
    }

    public static BigDecimal toDecimal(BigFraction f, int scale) {
        if (f == null) return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        BigDecimal raw = new BigDecimal(f.getNumerator())
                .divide(new BigDecimal(f.getDenominator()), 15, RoundingMode.HALF_UP);

        BigDecimal nearestInteger = raw.setScale(0, RoundingMode.HALF_UP);
        if (raw.subtract(nearestInteger).abs().compareTo(SNAP_TOLERANCE) < 0) {
            return nearestInteger.setScale(scale, RoundingMode.HALF_UP);
        }
        return raw.setScale(scale, RoundingMode.HALF_UP);
    }
}