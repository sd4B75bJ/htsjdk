package htsjdk.variant.variantcontext;

import java.util.List;

public interface AlleleSet {

    Allele referenceAllele();

    List<Allele> alternativeAlleles();

    List<Allele> allAlleles();

    default int numberOfAlternativeAlleles() {
        return alternativeAlleles().size();
    }

    default int numberOfAlleles() {
        return allAlleles().size();
    }

    default int indexOfAllele(final Allele allele) {
       return allAlleles().indexOf(allele);
    }

    default boolean isPolymorphic() {
       return numberOfAlleles() > 0;
    }

    default boolean isMonomorphic() {
        return numberOfAlleles() == 1;
    }
}
