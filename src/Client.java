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


    public static void main(String[] args) {
        board = new Board();
      
        try {
            MyClient = new Socket("localhost", 8888);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());

            while (true) {
                char cmd = (char)input.read();
                System.out.println(cmd);

                // Début de la partie en joueur Rouge
                if (cmd == '1') {
                    cpuPlayer = new CPUPlayer(Pion.ROUGE);
                    startGame();
                    System.out.println("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
                    respond();
                }
              
                // Début de la partie en joueur Noir
                if (cmd == '2') {
                    cpuPlayer = new CPUPlayer(Pion.NOIR);
                    startGame();
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
                }

                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if (cmd == '3') {
                    String opponentMove = getLastServerMessage(16);
                    System.out.println("Dernier coup :" + opponentMove);

                    board.setPionOnBoard(new Move(opponentMove));
                    board.show();

                    System.out.println("Entrez votre coup : ");
                    respond();
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    respond();
                }

                // La partie est terminée
                if (cmd == '5') {
                    String opponentMove = getLastServerMessage(16);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + opponentMove);

                    board.setPionOnBoard(new Move(opponentMove));
                    board.show();
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void startGame() throws IOException {
        String serverInput = getLastServerMessage(1024);
        String[] boardValues = serverInput.split(" ");
        board.setBoard(boardValues);
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


//    private static void readAndUpdateMove() throws IOException {
//       // Map<String, List<int[]>> hashMove = getHashAfficherMovesPossibles(board.getBoard());
//        Map<String, List<int[]>> hashMove = getHashAfficherMovesPossibles(new int[][]{});
//        Random random = new Random();
//        List<String> keysAsArray = new ArrayList<>(hashMove.keySet());
//
//        int[] randomMove = null;
//        String randomKey = null;
//        List<int[]> movesList = null;
//
//        if (!keysAsArray.isEmpty()) {
//            while (movesList == null || movesList.isEmpty()) {
//                // Pick a random key from the keysAsArray.
//                randomKey = keysAsArray.get(random.nextInt(keysAsArray.size()));
//                movesList = hashMove.get(randomKey);
//            }
//            randomMove = movesList.get(random.nextInt(movesList.size()));
//        }
//
//        System.out.println("Random Key: " + randomKey);
//        System.out.println("Random Value: " + Arrays.toString(randomMove));
//
//        String [] old_coords = randomKey.split(" ");
//        old_coords[0] = old_coords[0].substring(1);
//        old_coords[1] = old_coords[1].substring(1);
//
//        // Structure si on veut convertir les coordonnées en String comme demandé par le serveur
//        String move = new Move(Integer.parseInt(old_coords[0]), Integer.parseInt(old_coords[1]), randomMove[0], randomMove[1]).s;
//
//        // Normalement retourne une liste de coup (Move) qu'il faudra trier pour trouver le meilleur coup
//        // Devrait aussi accepter le board courant au lieu d'un board vide
//        // Je te laisse avoir du fun avec ca JC :D (! Je parle de la ligne ci-dessus BTW !)
////        String move = cpuPlayer.getNextMoveAB(new Board()).get(0).toString();
//    }

//    public static Map<String, List<int[]>> getHashAfficherMovesPossibles(int[][] board) {
//        Map<String, List<int[]>> allPossibleMoves = getAllPossibleMoves(board);
//
//        for (Map.Entry<String, List<int[]>> entry : allPossibleMoves.entrySet()) {
//            System.out.print(entry.getKey() + " : [");
//
//            for (int[] coordinates : entry.getValue()) {
//                System.out.print(Arrays.toString(coordinates) + ", ");
//            }
//            System.out.println("]");
//        }
//        return allPossibleMoves;
//    }
//
//    public static Map<String, List<int[]>> getAllPossibleMoves(int[][] board) {
//        Map<String, List<int[]>> allPossibleMoves = new HashMap<>();
//
//        for (int x = 0; x < board.length; x++) {
//            for (int y = 0; y < board[x].length; y++) {
//               if ((board[x][y] == 4 && cpuPlayer.getPion() == Pion.ROUGE) || ((board[x][y] == 5 || board[x][y] == 2) && cpuPlayer.getPion() != Pion.ROUGE)) {
//                    List<int[]> possibleMoves = getPossibleMoves(board, x, y);
//                    String position = "x" + x + " y" + y;
//                    allPossibleMoves.put(position, possibleMoves);
//               }
//            }
//        }
//
//        return allPossibleMoves;
//    }
//
//    public static List<int[]> getPossibleMoves(int[][] board, int x, int y) {
//        List<int[]> possibleMoves = new ArrayList<>();
//        boolean roi = board[x][y] == 5;
//
//        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
//
//        for (int[] direction : directions) {
//            int dx = direction[0];
//            int dy = direction[1];
//            int newX = x + dx;
//            int newY = y + dy;
//
//            // Parce que le roi peut aller dans les coins
//            if (roi) {
//                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 ) {
//                    possibleMoves.add(new int[]{newX, newY});
//                    newX += dx;
//                    newY += dy;
//                }
//            }
//            else {
//                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 && !estCoin(newX, newY)) {
//                    if(newX == 6 && newY == 6){
//                        newX += dx;
//                        newY += dy;
//                        continue;
//                    }
//                    possibleMoves.add(new int[]{newX, newY});
//                    newX += dx;
//                    newY += dy;
//                }
//            }
//        }
//
//        return possibleMoves;
//    }
//
//    public static boolean estCoin(int x, int y) {
//        return (x == 0 && (y == 0 || y == 12)) || (x == 12 && (y == 0 || y == 12));
//    }

}
