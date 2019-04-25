package htsjdk.variant.variantcontext;

/**
 * Subclass of Allele spezialized in representing breakend alleles.
 * <p>
 *     It does not offer any new operation, nor it is requirement for all breakend encoding alleles to be represeted by this class.
 *     It simply provides more efficient handling Breakend related methods declared in {@link Allele} when we
 *     can assue that the allele is indeed a break-end allele.
 * </p>
 */
final class BreakendAllele extends Allele {

    private final Breakend breakend;

    public static BreakendAllele of(final byte[] spec) {
        return new BreakendAllele(Breakend.of(spec), spec);
    }

    public static BreakendAllele of(final Breakend breakend) {
        return new BreakendAllele(breakend, breakend.encodeAsBytes());

    }

    private BreakendAllele(final Breakend breakend, final byte[] spec) {
        super(spec, false);
        this.breakend = breakend;
    }

    @Override
    public boolean looksLikeABreakend() {
        return true;
    }

    @Override
    public boolean looksLikeASingleBreakend() {
        return breakend.getType().isSingle();
    }

    @Override
    public Breakend asBreakend() {
        return breakend;
    }
}
