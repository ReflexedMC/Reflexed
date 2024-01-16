package mc.reflexed.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil extends Util {

    public static double toFixed(double d, int places) {
        return new BigDecimal(d).setScale(places, RoundingMode.HALF_EVEN).doubleValue();
    }
}
