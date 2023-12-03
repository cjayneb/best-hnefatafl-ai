import java.util.*;

class CPUPlayer {
    private static final int MIN_DEPTH = 2;
    private static final int TIME_LIMIT = 4900;
    private static long startTime;
    private int numExploredNodes;
    private final Pion cpu;

    private Queue<Move> lastMoves;

    public CPUPlayer(Pion cpu) {
        this.cpu = cpu;
        lastMoves = new ArrayDeque<>(2);
    }

    public int getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public Move getNextMoveAB(Board board) {
        ArrayList<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int rootNodesCounter = 0;
        startTime = System.currentTimeMillis();

        board.setInitialNumOfReds(board.getNumberOfPionsRouge());
        board.setInitialNumOfBlacks(board.getNumberOfPionsNoir());
        board.setInitialNumOfCapturersAroundKing(board.getNumberOfCapturersAroundKing());
        for (Move currentMove : board.getPossibleMoves(this.cpu)) {
            numExploredNodes = 0;
            numExploredNodes++;
            rootNodesCounter++;

            Board copy = new Board(board);
            copy.setPionOnBoard(currentMove);

            int maxDepth = determineDynamicDepth();

            int score = minimaxAB(copy, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
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

            // Check if time limit exceeded
            if (System.currentTimeMillis() - startTime >= TIME_LIMIT) {
                System.out.println("\nTime limit exceeded!");
                break;
            }
        }
        System.out.println("-> Moves traversed: " + rootNodesCounter + " / " + board.getPossibleMoves(this.cpu).size() + " in " + (System.currentTimeMillis() - startTime) + "ms");

        Move bestMove = getFastestMove(bestMoves);
        if (bestMoves.size() > 1 && !lastMoves.isEmpty() && lastMoves.peek().indexToString().equals(bestMove.indexToString())) {
            bestMoves.remove(bestMove);
            bestMove = getFastestMove(bestMoves);
        }
        if (bestMoves.size() == 1 && !lastMoves.isEmpty() && lastMoves.peek().indexToString().equals(bestMove.indexToString())) {
            bestMove = board.getPossibleMoves(bestMove.old_position.x, bestMove.old_position.y).get(0);
        }
        if (lastMoves.size() == 2) {
            lastMoves.poll();
        }
        lastMoves.add(bestMove);

        return bestMove;
    }

    private int determineDynamicDepth() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int maxDepth = MIN_DEPTH;

        if (elapsedTime < 0.9 * TIME_LIMIT) {
            maxDepth = 3;
        }

        return maxDepth;
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

    public static Move getFastestMove(ArrayList<Move> moves) {
        Optional<Move> theMove = moves.stream().min(Comparator.comparing(Move::getNumOfNodes));
        return theMove.get();
    }
}
