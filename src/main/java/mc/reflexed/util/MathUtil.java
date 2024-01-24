package mc.reflexed.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil extends Util {

    public static double toFixed(double d, int places) {
        if(places < 0) throw new IllegalArgumentException();

        if(Double.isInfinite(d) || Double.isNaN(d)) return d;

        return new BigDecimal(d).setScale(places, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static double bigger(double d1, double d2) {
        return Math.max(d1, d2);
    }

    public static double round(double d, int i) {
        return Math.round(d * Math.pow(10, i)) / Math.pow(10, i);
    }
}
