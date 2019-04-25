package htsjdk.variant.variantcontext;

class SingleUnlocalizedBreakend implements Breakend {

    private byte[] bases;
    private final BreakendType type;

    SingleUnlocalizedBreakend(final byte[] bases, final BreakendType type) {
        if (type == null || !type.isSingle()) {
            throw new IllegalArgumentException("bad type: " + type);
        }
        this.bases = bases;
        this.type = type;
    }

    @Override
    public BreakendType getType() {
        return type;
    }

    @Override
    public String getMateContig() {
        return null;
    }

    @Override
    public int getMatePosition() {
        return -1;
    }

    @Override
    public String getContig() {
        return null;
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public int getEnd() {
        return 0;
    }

    @Override
    public boolean mateIsOnSameContig() {
        throw new UnsupportedOperationException("this is a single breakpoint thus it does not have a mate");
    }

    @Override
    public Breakend getMate() {
        return null;
    }
}
