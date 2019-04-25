package htsjdk.variant.variantcontext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SymbolicAlleleType implements CharSequence {

    private static final Map<String, SymbolicAlleleType> instances = new ConcurrentHashMap<>();

    /**
     * Standard symbolic allele types and their mapping to structural variant types.
     */
    public static final SymbolicAlleleType DEL = of("DEL", StructuralVariantType.DEL);
    public static final SymbolicAlleleType INS = of("INS", StructuralVariantType.INS);
    public static final SymbolicAlleleType DUP = of("DUP", StructuralVariantType.DUP);
    public static final SymbolicAlleleType INV = of("INV", StructuralVariantType.INV);
    public static final SymbolicAlleleType CNV = of("CNV", StructuralVariantType.CNV);
    public static final SymbolicAlleleType BND = of("BND", StructuralVariantType.BND);
    public static final SymbolicAlleleType DUP_TANDEM = of("DUP:TANDEM", StructuralVariantType.DUP);
    public static final SymbolicAlleleType DEL_ME = of("DEL:ME", StructuralVariantType.DEL);
    public static final SymbolicAlleleType INS_ME = of("INS:ME", StructuralVariantType.INS);

    public static SymbolicAlleleType of(final String name) {
        validateName(name);
        return instances.computeIfAbsent(name, SymbolicAlleleType::new);
    }

    public static SymbolicAlleleType of(final String name, final StructuralVariantType type) {
        validateName(name);
        return instances.computeIfAbsent(name, (name_) -> new SymbolicAlleleType(name_, type));
    }

    private static void validateName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("the name cannot be null");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        } else {
            int lastColon = -1;
            final int length = name.length();
            for (int i = 0; i < length; i++) {
                final char ch = name.charAt(i);
                if (ch == ':') {
                    if (lastColon == i - 1) {
                        throw new IllegalArgumentException("empty component in symbolic id: " + name);
                    } else {
                        lastColon = i;
                    }
                } else if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) {
                    if (ch == '[' || ch == ']' || ch == '<' || ch == '>' || ch == '=' || Character.isWhitespace(ch)) {
                        throw new IllegalArgumentException("invalid character found in name: " + name);
                    }
                }
            }
            if (lastColon == length - 1) {
                throw new IllegalArgumentException("empty component in symbolic name: " + name);
            }
        }
    }

    private final String value;
    private final Optional<StructuralVariantType> svType;

    private SymbolicAlleleType(final String name) {
        value = name;
        svType = Optional.empty();
    }

    private SymbolicAlleleType(final String name, final StructuralVariantType svType) {
        this.value = name;
        this.svType = Optional.ofNullable(svType);
    }

    public boolean isSubtypeOf(final SymbolicAlleleType other) {
        return other != null
                && value.startsWith(other.value)
                && (other.value.length() == value.length() || other.value.charAt(value.length()) == ':');
    }

    public boolean isSupertypeOf(final SymbolicAlleleType other) {
        return other != null
                && other.value.startsWith(this.value)
                && (other.value.length() == value.length() || value.charAt(other.value.length()) == ':');
    }

    /**
     * Returns the symbollic allele ID  of the "super" defined as the one resulting of reminig the last part of the current
     * ID. When the current ID only has one part (i.e. is a top type) then we return {@code null}.
     * @return possibly {@code null}.
     */
    public SymbolicAlleleType supertype() {
        final int lastColonIndex = value.lastIndexOf(':');
        return lastColonIndex < 0 ? null : of(value.substring(0, lastColonIndex));
    }

    /**
     * Returns the symbolic allele that results of appending additional parts to the ID.
     * @param suffix the suffix to append. This must not start with a ':' (as it is automatically added).
     * @return never {@code null}.
     */
    public SymbolicAlleleType subtype(final String suffix) {
        return of(value + ':' + suffix);
    }

    public StructuralVariantType getStructuralVariantType() {
        return svType.orElseGet(() -> {
            final SymbolicAlleleType zuper = supertype();
            return zuper == null ? null : zuper.getStructuralVariantType();
        });
    }


    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return value;
    }
}
