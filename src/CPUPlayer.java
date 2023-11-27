import java.util.*;

class CPUPlayer {
    private static final int MAX_DEPTH = 2;
    private static final int TIME_LIMIT = 5000;
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
        int nombrePionRouge = board.getNumberOfPionsRouge();
        int nombrePionNoir = board.getNumberOfPionsNoir();
        ArrayList<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        startTime = System.currentTimeMillis();
        for (Move currentMove : board.getPossibleMoves(this.cpu)) {
            numExploredNodes = 0;
            numExploredNodes++;

            Board copy = new Board(board);
            copy.setPionOnBoard(currentMove);
            int score = minimaxAB(copy, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false, nombrePionRouge, nombrePionNoir);
            currentMove.setNumOfNodes(numExploredNodes);

            System.out.println(currentMove.indexToString() + " | Score: " + score + " | nodes: " + numExploredNodes);
            if (score == 100 && numExploredNodes == 1) {
                bestMoves.clear();
                bestMoves.add(currentMove);
                break;
            }

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
        Move bestMove = getFastestMove(bestMoves);
        if (!lastMoves.isEmpty() && lastMoves.peek().indexToString().equals(bestMove.indexToString())) {
            bestMoves.remove(bestMove);
            bestMove = getFastestMove(bestMoves);
        }
        if (lastMoves.size() == 2) {
            lastMoves.poll();
        }
        lastMoves.add(bestMove);

        return bestMove;
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

    public static Move getFastestMove(ArrayList<Move> moves) {
        Optional<Move> theMove = moves.stream().min(Comparator.comparing(Move::getNumOfNodes));
        return theMove.get();
    }
}
