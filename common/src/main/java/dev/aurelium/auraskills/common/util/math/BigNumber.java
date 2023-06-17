package dev.aurelium.auraskills.common.util.math;

public class BigNumber {
	
	public static String withSuffix(long count) {
	    if (count < 1000) return "" + count;
	    int exp = (int) (Math.log(count) / Math.log(1000));
		double val = count / Math.pow(1000, exp);
		if (val == (long) val) { // Format as an integer
			return String.format("%d%c", (long) val, "KMBTQU".charAt(exp - 1));
		} else {
			return String.format("%.1f%c", val, "KMBTQU".charAt(exp - 1));
		}
	}
}
