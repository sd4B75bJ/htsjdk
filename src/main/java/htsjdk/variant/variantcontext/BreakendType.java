package htsjdk.variant.variantcontext;

public enum BreakendType {
    UNSPECIFIED,
    /**
     * Single left break-end, where the adjacency extends to the right of the enclosing location.
     */
    LEFT_SINGLE(true),
    RIGHT_SINGLE(false),
    LEFT_FORWARD(true, true),
    RIGHT_FORWARD(false, true),
    LEFT_REVERSE(true, false),
    RIGHT_REVERSE(false, false);

    private boolean isLeftEnd;
    private boolean isForward;
    private boolean isRightEnd;
    private boolean isReverse;

    BreakendType() {
        isLeftEnd = isRightEnd = isForward = isReverse = false;
    }

    BreakendType(final boolean left) {
        isLeftEnd = left;
        isRightEnd = !left;
        isForward = isReverse = false;
    }

    BreakendType(final boolean left, final boolean forward) {
        isLeftEnd = left;
        isForward = forward;
        isRightEnd = !left;
        isReverse = !forward;
    }

    public boolean isSpecified() {
        return this != UNSPECIFIED;
    }

    public boolean isLeftEnd() {
        return isLeftEnd;
    }

    public boolean isForward() {
        return isForward;
    }

    public boolean isRightEnd() {
        return !isLeftEnd;
    }

    public boolean isReverse() {
        return !isForward;
    }

    public boolean isSingle() {
        return this == LEFT_SINGLE || this == RIGHT_SINGLE;
    }

    /**
     * Returns the type for the mate-breakend.
     * <p>
     *     When this cannot be determined it returns {@link #UNSPECIFIED}.
     * </p>
     * @return never {@code null}.
     */
    public BreakendType mateType() {
        switch (this) {
            case LEFT_FORWARD:
                return RIGHT_FORWARD;
            case RIGHT_FORWARD:
                return LEFT_FORWARD;
            case LEFT_REVERSE:
                return RIGHT_REVERSE;
            case RIGHT_REVERSE:
                return LEFT_REVERSE;
            default:
            //case UNSPECIFIED:
            //case LEFT_SINGLE:
            //case RIGHT_SINGLE:
                return UNSPECIFIED;
        }
    }
}
