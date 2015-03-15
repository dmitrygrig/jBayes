/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbayes.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ensure is a helper class that used for assertions.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Ensure {

    private static final Logger LOGGER = LoggerFactory.getLogger(Ensure.class);

    private Ensure() {
    }

    /**
     * Is assert used. See more:
     * http://docs.oracle.com/cd/E19683-01/806-7930/assert-13/index.html
     * http://stackoverflow.com/questions/2758224/assertion-in-java
     */
    private static boolean isAssert = false;

    /**
     * Are exceptions thrown on wrong conditions.
     */
    private static boolean throwEnabled = true;

    public static void setIsAssert(boolean isAssert) {
        Ensure.isAssert = isAssert;
    }

    public static void setThrowEnabled(boolean throwEnabled) {
        Ensure.throwEnabled = throwEnabled;
    }

    public final static void IsNull(Object value, String message) {
        if (value != null) {
            throw new IllegalArgumentException(String.format("%s should ne null.", message));
        }
    }

    public final static void NotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null.", message));
        }
    }

    public final static void NotNullOrEmpty(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be null.", message));
        }
    }

    public final static void IsTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalStateException(message);
        }
    }

    public final static void IsFalse(boolean value, String message) {
        if (value) {
            throw new IllegalStateException(message);
        }
    }

    public final static void AreEquals(Object value, Object expected, String message) {
        if ((value == null && expected != null)
                || (value != null && !(value.equals(expected)))) {
            throw new IllegalStateException(String.format("%s should equal %s.", message, expected == null ? "<null>" : expected.toString()));
        }
    }

    public final static void AreNotEquals(Object value, Object expected, String message) {
        if ((value == null && expected == null)
                || (value != null && (value.equals(expected)))) {
            throw new IllegalStateException(String.format("%s should not equal %s.", message, expected == null ? "<null>" : expected.toString()));
        }
    }

    public final static void BetweenInclusive(long value, long lowerExpected, long upperExpected, String message) {
        check(value >= lowerExpected && value <= upperExpected, String.format("%s should be between %d and %d inclusive.", message, lowerExpected, upperExpected));
    }

    public final static void BetweenExclusive(long value, long lowerExpected, long upperExpected, String message) {
        check(value > lowerExpected && value < upperExpected, String.format("%s should be between %d and %d exclusive.", message, lowerExpected, upperExpected));
    }

    public final static void BetweenInclusive(int value, int lowerExpected, int upperExpected, String message) {
        check(value >= lowerExpected && value <= upperExpected, String.format("%s should be between %d and %d inclusive.", message, lowerExpected, upperExpected));
    }

    public final static void BetweenExclusive(int value, int lowerExpected, int upperExpected, String message) {
        check(value > lowerExpected && value < upperExpected, String.format("%s should be between %d and %d exclusive.", message, lowerExpected, upperExpected));
    }

    public final static void BetweenInclusive(double value, double lowerExpected, double upperExpected, String message) {
        check(value >= lowerExpected && value <= upperExpected, String.format("%s should be between %f and %f inclusive.", message, lowerExpected, upperExpected));
    }

    public final static void BetweenExclusive(double value, double lowerExpected, double upperExpected, String message) {
        check(value > lowerExpected && value < upperExpected, String.format("%s should be between %f and %f exclusive.", message, lowerExpected, upperExpected));
    }

    public final static void GreaterThan(long value, long expected, String message) {
        check(value > expected, String.format("%s should be greater than %d.", message, expected));
    }

    public final static void GreaterThanOrEquals(long value, long expected, String message) {
        check(value >= expected, String.format("%s should be greater than or equal to %d.", message, expected));
    }

    public final static void GreaterThan(int value, int expected, String message) {
        check(value > expected, String.format("%s should be greater than %d.", message, expected));
    }

    public final static void GreaterThanOrEquals(int value, int expected, String message) {
        check(value >= expected, String.format("%s should be greater than or equal to %d.", message, expected));
    }

    public final static void GreaterThan(double value, double expected, String message) {
        check(value > expected, String.format("%s should be greater than %f.", message, expected));
    }

    public final static void GreaterThanOrEquals(double value, double expected, String message) {
        check(value >= expected, String.format("%s should be greater than or equal to %f.", message, expected));
    }

    public final static void LowerThan(long value, long expected, String message) {
        check(value < expected, String.format("%s should be greater than %d.", message, expected));
    }

    public final static void LowerThanOrEquals(long value, long expected, String message) {
        check(value <= expected, String.format("%s should be greater than or equal to %d.", message, expected));
    }

    public final static void LowerThan(int value, int expected, String message) {
        check(value < expected, String.format("%s should be greater than %d.", message, expected));
    }

    public final static void LowerThanOrEquals(int value, int expected, String message) {
        check(value <= expected, String.format("%s should be greater than or equal to %d.", message, expected));
    }

    public final static void LowerThan(double value, double expected, String message) {
        check(value < expected, String.format("%s should be greater than %f.", message, expected));
    }

    public final static void LowerThanOrEquals(double value, double expected, String message) {
        check(value <= expected, String.format("%s should be greater than or equal to %f.", message, expected));
    }

    private static void check(boolean condition, String message) {
        if (isAssert) {
            assert condition;
        } else {
            if (!condition) {
                LOGGER.error(message, new IllegalStateException(message));
                if (throwEnabled) {
                    throw new IllegalStateException(message);
                }
            }
        }
    }
}
