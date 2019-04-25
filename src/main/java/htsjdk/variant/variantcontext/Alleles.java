package htsjdk.variant.variantcontext;

public final class Alleles {

    public s

    public static Allele of(final int index) {
        if (index == -1) {
            return NO_ALLELE;
        }
        return IndexedAlleles.of(index);
    }
}
