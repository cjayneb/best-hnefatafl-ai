enum Pion {
    ROUGE('R'),
    NOIR('N'),
    ROI('R'),
    VIDE(' ');

    private final char pion;

    Pion(char initPion) {
        this.pion = initPion;
    }

    public char getPion() {
        return this.pion;
    }

    public static Pion getOppositePion(Pion pion) {
        if (pion == ROUGE) return NOIR;
        return ROUGE;
    }

    @Override
    public String toString() {
        return String.valueOf(pion);
    }
}