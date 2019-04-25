package htsjdk.variant.variantcontext;

public final class NoAllele implements Allele {

    public static final NoAllele INSTANCE = new NoAllele();

    private NoAllele() {
    }

    @Override
    public boolean isReferenence() {
        return false;
    }

    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isMissing() {
        return true;
    }

    @Override
    public int index() {
        return -1;
    }

    @Override
    public AlleleSet enclosingSet() {
        return null;
    }

    @Override
    public String encode() {
        return ".";
    }

    @Override
    public boolean isSNP() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInsertion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeletion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTransposition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSymbolic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object other) {
        return other == INSTANCE;
    }

    @Override
    public int hashCode() {
        return -1;
    }

}
