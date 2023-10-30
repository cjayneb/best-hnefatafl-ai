import java.util.ArrayList;

class CPUPlayer {
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

        for (Move currentMove : board.getPossibleMoves()) {
            numExploredNodes++;
            board.setPionOnBoard(currentMove, this.cpu);

            int score = minimaxAB(board,Integer.MIN_VALUE,Integer.MAX_VALUE, false);
            board.setPionOnBoard(currentMove, Pion.VIDE);
            currentMove.setNumOfNodes(numExploredNodes);

//            System.out.print("row: " + currentMove.getRow() + ", Col: " + currentMove.getCol());
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

    public int minimaxAB(Board board, int alpha, int beta, boolean maximizingPlayer) {
        numExploredNodes++;

        int boardScore = board.evaluate(this.cpu);
        if (boardScore != 0 || board.isFull()) {
            return board.evaluate(this.cpu);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move currentMove : board.getPossibleMoves()) {
                board.setPionOnBoard(currentMove, this.cpu);
                int eval = minimaxAB(board, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                board.setPionOnBoard(currentMove, Pion.VIDE);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;
            for (Move currentMove : board.getPossibleMoves()) {
                board.setPionOnBoard(currentMove, Pion.getOppositePion(this.cpu));
                int eval = minimaxAB(board, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                board.setPionOnBoard(currentMove, Pion.VIDE);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

}
