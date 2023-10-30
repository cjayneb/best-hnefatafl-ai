import java.io.*;
import java.net.*;

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

                // Début de la partie en joueur blanc
                if(cmd == '1'){
                    startGame(input, board);

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    String move;
                    move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if (cmd == '2') {
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
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
                    System.out.println("Entrez votre coup : ");

                    String move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();

                }

                // La partie est terminée
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);

                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    String move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
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
}
