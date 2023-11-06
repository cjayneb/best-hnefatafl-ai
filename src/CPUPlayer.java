import java.util.ArrayList;

class CPUPlayer {
    private static final int MAX_DEPTH = 2;
    private int numExploredNodes;
    private Pion cpu;

    public CPUPlayer(Pion cpu) {
        this.cpu = cpu;
    }

    public Pion getPion() {
        return cpu;
    }

    public void setPion(Pion pion) {
        this.cpu = pion;
    }

    public int  getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public ArrayList<Move> getNextMoveAB(Board board) {
            ArrayList<Move> bestMoves = new ArrayList<>();
            int bestScore = Integer.MIN_VALUE;
            numExploredNodes = 0;

        for (Move currentMove : board.getPossibleMoves(this.cpu)) {
            numExploredNodes++;
            ArrayList<Move> pionsToRevive = new ArrayList<>();
            board.setPionOnBoard(currentMove);
            int score = minimaxAB(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.revertMove(currentMove, new ArrayList<>());
            currentMove.setNumOfNodes(numExploredNodes);

            System.out.print("| Score: " + score + " | nodes: " + numExploredNodes);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(currentMove);
            }
            else if (score == bestScore) {
                bestMoves.add(currentMove);
            }
            System.out.println();
        }
        return bestMoves;
    }

    public int minimaxAB(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        numExploredNodes++;

        if (depth == 0 || board.gameIsDone()) {
            return board.evaluate(this.cpu);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move currentMove : board.getPossibleMoves(this.cpu)) {
                ArrayList<Move> pionsToRevive = new ArrayList<>();
                board.setPionOnBoard(currentMove, pionsToRevive);
                int eval = minimaxAB(board, depth - 1, alpha, beta, false);
                board.revertMove(currentMove, pionsToRevive);

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
                ArrayList<Move> pionsToRevive = new ArrayList<>();
                board.setPionOnBoard(currentMove, pionsToRevive);
                int eval = minimaxAB(board, depth - 1, alpha, beta, true);
                board.revertMove(currentMove, pionsToRevive);

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
