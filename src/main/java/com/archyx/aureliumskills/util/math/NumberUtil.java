package com.archyx.aureliumskills.util.math;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

    private static final NumberFormat zero = new DecimalFormat("#");
    private static final NumberFormat one = new DecimalFormat("#.#");
    private static final NumberFormat two = new DecimalFormat("#.##");

    static {
        zero.setRoundingMode(RoundingMode.HALF_UP);
        one.setRoundingMode(RoundingMode.HALF_UP);
        two.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static String format0(double input) {
        return zero.format(input);
    }

    public static String format1(double input) {
        return one.format(input);
    }

    public static String format2(double input) {
        return two.format(input);
    }

}
