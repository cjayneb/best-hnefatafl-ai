enum Pion {
    RED('R'),
    BLACK('B'),
    KING('K'),
    EMPTY(' ');

    private final char pion;

    Pion(char initPion) {
        this.pion = initPion;
    }

    public char getPion() {
        return this.pion;
    }

    public static Pion getOppositePion(Pion pion) {
        if (pion == RED) return BLACK;
        return RED;
    }

    @Override
    public String toString() {
        return String.valueOf(pion);
    }
}