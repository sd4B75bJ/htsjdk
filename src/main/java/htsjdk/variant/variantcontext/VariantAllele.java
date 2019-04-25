package htsjdk.variant.variantcontext;

class VariantAllele extends Allele {

    class ReferenceVariantAllele extends VariantAllele {

        ReferenceVariantAllele
    }


    static VariantAllele reference(final String contig, final int position, final String bases) {
        return
    }

    protected VariantAllele(final String bases, boolean isRef) {
        super(bases, isRef);
    }

    public boolean isAlternative() {
        return isNonReference();
    }




    boolean isStructural();

    boolean isSnp();

    boolean isMnp();

    boolean isInsertion();

    boolean isDeletion();

    default boolean isIndel() {
        return isInsertion() || isDeletion();
    }

    boolean isMissing();

    /**
     * Check whether this allele is or has been specified as a Break-end. as per the 5.2.3 section of the VCF 4.3 spec (e.g. "G[chr1:124121[", "G.")
     * Despite that some other allele types such as deletion may be interpreted as a break-end this method will return
     * false on those.
     *
     */
    boolean isBreakend();

    /**
     * Returns a break-end interpreation of this allele even for non explicit break-end types.
     * @return
     */
    Breakend asBreakend();

    /**
     *
     *
     * @return never returns {@code null} but perhaps an 0-length array.
     */
    byte[] getBases();

    default int numberOfBases() {
        return getBases().length;
    }

    default void copyBases(final int offset, final byte[] dest, final int destOffset, final int length) {
        if (offset < 0) {
            throw new IllegalArgumentException("the offset cannot be negative");
        } else if (dest == null) {
            throw new IllegalArgumentException("the input destination cannot be null");
        } else if (destOffset < 0) {
            throw new IllegalArgumentException("the destination offset cannot be negative");
        }
        final byte[] bases = getBases();
        final int stop = offset + length;
        if (stop > bases.length) {
            throw new IllegalArgumentException("length goes beyond the end");
        }
        if (destOffset + length > dest.length) {
            throw new IllegalArgumentException("length goes beyond destination end");
        }
        System.arraycopy(bases, offset, dest, destOffset, length);
    }

    default void copyBases(final byte[] dest, final int destOffset) {
        copyBases(0, dest, destOffset, numberOfBases());
    }

    default void copyBases(final byte[] dest) {
        copyBases(0, dest, 0, numberOfBases());
    }

    String encode();

}
