package htsjdk.variant.variantcontext;

import java.util.List;

public class VariantAlleleSet implements AlleleSet {


    private final Allele referenceAllele;
    private final List<Allele> allAlleles;
    private final List<Allele> altAlleles;

    private VariantAlleleSet(final Allele reference, final Allele ... alternativeAlleles) {
        this.reference = reference;
        this.alternatives = alternativeAlleles;
    }

    static VariantAlleleSet of(final String contig, final int position, final List<Allele> refAndAlternatives) {
        final

        final Allele referenceAllele = VariantAllele.reference(contig, position, refEncoding);


    }

    @Override
    public Allele referenceAllele() {
        return reference;
    }

    @Override
    public List<Allele> alternativeAlleles() {
        return null;
    }

    @Override
    public List<Allele> allAlleles() {
        return null;
    }
}
