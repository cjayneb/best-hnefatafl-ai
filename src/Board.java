import java.util.ArrayList;

public class Board {
    public static final int BOARD_SIZE = 13;
    private Pion[][] board;

    public Board() {
        this.board = new Pion[BOARD_SIZE][BOARD_SIZE];
    }

    public ArrayList<Move> getPossibleMoves() {
        return new ArrayList<>();
    }

    public void setPionOnBoard(Move move, Pion pion) {

    }

    public int evaluate(Pion pion) {
        return 0;
    }

    public boolean isFull() {
        return true;
    }
}