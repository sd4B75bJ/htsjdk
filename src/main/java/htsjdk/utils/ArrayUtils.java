package htsjdk.utils;

/**
 * Set of utility methods on naked array instances not provided by {@link java.util.Arrays} or other dependencies.
 */
public final class ArrayUtils {

    private ArrayUtils() {}

    /**
     * Returns the first index within a byte array range of a particular byte value.
     * @param where the target array.
     * @param what what byte value we are looking for.
     * @param from first position of the array to be considered.
     * @param to last position of the array to be considered +1
     * @return either {@code -1} indicating that the value could not be found in the range,
     *  or an index within the range {@code [from, to - 1]}.
     * @throws NullPointerException if {@code where} is {@code null}
     * @throws IndexOutOfBoundsException if {@code from} or {@code to} point to invalid positions
     * outside the {@code where} array.
     */
    public static int indexOf(final byte[] where, final byte what, final int from, final int to) {
        for (int i = from; i < to; ++i) {
            if (where[i] == what) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index within a byte array range of a particular byte value.
     * @param where the target array.
     * @param what what byte value we are looking for.
     * @param from first position of the array to be considered.
     * @param to last position of the array to be considered +1
     * @return either {@code -1} indicating that the value could not be found in the range,
     *  or an index within the range {@code [from, to - 1]}.
     * @throws NullPointerException if {@code where} is {@code null}
     * @throws IndexOutOfBoundsException if {@code from} or {@code to} point to invalid positions
     * outside the {@code where} array.
     */
    public static int lastIndexOf(final byte[] where, final byte what, final int from, final int to) {
        for (int i = to - 1; i >= from; --i) {
            if (where[i] == what) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the integer represented as digit characters in a range of a byte array.
     * <p>
     *     A sign character '-' or '+' is allowed at the very beginning of the range.
     *     Otherwise a non-digit character anywhere else would result in a {@link NumberFormatException}.
     * </p>
     * @param chars the byte-array containing the characters.
     * @param start the first position of the range to consider.
     * @param end the position after the last in the range to consider.
     * @return any integer.
     * @throws NumberFormatException if the input range does not contain an integer.
     */
    public static int parseInt(final byte[] chars, final int start, final int end) {
        if (end <= start) {
            return 0;
        } else {
            final int sign = chars[start] == '-' ? -1  : (chars[start] == '+' ? 1 : 0);
            if (sign != 0 && end == start + 1) {
                throw new NumberFormatException("not a digit (" + (char) chars[start] + ") in " + new String(chars, start, end - start));
            }
            int result = 0;
            for (int i = sign == 0 ? start : start + 1; i < end; i++) {
                final byte digitByte = chars[i];
                if (digitByte < '0' || digitByte > '9') {
                    throw new NumberFormatException("not a digit (" + (char) chars[i] + ") in " + new String(chars, start, end - start));
                }
                result = result * 10 + chars[i] - '0';
            }
            return sign >= 0 ? result : -result;
        }
    }

}

