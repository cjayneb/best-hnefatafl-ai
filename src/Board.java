import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    public static final int BOARD_SIZE = 13;

    private Pion[][] board;

    public Board() {
        this.board = new Pion[BOARD_SIZE][BOARD_SIZE];
    }

    public Pion[][] getBoard() {
        return board;
    }

    public ArrayList<Move> getPossibleMoves(Pion player) {
        Map<String, List<int[]>> allPossibleMoves = new HashMap<>();
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if ((board[x][y] == Pion.ROUGE && player == Pion.ROUGE)
                        || ((board[x][y] == Pion.NOIR || board[x][y] == Pion.ROI) && player != Pion.ROUGE)) {
                    ArrayList<Move> moves = getPossibleMoves(x, y);
                    possibleMoves.addAll(moves);

//                    String position = "x" + x + " y" + y;
//                    allPossibleMoves.put(position, possibleMoves);
                }
            }
        }

        return possibleMoves;
    }

    private boolean isPlayerPion(int x, int y, Pion player) {
        if (x < 0 || y < 0 || x > 12 || y > 12) {
            return false;
        }
        if (board[x][y] == Pion.VIDE && ((x == 6 && y == 6) || estCoin(x, y))) {
            return true;
        }
        return (
                (board[x][y] == Pion.ROUGE && player == Pion.ROUGE)
                        || ((board[x][y] == Pion.NOIR || board[x][y] == Pion.ROI) && player != Pion.ROUGE)
        );
    }

    private ArrayList<Move> getPossibleMoves(int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        boolean roi = board[x][y] == Pion.ROI;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x + dx;
            int newY = y + dy;

            // Parce que le roi peut aller dans les coins
            if (roi) {
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == Pion.VIDE) {
                    Move move = new Move(x, y, newX, newY);
                    possibleMoves.add(move);
                    //possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            }
            else {
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == Pion.VIDE && !estCoin(newX, newY)) {
                    if (newX == 6 && newY == 6) {
                        newX += dx;
                        newY += dy;
                        continue;
                    }
                    Move move = new Move(x, y, newX, newY);
                    possibleMoves.add(move);
                    //possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            }
        }
        return possibleMoves;
    }

    private static boolean estCoin(int x, int y) {
        return (x == 0 && (y == 0 || y == 12)) || (x == 12 && (y == 0 || y == 12));
    }

    public void setPionOnBoard(Move move) {
        Pion p = board[move.old_position.x][move.old_position.y];
        board[move.old_position.x][move.old_position.y] = Pion.VIDE;
        board[move.new_position.x][move.new_position.y] = p;
        checkForCapture(move, p, new ArrayList<>());
    }
    public void setPionOnBoard(Move move, ArrayList<Move> pionsToRevive) {
        Pion p = board[move.old_position.x][move.old_position.y];
        board[move.old_position.x][move.old_position.y] = Pion.VIDE;
        board[move.new_position.x][move.new_position.y] = p;
        //checkForCapture(move, p, pionsToRevive);
    }

    public void revertMove(Move move, ArrayList<Move> pionsToRevive) {
        Pion p = board[move.new_position.x][move.new_position.y];
        board[move.new_position.x][move.new_position.y] = Pion.VIDE;
        board[move.old_position.x][move.old_position.y] = p;
//        for (Move m : pionsToRevive) {
//            board[m.new_position.x][m.new_position.y] = Pion.getOppositePion(p);
//        }
    }

    public void setBoard(String[] input) {
        int index = 0;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = getPion(input[index++]);
            }
        }
    }

    public boolean gameIsDone() {
        return false;
    }

    public int evaluate(Pion pion) {
        if (pion == Pion.ROUGE) {
            return evaluateRed();
        }
        return evaluateBlack();
    }

    private int evaluateRed() {
        return (int)(Math.random()*100);
    }

    private int evaluateBlack() {
        return (int)(Math.random()*100);
    }

    private void checkForCapture(Move move, Pion pion, ArrayList<Move> pionsToRevive) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int x = move.new_position.x + dx;
            int y = move.new_position.y + dy;

            while (y >= 0 && y < board.length && x >= 0 && x < board.length) {
                int differenceY = Math.abs(move.new_position.y - y);
                int differenceX = Math.abs(move.new_position.x - x);
                if ((differenceY % 2 == 1 || differenceX % 2 == 1) && (isPlayerPion(x, y, pion) || board[x][y] == Pion.VIDE)) {
                    break;
                }

                int nextX = x + dx;
                int nextY = y + dy;

                if (!isPlayerPion(x, y, pion) && isPlayerPion(nextX, nextY, pion)) {
                    pionsToRevive.add(new Move(x, y, x, y));
                    board[x][y] = Pion.VIDE;
                }

                x += dx;
                y += dy;
            }
        }
    }

    private Pion getPion(String pion) {
        switch (pion) {
            case "4": return Pion.ROUGE;
            case "2": return Pion.NOIR;
            case "5": return Pion.ROI;
            default: return Pion.VIDE;
        }
    }

    public void show() {
        System.out.println("BOARD : ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(String.format("%1$-" + 3 + "s", (BOARD_SIZE - i)));
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print("[" + board[i][j] + "] ");
            }
            System.out.println();
        }
        char[] alphabet = "ABCDEFGHIJKLM".toCharArray();
        System.out.print("   ");
        for (int j = 0; j < BOARD_SIZE; j++) {
            System.out.print(" " + alphabet[j] + "  ");
        }
        System.out.println();
    }
}