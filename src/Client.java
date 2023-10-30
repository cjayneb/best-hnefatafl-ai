import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Client {
    public static void main(String[] args) {
        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[13][13];
      
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
                    startGame(input, board);
                    AfficherBoard(board);
                    System.out.println("Nouvelle partie! Vous jouer rouge, entrez votre premier coup : ");
                    
                    // Voir tous les coups possibles de E1
                    List<int[]> possibleMovesE1 = getPossibleMoves(board, 12, 2);
                    System.out.println("Possible moves for roi: ");
                    for (int[] move : possibleMovesE1) {
                        System.out.println(move[0] + " " + move[1]);
                    }

                    readMove(output, console, board);
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

                    readMove(output, console, board);
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    readMove(output, console, board);
                }

                // La partie est terminée
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                  
                    readMove(output, console, board);
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

    private static void readMove(BufferedOutputStream output, BufferedReader console, int[][] board) throws IOException {
        String move = console.readLine();
        updateBoard(move, board);
        output.write(move.getBytes(), 0, move.length());
        output.flush();
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
            
            //ATTENTION IL RESTE ENCORE A GERER LA CASE DU MILIEU
            if (roi) { //Parce que le roi peut aller dans les coins 
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 ) {
                    possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            } 
            else {
                while (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length && board[newX][newY] == 0 && !estCoin(newX, newY)) {
                    possibleMoves.add(new int[]{newX, newY});
                    newX += dx;
                    newY += dy;
                }
            }
        }
        
        return possibleMoves;
    }

    public static void updateBoard(String dernierMove, int[][] board) {
        Move dernierMoveObj = new Move(dernierMove.split("-"));
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
