package com.jpmorgan.sssm.math;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * Shared resource for math constants and common math operations.
 *
 * @author Anthony Accioly
 */
@UtilityClass
@Slf4j
public class FixedPointMath {

    private static final int INTERNAL_OPERATIONS_PRECISION = 30;
    /**
     * Unit in the last place
     */
    private static final BigDecimal ULP = BigDecimal.valueOf(.1).movePointLeft(INTERNAL_OPERATIONS_PRECISION);

    public static final RoundingMode ROUNDING_MODE = HALF_EVEN;
    public static final MathContext MATH_CONTEXT = new MathContext(INTERNAL_OPERATIONS_PRECISION, ROUNDING_MODE);
    public static final int CURRENCY_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 5;

    public static final BigDecimal MIN_VALUE = new BigDecimal("0.01");
    private static final String MIN_VALUE_MESSAGE = "%s has to be equal or greater than %s";

    /**
     * Ensures that the {@code left} parameter is greater than or equal to @{right} parameter.
     *
     * @param label a label to describe the argument in the exception error message
     * @param left the left element of the ordered pair of arguments
     * @param right the right element of the ordered pair of arguments
     * @param <T> numerical type
     * @param <V> numerical type comparable with {@code <T>}
     *
     * @throws IllegalArgumentException if {@code right} is less than {@code left}. The resulting exception contains a message stating the
     * {@code label} and {@code right} argument.
     */
    public static <T extends Number, V extends Comparable<T>>  void checkArgumentGreaterThanOrEgual(String label, V left, T right) {
        checkArgument(left.compareTo(right) >= 0, MIN_VALUE_MESSAGE, label, right);
    }
    /**
     * Returns the correctly rounded positive square root of a BigDecimal value.
     *
     * @param n degree of the root
     * @param a a value
     *
     * @return the value of {@code r} such that {@code r}<sup>{@code n}</sup>{@code =a}
     */
     static BigDecimal nthRoot(final int n, final BigDecimal a) {
        checkArgument(a.compareTo(BigDecimal.ZERO) >= 0, "nth root can only be calculated for positive numbers");

        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal nBigDecimal = new BigDecimal(n);
        final BigDecimal nMinus1BigDecimal = new BigDecimal(n - 1);

        BigDecimal xPrev = a;
        BigDecimal x = a.divide(nBigDecimal, INTERNAL_OPERATIONS_PRECISION, ROUNDING_MODE);

        while (x.subtract(xPrev).abs().compareTo(ULP) > 0) {
            log.trace("Guess: {}, previous: {}", x, xPrev);
            xPrev = x;
            x = nMinus1BigDecimal
                    .multiply(x)
                    .add(a.divide(x.pow(n - 1), INTERNAL_OPERATIONS_PRECISION, ROUNDING_MODE))
                    .divide(nBigDecimal, INTERNAL_OPERATIONS_PRECISION, ROUNDING_MODE);
        }

        return x;
    }

}
