import java.io.*;
import java.net.*;
import java.util.*;

class Client {

    // Server stuff
    private static Socket MyClient;
    private static BufferedInputStream input;
    private static BufferedOutputStream output;

    // Game stuff
    private static CPUPlayer cpuPlayer;
    private static Board board;
    private static Move previousMove;
    public static long start;


    public static void main(String[] args) {
        board = new Board();
      
        try {
            MyClient = new Socket("localhost", 8888);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());

            while (true) {
                char cmd = (char)input.read();
                start = System.currentTimeMillis();
                System.out.println(cmd);

                // Début de la partie en joueur Rouge
                if (cmd == '1') {
                    cpuPlayer = new CPUPlayer(Pion.RED);
                    startGame();
                    System.out.println("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
                    respond();
                    logTime();
                }
              
                // Début de la partie en joueur Noir
                if (cmd == '2') {
                    cpuPlayer = new CPUPlayer(Pion.BLACK);
                    startGame();
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
                    logTime();
                }

                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if (cmd == '3') {
                    String opponentMoveStr = getLastServerMessage(16);
                    System.out.println("Dernier coup :" + opponentMoveStr);
                    Move opponentMove = new Move(opponentMoveStr);

                    board.setPionOnBoard(opponentMove);
                    board.show();

                    System.out.println("Entrez votre coup : ");
                    respond();
                    logTime();
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    respond();
                    logTime();
                }

                // La partie est terminée
                if (cmd == '5') {
                    String opponentMoveStr = getLastServerMessage(16);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + opponentMoveStr);
                    Move opponentMove = new Move(opponentMoveStr);

                    // So our board doesn't update when we receive the final move after winning
                    if (!(previousMove.old_position.equals(opponentMove.old_position)
                            && previousMove.new_position.equals(opponentMove.new_position))) {
                        board.setPionOnBoard(opponentMove);
                    }
                    board.show();
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void logTime() {
        long timeToRespond = System.currentTimeMillis() - start;
        if (timeToRespond > 5000) {
            System.out.println("Took too long to respond! Game lost :(");
        }
        System.out.println("Time to respond: " + timeToRespond + "ms");
    }

    private static void startGame() throws IOException {
        String serverInput = getLastServerMessage(1024);
        String[] boardValues = serverInput.split(" ");
        board.initializeBoard(boardValues);
        board.show();
    }

    private static String getLastServerMessage(int bufferSize) throws IOException {
        byte[] aBuffer = new byte[bufferSize];
        int size = input.available();
        input.read(aBuffer, 0, size);
        String s = new String(aBuffer).trim();
        return s;
    }

    private static void respond() throws IOException {
        Move nextMove = getFastestMove(cpuPlayer.getNextMoveAB(board));
        board.setPionOnBoard(nextMove);
        board.show();
        previousMove = nextMove;
        sendMoveToServer(nextMove.indexToString());
    }

    private static void sendMoveToServer(String move) throws IOException {
        System.out.println(move);
        output.write(move.getBytes(), 0, move.length());
        output.flush();
    }

    public static Move getFastestMove(ArrayList<Move> moves) {
        Optional<Move> theMove = moves.stream().min(Comparator.comparing(Move::getNumOfNodes));
        return theMove.get();
    }
}
