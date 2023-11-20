import java.util.*;

class CPUPlayer {
    private static final int MAX_DEPTH = 2;
    private static final int TIME_LIMIT = 5000;
    private static long startTime;
    private int numExploredNodes;
    private final Pion cpu;

    public CPUPlayer(Pion cpu) {
        this.cpu = cpu;
    }

    public int getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public ArrayList<Move> getNextMoveAB(Board board) {
        ArrayList<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        startTime = System.currentTimeMillis();

        for (Move currentMove : board.getPossibleMoves(this.cpu)) {
            numExploredNodes = 0;
            numExploredNodes++;

            Board copy = new Board(board);
            copy.setPionOnBoard(currentMove);

            int score = minimaxAB(copy, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH % 2 != 0);
            currentMove.setNumOfNodes(numExploredNodes);

            System.out.println("| Score: " + score + " | nodes: " + numExploredNodes);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(currentMove);
            }
            else if (score == bestScore) {
                bestMoves.add(currentMove);
            }

            // Check if time limit exceeded
            if (System.currentTimeMillis() - startTime >= TIME_LIMIT + 200) {
                System.out.println("\nTime limit exceeded!");
                break;
            }
        }
        return bestMoves;
    }

    public int minimaxAB(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.gameIsDone() || System.currentTimeMillis() - startTime >= TIME_LIMIT) {
            return board.evaluate(this.cpu);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move currentMove : board.getPossibleMoves(this.cpu)) {
                numExploredNodes++;
                Board copy = new Board(board);
                copy.setPionOnBoard(currentMove);

                int eval = minimaxAB(copy, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;
            for (Move currentMove : board.getPossibleMoves(Pion.getOppositePion(this.cpu))) {
                numExploredNodes++;
                Board copy = new Board(board);
                copy.setPionOnBoard(currentMove);

                int eval = minimaxAB(copy, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
