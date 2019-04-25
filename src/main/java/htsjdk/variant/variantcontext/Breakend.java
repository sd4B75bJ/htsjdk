package htsjdk.variant.variantcontext;

import htsjdk.samtools.util.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents the information about a breakend representable in an VCF allele spec.
 */
public abstract class Breakend implements Serializable {

    private final BreakendType type;
    private final byte[] bases;

    private Breakend(final BreakendType type, final byte[] bases) {
        this.type = type;
        this.bases = bases;
    }

    /**
     * Checks whether an allele spec byte sequence is likely to be a break-end spec.
     * <p>
     *     In order to keep the code efficient, this does not make a full check but
     *     if it return true most likely a call to  {@link Breakend#of} won't fail on the same array, if we assume
     *     that such spec came from a well-formed VCF.
     * </p>
     * @param spec the allele representation bases as a byte array.
     */
    public static boolean looksLikeABreakend(final byte[] spec) {
        if (spec == null || spec.length < 2) {
            return false;
        }
        final char first = (char) spec[0];
        final char last = (char) spec[spec.length - 1];
        if (first == '.' && last != '.') {
            return true;
        } else if (last == '.' && first != '.') {
            return true;
        } else if ((first == '[' || first == ']') && last != '[' && last != ']') {
            return true;
        } else if (first != '[' && first != ']' && (last == '[' || last == ']')) {
            return true;
        }
        return false;
    }

    public boolean looksLikeASingleBreakend(final byte[] spec) {
        if (spec == null || spec.length < 2) {
            return false;
        } else {
            final char first = (char) spec[0];
            final char last = (char) spec[spec.length - 1];
            if (first == '.' && last != '.') {
                return true;
            } else {
                return first != '.' && last != '.';
            }
        }
    }

    public static Breakend single(final BreakendType type, final byte[] bases) {
        if (type == null || !type.isSingle()) {
            throw new IllegalArgumentException("bad type");
        }
        if (bases == null || bases.length < 1) {
            throw new IllegalArgumentException("bad bases array");
        }
        if (!SequenceUtil.areValidIupacCodes(bases) || ArrayUtils.contains(bases, (byte) '.')) {
            throw new IllegalArgumentException("invalid bases");
        }
        return new SimpleSingleBreakend(type, bases);
    }

    public static Breakend paired(final BreakendType type, final byte[] bases, final String mateContig, final int matePosition) {
        if (type == null || type.isSingle() || type == BreakendType.UNSPECIFIED) {
            throw new IllegalArgumentException("bad type");
        }
        if (bases == null) {
            throw new IllegalArgumentException("bad bases array");
        }
        if (!SequenceUtil.areValidIupacCodes(bases) || ArrayUtils.contains(bases, (byte) '.')) {
            throw new IllegalArgumentException("invalid bases");
        }
        if (mateContig == null) {
            throw new IllegalArgumentException("mate contig cannot be null");
        }
        if (matePosition <= 0) {
            throw new IllegalArgumentException("mate position cannot be negative or 0");
        }
        return new SimplePairedBreakend(type, bases, mateContig, matePosition);
    }

    /**
     * Returns the allele representation of a breakend.
     * @return never {@code null}.
     */
    public Allele asAllele() {
        return BreakendAllele.of(this);
    }


    public static Breakend of(final byte[] spec) {
        if (spec == null || spec.length < 2) {
            throw new IllegalArgumentException("invalid bases: " + (spec == null ? "null" : new String(spec)));
        }
        final int length = spec.length;
        for (int i = 0; i < length; i++) {
            final char ch = (char) spec[i];
            if (ch == '[' || ch == ']') {
                return ofPaired(spec, i, ch);
            }
        }
        return ofSingle(spec);
    }

    private static Breakend ofSingle(final byte[] spec) {
        final BreakendType type = spec[0] == '.' ? BreakendType.RIGHT_SINGLE : BreakendType.LEFT_SINGLE;
        if (!type.isLeftEnd() && spec[spec.length - 1] != '.') {
            throw new IllegalArgumentException("invalid spec: " + new String(spec));
        }
        final byte[] bases = type.isLeftEnd() ? Arrays.copyOfRange(spec, 1, spec.length) : Arrays.copyOfRange(spec, 0, spec.length);
        if (!SequenceUtil.areValidIupacCodes(bases) || org.apache.commons.lang3.ArrayUtils.contains(bases, (byte) '.')) {
            throw new IllegalArgumentException("invalid spec: " + new String(spec));
        }
        return new SimpleSingleBreakend(type, bases);
    }

    /**
     * Proceeds assuming the spec is a mated (non-single) break-end.
     * It is provided the correct location for the first braket and its value.
     * @param spec the full byte array spec for the breakend.
     * @param firstBraketOffset the offset in {@ode spec} of the first bracket.
     * @param firstBraket the first bracket value itself.
     * @return never {@code null}.
     */
    private static Breakend ofPaired(final byte[] spec,
                                     final int firstBraketOffset,
                                     final char firstBraket) {
        int secondBraketIndex = -1;
        int lastColonIndex = -1;
        int matePosition = -1;
        int length = spec.length;
        for (int i = firstBraketOffset + 1; i < length; i++) {
            final char ch = (char) spec[i];
            if (ch == firstBraket) { // find the second braket.
               secondBraketIndex = i;
               break;
            } else if (ch == ':') { // we capture the last colon between brakets.
                lastColonIndex = i;
                matePosition = 0;
            } else if (matePosition >= 0) { // we calculate the numeric value after the last colon.
                final int digitValue = ch - '0';
                matePosition = digitValue >= 0 && digitValue < 10 ? matePosition * 10 + digitValue : -1;
            }
        }
        if (secondBraketIndex < 0
                || lastColonIndex < 0
                || matePosition <= 0
                || (firstBraketOffset == 0) == (secondBraketIndex == length -1)) {
            throw new IllegalArgumentException();
        } else {
            final boolean isLeft = firstBraketOffset > 0;
            final boolean isForward = firstBraket == '[';
            final byte[] bases = isLeft
                    ? Arrays.copyOfRange(spec, 0, firstBraketOffset) :
                    Arrays.copyOfRange(spec, secondBraketIndex + 1, length);
            if (!SequenceUtil.areValidIupacCodes(bases) || ArrayUtils.contains(bases, (byte) '.')) {
                throw new IllegalArgumentException();
            } else {
                final BreakendType type = isLeft
                        ? (isForward ? BreakendType.LEFT_FORWARD : BreakendType.LEFT_REVERSE)
                        : (isForward ? BreakendType.RIGHT_FORWARD : BreakendType.RIGHT_REVERSE);
                return new SimplePairedBreakend(type, bases, new String(spec, firstBraket + 1, lastColonIndex - firstBraket - 1), matePosition);
            }
        }
    }

    public BreakendType getType() {
        return type;
    }

    public byte[] getBases() {
        return bases;
    }

    public boolean isSingle() {
        return getType().isSingle();
    }

    public boolean hasMate() {
        return !getType().isSingle();
    }

    /**
     * Returns the contig for the mate break-end if known. Otherwise it return {@code null}, for example if this is a
     * single typed breakend.
     *
     * @return might be {@code null}
     */
    public abstract String getMateContig();

    /**
     * Position of the mate break-end using 1-based indexing.
     * <p>
     *     When there is no mate this will return -1.
     * </p>
     * @return -1 or 1 or greater.
     */
    public abstract int getMatePosition();

    /**
     * Returns a 1-bp sized locatable indicating the contig and position of the mate-break end.
     * @return never {@code null}.
     */
    public Locatable getMateLocation() {
        if (hasMate()) {
            return new Locatable() {

                @Override
                public String getContig() {
                    return getMateContig();
                }

                @Override
                public int getStart() {
                    return getMatePosition();
                }

                @Override
                public int getEnd() {
                    return getMatePosition();
                }
            };
        } else {
            return null;
        }
    }

    public StringBuilder appendTo(final StringBuilder builder) {
        if (type.isSingle()) {
            builder.ensureCapacity(builder.length() + bases.length + 1);
            if (type.isRightEnd()) {
                builder.append('.');
            }
            for (final byte b : bases) {
                builder.append((char) b);
            }
            if (type.isLeftEnd()) {
                builder.append('.');
            }
        } else {
            final String mateContig = getMateContig();
            final int position = getMatePosition();
            // 14 = [ + ] + .? + : + length(contig) + max_digits_int (10)
            builder.ensureCapacity(builder.length() + bases.length + mateContig.length() + 14);
            if (type.isLeftEnd()) {
                if (bases.length == 0) { // insert before contig start.
                    builder.append('.');
                } else {
                    for (final byte b : bases) {
                        builder.append((char) b);
                    }
                }
            }
            final char bracket = type.isForward() ? '[' : ']';
            builder.append(bracket)
                   .append(mateContig)
                   .append(':')
                   .append(position)
                   .append(bracket);
            if (type.isRightEnd()) {
                for (final byte b : bases) {
                    builder.append((char) b);
                }
            }
        }
        return builder;
    }

    @Override
    public String toString() {
        return appendTo(new StringBuilder(30)).toString();
    }

    public String encodeAsString() {
        return toString();
    }

    public byte[] encodeAsBytes() {
        return type.isSingle()
                ? encodeAsBytesSingle()
                : encodeAsBytesStandard();
    }

    private byte[] encodeAsBytesSingle() {
        final byte[] result;
        if (type.isLeftEnd()) {
            result = new byte[bases.length + 1];
            result[0] = '.';
            System.arraycopy(bases, 0, result, 1, bases.length);
        } else {
            result = Arrays.copyOf(bases, bases.length + 1);
            result[bases.length] = '.';
        }
        return result;
    }

    private byte[] encodeAsBytesStandard() {
        int offset = 0;
        final byte[] mateContig = getMateContig().getBytes();
        final int matePosition = getMatePosition();
        final int positionDigits = numberOfDigits(matePosition);
        final byte[] result = new byte[Math.min(bases.length, 1) + positionDigits + mateContig.length + 3];
        if (type.isLeftEnd()) {
            if (bases.length == 0) {
                result[offset++] = '.';
            } else {
                System.arraycopy(bases, 0, result, 0, bases.length);
            }
        }
        final byte bracket = (byte) (type.isForward() ? ']' : '[');
        result[offset++] = bracket;
        System.arraycopy(mateContig, 0, result, offset, mateContig.length);
        result[offset += mateContig.length] = ':';
        offset += positionDigits - 1;
        for (int i = 0, q = matePosition; i < positionDigits; i++) {
            final int c = q / 10;
            final int r = q - c * 10;
            result[offset--] = (byte) ('0' + r);
            q = c;
        }
        offset += positionDigits;
        result[offset++] = bracket;
        if (type.isRightEnd()) {
            System.arraycopy(bases, 0, result, offset, bases.length);
        }
        return result;
    }

    /**
     * Determines the number of digits required to represent
     * a zero or positive integer.
     * <p>
     *     Based on a public benchmark this seems to be the fastest approach
     *     {@see  https://www.baeldung.com/java-number-of-digits-in-int}
     * </p>
     * <p>It slightly bias against small indexes as usually genomic positions
     * are much larger than say 1000.</p>
     *
     * @param i the target integer.
     * @return 1 to 10.
     */
    private static int numberOfDigits(final int i) {
        if (i < 1000000) {
            if (i >= 10000) {
                return i < 100000 ? 5 : 6;
            } else if (i >= 100) {
                return i < 1000 ? 3 : 4;
            } else {
                return i < 10 ? 1 : 2;
            }
        } else if (i < 100000000) {
            return i < 10000000 ? 7 : 8;
        } else {
            return i < 1000000000 ? 9 : 10;
        }
    }

    private static class SimplePairedBreakend extends Breakend {

        private static final long serialVersionUID = 1;

        private final String mateContig;
        private final int matePosition;

        private SimplePairedBreakend(final BreakendType type, final byte[] bases, final String mateContig, final int matePosition) {
            super(type, bases);
            this.mateContig = mateContig;
            this.matePosition = matePosition;
        }

        @Override
        public String getMateContig() {
            return mateContig;
        }

        @Override
        public int getMatePosition() {
            return matePosition;
        }
    }

    private static class SimpleSingleBreakend extends Breakend {

        private static final long serialVersionUID = 1;

        private SimpleSingleBreakend(final BreakendType type, final byte[] bases) {
            super(type, bases);
        }

        @Override
        public String getMateContig() {
            return null;
        }

        @Override
        public int getMatePosition() {
            return -1;
        }
    }

}

