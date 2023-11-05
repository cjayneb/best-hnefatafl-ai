import java.util.HashMap;
import java.util.Map;
import java.awt.Point;

public class Move {
    Map<Character, Integer> letterToY = new HashMap<>();
    public Point old_position;
    public Point new_position;
    public String s;
    private int numOfNodes;

    public Move(String s) {
        InitHash();
        old_position = new Point();
        new_position = new Point();
        this.s = s;
        stringToIndex();
    }

    public Move(int x_old, int y_old, int x_new, int y_new) {
        InitHash();
        this.old_position = new Point(x_old, y_old);
        this.new_position = new Point(x_new, y_new);
        indexToString();
    }
    public void InitHash() {
        letterToY.put('A', 0);
        letterToY.put('B', 1);
        letterToY.put('C', 2);
        letterToY.put('D', 3);
        letterToY.put('E', 4);
        letterToY.put('F', 5);
        letterToY.put('G', 6);
        letterToY.put('H', 7);
        letterToY.put('I', 8);
        letterToY.put('J', 9);
        letterToY.put('K', 10);
        letterToY.put('L', 11);
        letterToY.put('M', 12);
    }

    public Move stringToIndex() {
        String [] sTab = s.split("-");
        this.old_position.x = 13 - Integer.parseInt(sTab[0].trim().substring(1)); //Soustraction car le board est inversé
        this.old_position.y = letterToY.get(sTab[0].trim().charAt(0));
        this.new_position.x = 13 - Integer.parseInt(sTab[1].trim().substring(1));
        this.new_position.y = letterToY.get(sTab[1].trim().charAt(0));
        return this;
    }

    public Move indexToString() {
        String old_position = "";
        String new_position = "";
        old_position += (char) (this.old_position.y + 'A');
        old_position += (13 - this.old_position.x);
        new_position += (char) (this.new_position.y + 'A');
        new_position += (13 - this.new_position.x);
        this.s = old_position + "-" + new_position;
        return this;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }

    public int getNumOfNodes() {
        return this.numOfNodes;
    }
}
