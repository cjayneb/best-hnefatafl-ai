import java.io.*;
import java.net.*;
import java.util.*;


class Client {
    public static void main(String[] args) {
        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[13][13];
        boolean joueurRouge = false;
      
        try {
            MyClient = new Socket("localhost", 8888);

            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                char cmd = (char)input.read();
                System.out.println(cmd);

                // Début de la partie en joueur rouge
                if (cmd == '1') {
                    joueurRouge = true;
                    startGame(input, board);
                    AfficherBoard(board);
                    System.out.println("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");


                    readMove(output, console, board, joueurRouge);
                }
              
                // Debut de la partie en joueur Noir
                if (cmd == '2') {
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
                    startGame(input, board);
                }

                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);

                    System.out.println("Dernier coup :" + s);
                    updateBoard(s, board);
                    AfficherBoard(board);
                    System.out.println("Entrez votre coup : ");
                    readMove(output, console, board, joueurRouge);
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    readMove(output, console, board, joueurRouge);
                }

                // La partie est terminée
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                  
                    readMove(output, console, board, joueurRouge);
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void startGame(BufferedInputStream input, int[][] board) throws IOException {
        byte[] aBuffer = new byte[1024];
        int size = input.available();
        //System.out.println("size " + size);

        input.read(aBuffer,0,size);
        String s = new String(aBuffer).trim();
        System.out.println(s);

        String[] boardValues;
        boardValues = s.split(" ");

        int x = 0,y = 0;
        for (String boardValue : boardValues) {
            board[x][y] = Integer.parseInt(boardValue);
            x++;
            if (x == 13) {
                x = 0;
                y++;
            }
        } 
    }

    private static void readMove(BufferedOutputStream output, BufferedReader console, int[][] board, boolean joueurRouge) throws IOException {
        String move = console.readLine();

        Map<String, List<int[]>> hashMove = getHashafficherMovesPossibles(board, joueurRouge);
        /*Random random = new Random();
        List<String> keysAsArray = new ArrayList<>(hashMove.keySet());

        int[] randomMove = null;
        String randomKey = null;
        List<int[]> movesList = null;

        if (!keysAsArray.isEmpty()) {
            while (movesList == null || movesList.isEmpty()) {
                // Pick a random key from the keysAsArray.
                randomKey = keysAsArray.get(random.nextInt(keysAsArray.size()));
                movesList = hashMove.get(randomKey);
            }
            randomMove = movesList.get(random.nextInt(movesList.size()));
        }

        System.out.println("Random Key: " + randomKey);
        System.out.println("Random Value: " + Arrays.toString(randomMove));

        String [] old_coords = randomKey.split(" ");
        old_coords[0] = old_coords[0].substring(1);
        old_coords[1] = old_coords[1].substring(1);

        String move = new Move(Integer.parseInt(old_coords[0]), Integer.parseInt(old_coords[1]), randomMove[0], randomMove[1]).s; //Structure si on veut convertir coordonnées en String demandé par serveur
        */
        updateBoard(move, board);
        output.write(move.getBytes(), 0, move.length());
        output.flush();
    }

    public static Map<String, List<int[]>> getHashafficherMovesPossibles(int[][] board, boolean joueurRouge) {
        Map<String, List<int[]>> allPossibleMoves = getAllPossibleMoves(board, joueurRouge);
        for (Map.Entry<String, List<int[]>> entry : allPossibleMoves.entrySet()) {
            System.out.print(entry.getKey() + " : [");
            for (int[] coordinates : entry.getValue()) {
                System.out.print(Arrays.toString(coordinates) + ", ");
            }
            System.out.println("]");
        }
        return allPossibleMoves;
    }
    public static Map<String, List<int[]>> getAllPossibleMoves(int[][] board, boolean joueurRouge) {
        Map<String, List<int[]>> allPossibleMoves = new HashMap<>();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] == 4 && joueurRouge) {
                    List<int[]> possibleMoves = getPossibleMoves(board, x, y);

                    String position = "x" + x + " y" + y;

                    allPossibleMoves.put(position, possibleMoves);
                }else if((board[x][y] == 5 || board[x][y] == 2) && !joueurRouge){
                    List<int[]> possibleMoves = getPossibleMoves(board, x, y);

                    String position = "x" + x + " y" + y;

                    allPossibleMoves.put(position, possibleMoves);
                }
            }
        }

        return allPossibleMoves;
    }

    public static List<int[]> getPossibleMoves(int[][] board, int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        boolean roi = false;

        if(board[x][y] == 5){
            roi = true;
        }
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; 
        
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int newX = x + dx;
            int newY = y + dy;
            
            if (roi) { //Parce que le roi peut aller dans les coins
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 ) {
                    possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            } 
            else {
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 && !estCoin(newX, newY)) {
                    if(newX == 6 && newY == 6){
                        newX += dx;
                        newY += dy;
                        continue;
                    }
                    possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            }
        }
        
        return possibleMoves;
    }

    public static void updateBoard(String dernierMove, int[][] board) {
        Move dernierMoveObj = new Move(dernierMove);
        int joueur = board[dernierMoveObj.old_position.x][dernierMoveObj.old_position.y];
        board[dernierMoveObj.old_position.x][dernierMoveObj.old_position.y] = 0;
        board[dernierMoveObj.new_position.x][dernierMoveObj.new_position.y] = joueur;
    }

    public static void AfficherBoard(int[][] board) {
        System.out.println("BOARD : ");
        //print the board 
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) { 
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public static boolean estCoin(int x, int y) {
        return (x == 0 && (y == 0 || y == 12)) || (x == 12 && (y == 0 || y == 12));
    }

}
