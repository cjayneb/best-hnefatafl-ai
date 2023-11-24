import java.util.*;

class CPUPlayer {
    private static final int MIN_DEPTH = 2;
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
        int nombrePionRouge = board.getNumberOfPionsRouge();
        int nombrePionNoir = board.getNumberOfPionsNoir();
        ArrayList<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int rootNodesCounter = 0;
        startTime = System.currentTimeMillis();

        int remainingMoves = board.getPossibleMoves(this.cpu).size();

        for (Move currentMove : board.getPossibleMoves(this.cpu)) {
            numExploredNodes = 0;
            numExploredNodes++;
            rootNodesCounter++;

            Board copy = new Board(board);
            copy.setPionOnBoard(currentMove);

            int maxDepth = determineDynamicDepth(remainingMoves, MIN_DEPTH);

            int score = minimaxAB(copy, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, nombrePionRouge, nombrePionNoir);
            currentMove.setNumOfNodes(numExploredNodes);

            System.out.println(currentMove.indexToString() + "| Score: " + score + " | nodes: " + numExploredNodes + " | depth: " + maxDepth);
            if (score == 100 && numExploredNodes == 1) {
                bestMoves.clear();
                bestMoves.add(currentMove);
                break;
            }

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(currentMove);
            } else if (score == bestScore) {
                bestMoves.add(currentMove);
            }

            remainingMoves--;

            // Check if time limit exceeded
            if (System.currentTimeMillis() - startTime >= TIME_LIMIT + 20) {
                System.out.println("\nTime limit exceeded!");
                break;
            }
        }
        System.out.println("-> Moves traversed: " + rootNodesCounter + " / " + board.getPossibleMoves(this.cpu).size() + " in " + (System.currentTimeMillis() - startTime) + "ms");
        return bestMoves;
    }

    private int determineDynamicDepth(int remainingMoves, int minDepth) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int maxDepth = minDepth;

        if (remainingMoves > 20 && elapsedTime < 0.8 * TIME_LIMIT) {
            maxDepth = 3;
        }

        return maxDepth;
    }

    public int minimaxAB(Board board, int depth, int alpha, int beta, boolean maximizingPlayer, int nombrePionRouge, int nombrePionNoir) {
        if (depth == 0 || board.gameIsDone() || System.currentTimeMillis() - startTime >= TIME_LIMIT) {
            return board.evaluate(this.cpu, nombrePionRouge, nombrePionNoir);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move currentMove : board.getPossibleMoves(this.cpu)) {
                numExploredNodes++;
                Board copy = new Board(board);
                copy.setPionOnBoard(currentMove);

                int eval = minimaxAB(copy, depth - 1, alpha, beta, false, nombrePionRouge, nombrePionNoir);
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

                int eval = minimaxAB(copy, depth - 1, alpha, beta, true, nombrePionRouge, nombrePionNoir);
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
