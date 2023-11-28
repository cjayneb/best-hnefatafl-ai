import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class Board {
    public static final int BOARD_SIZE = 13;

    private Pion[][] board;

    private static final int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public Board() {
        this.board = new Pion[BOARD_SIZE][BOARD_SIZE];
    }

    public Pion[][] getBoard() {
        return board;
    }

    public Board(Board board) {
        this.board = new Pion[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < board.getBoard().length; x++) {
            for (int y = 0; y < board.getBoard().length; y++) {
                this.board[x][y] = board.getBoard()[x][y];
            }
        }
    }

    public void initializeBoard(String[] input) {
        int index = 0;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = getPion(input[index++]);
            }
        }
    }

    public ArrayList<Move> getPossibleMoves(Pion player) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (isOnTheSameTeam(x, y, player)) {
                    ArrayList<Move> moves = getPossibleMoves(x, y);
                    possibleMoves.addAll(moves);
                }
            }
        }

        return possibleMoves;
    }

    private ArrayList<Move> getPossibleMoves(int x, int y) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x + dx;
            int newY = y + dy;

            // Parce que le roi peut aller dans les coins
            if (isKing(x, y)) {
                while (!isOutOfBoard(newX, newY) && isEmpty(newX, newY)) {
                    Move move = new Move(x, y, newX, newY);
                    possibleMoves.add(move);
                    newX += dx;
                    newY += dy;
                }
            }
            else {
                while (!isOutOfBoard(newX, newY) && isEmpty(newX, newY) && !isCorner(newX, newY)) {
                    if (isThrone(newX, newY)) {
                        if (isKing(newX, newY)) {
                            break;
                        }
                        newX += dx;
                        newY += dy;
                        continue;
                    }
                    Move move = new Move(x, y, newX, newY);
                    possibleMoves.add(move);
                    newX += dx;
                    newY += dy;
                }
            }
        }
        return possibleMoves;
    }

    public void setPionOnBoard(Move move) {
        Pion p = board[move.old_position.x][move.old_position.y];
        board[move.old_position.x][move.old_position.y] = Pion.EMPTY;
        board[move.new_position.x][move.new_position.y] = p;
        checkForCapture(move, p);
    }

    public boolean gameIsDone() {
        return !kingFound() || kingInCorner() || noPionLeft() || !canRedStillPlay() || !canBlackStillPlay();
    }

    private boolean noPionLeft() {
        int numRedPions = 0;
        int numBlackPions = 0;

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (isRed(x, y)) {
                    numRedPions++;
                }
                if (isBlack(x, y)) {
                    numBlackPions++;
                }
            }
        }

        return numRedPions == 0 || numBlackPions == 0;
    }

    private Optional<Point> getKingPosition() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (isKing(x, y)) {
                    return Optional.of(new Point(x, y));
                }
            }
        }
        return Optional.empty();
    }

    private boolean kingFound() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (isKing(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int evaluate(Pion pion, int nombrePionRouge, int nombrePionNoir) {
        if (pion.isRed()) {
            return -evaluateBlack(nombrePionRouge, nombrePionNoir, pion);
        }
        return evaluateBlack(nombrePionRouge, nombrePionNoir, pion);
    }

    private int evaluateBlack(int nombrePionRouge, int nombrePionNoir, Pion pion) {
        // Check si king arrive dans un coin
        if (kingInCorner()) {
            return 100;
        }

        //Check si le roi est toujours sur le board ou si il s'est fait capturer
        if (!isKingOnBoard()) {
            return -100;
        }
        return getEvaluateGlobalNoir(nombrePionRouge, nombrePionNoir, pion); //Comprends hasKingclearPath et pions mangés
    }

    private boolean kingInCorner() {
        return board[0][0] == Pion.KING || board[0][BOARD_SIZE - 1] == Pion.KING ||
                board[BOARD_SIZE - 1][0] == Pion.KING || board[BOARD_SIZE - 1][BOARD_SIZE - 1] == Pion.KING;
    }

    private boolean hasKingPathToCorner() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == Pion.KING) {
                    // Check le roi aux coins
                    if (isPathClear(i, j, 0, 0) ||
                            isPathClear(i, j, 0, BOARD_SIZE - 1) ||
                            isPathClear(i, j, BOARD_SIZE - 1, 0) ||
                            isPathClear(i, j, BOARD_SIZE - 1, BOARD_SIZE - 1)) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean isKingOnBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == Pion.KING) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPathClear(int startX, int startY, int endX, int endY) {
        // A l'horizontale
        if (startX == endX) {
            int start = Math.min(startY, endY);
            int end = Math.max(startY, endY);
            for (int y = start + 1; y < end; y++) {
                if (board[startX][y] != Pion.EMPTY) {
                    return false;
                }
            }
            return true;
        }

        // A la verticale
        if (startY == endY) {
            int start = Math.min(startX, endX);
            int end = Math.max(startX, endX);
            for (int x = start + 1; x < end; x++) {
                if (board[x][startY] != Pion.EMPTY) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }


    private void checkForCapture(Move move, Pion pion) {
        int x = move.new_position.x;
        int y = move.new_position.y;
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = move.new_position.x + dx;
            int newY = move.new_position.y + dy;

            int nextX = newX + dx;
            int nextY = newY + dy;

            if (isElligibleForCapture(newX, newY, dx, dy)) {
                if (!isKing(newX, newY) && !isOnTheSameTeam(newX, newY, pion) && canBeUsedToCapture(nextX, nextY, pion)) {
                    board[newX][newY] = Pion.EMPTY;
                }
                if (isRed(x, y) && isKing(newX, newY)) {
                    checkForKingCapture(newX, newY);
                }
            }
        }
    }

    private boolean isElligibleForCapture(int newX, int newY, int dx, int dy) {
        return !isOutOfBoard(newX, newY)
                && (!isEmpty(newX, newY))
                && !isOnTheSameTeam(newX - dx, newY - dy, board[newX][newY]);
    }

    private void checkForKingCapture(int x, int y) {
        boolean kingIsCaptured = true;
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x + dx;
            int newY = y + dy;

            if (!canBeUsedToCaptureKing(newX, newY)) {
                kingIsCaptured = false;
                break;
            }
        }
        if (kingIsCaptured) {
            board[x][y] = Pion.EMPTY;
        }
    }

    private boolean canBeUsedToCaptureKing(int x, int y) {
        return isOutOfBoard(x, y) || isCorner(x, y) || isThrone(x, y) || isRed(x, y);
    }

    private boolean canBeUsedToCapture(int x, int y, Pion capturer) {
        if (isOutOfBoard(x, y)) {
            return false;
        }
        if (isThrone(x, y) || isCorner(x, y)) {
            return true;
        }
        return isOnTheSameTeam(x, y, capturer);
    }

    private boolean isOnTheSameTeam(int x, int y, Pion player) {
        return (board[x][y] == Pion.RED && player == Pion.RED)
                || ((board[x][y] == Pion.BLACK || board[x][y] == Pion.KING) && player != Pion.RED);
    }

    private boolean isKing(int x, int y) {
        return board[x][y] == Pion.KING;
    }

    private boolean isCorner(int x, int y) {
        int lastBoardIndex = board.length - 1;
        return (x == 0 && (y == 0 || y == lastBoardIndex)) || (x == lastBoardIndex && (y == 0 || y == lastBoardIndex));
    }

    private boolean isThrone(int x, int y) {
        return (x == 6 && y == 6);
    }

    private boolean isEmpty(int x, int y) {
        return board[x][y] == Pion.EMPTY;
    }

    private boolean isRed(int x, int y) {
        return board[x][y] == Pion.RED;
    }

    private boolean isBlack(int x, int y) {
        return board[x][y] == Pion.BLACK || isKing(x, y);
    }

    private boolean isOutOfBoard(int x, int y) {
        return x < 0 || y < 0 || x > (board.length - 1) || y > (board.length - 1);
    }

    private Pion getPion(String pion) {
        switch (pion) {
            case "4": return Pion.RED;
            case "2": return Pion.BLACK;
            case "5": return Pion.KING;
            default: return Pion.EMPTY;
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

    public int getNumberOfPions() {
        int counter = 0;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (!isEmpty(x, y)) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int getNumberOfPionsRouge(){
        int counter = 0;
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board.length; y++){
                if(isRed(x,y)){
                    counter++;
                }
            }
        }
        return counter;
    }

    public int getNumberOfPionsNoir(){
        int counter = 0;
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board.length; y++){
                if(isBlack(x,y) && isKing(x,y)){
                    counter++;
                }
            }
        }
        return counter;
    }

    public boolean canRedStillPlay(){
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board.length; y++){
                if(isRed(x,y)){
                    if(!getPossibleMoves(x, y).isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canBlackStillPlay(){
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board.length; y++){
                if(isBlack(x,y)){
                    if(!getPossibleMoves(x, y).isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getEvaluateGlobalNoir(int nombrePionRouge, int nombrePionNoir, Pion pion){
        int eval = 0;
        eval += 2*(nombrePionRouge-getNumberOfPionsRouge());
        eval -= 10*(nombrePionNoir - getNumberOfPionsNoir()); //Plus de points parce que y a moins de pions noir que rouge

        if (pion.isRed()) {
            eval -= 5 * getNumberOfCapturersAroundKing();
        }
        if(hasKingPathToCorner()){
            eval += 40;
        }
        if(!canRedStillPlay()){
            eval = -90;
        }
        if(!canBlackStillPlay()){
            eval = 90;
        }

        return eval;
    }

    private int getNumberOfCapturersAroundKing() {
        int numOfCapturers = 0;
        Point kingPosition = getKingPosition().get();
        int kingX = kingPosition.x;
        int kingY = kingPosition.y;

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = kingX + dx;
            int newY = kingY + dy;

            if (isOutOfBoard(newX, newY) || isRed(newX, newY) || isCorner(newX, newY) || isThrone(newX, newY)) {
                numOfCapturers++;
            }
        }
        return numOfCapturers;
    }
}