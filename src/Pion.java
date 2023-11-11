import java.awt.*;

enum Pion {
    RED('R'),
    BLACK('B'),
    KING('K'),
    EMPTY(' ');

    private final char pion;

    private Point position;

    Pion(char initPion) {
        this.pion = initPion;
    }

    public char getPion() {
        return this.pion;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public static Pion getOppositePion(Pion pion) {
        if (pion == RED) return BLACK;
        return RED;
    }

    public boolean isRed() {
        return this.pion == RED.pion;
    }

    @Override
    public String toString() {
        return String.valueOf(pion);
    }
}