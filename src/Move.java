import java.util.HashMap;
import java.util.Map;
import java.awt.Point;

public class Move {
    public static Map<Character, Integer> letterToY = new HashMap<Character, Integer>() {{
        put('A', 0);
        put('B', 1);
        put('C', 2);
        put('D', 3);
        put('E', 4);
        put('F', 5);
        put('G', 6);
        put('H', 7);
        put('I', 8);
        put('J', 9);
        put('K', 10);
        put('L', 11);
        put('M', 12);
    }};
    public Point old_position;
    public Point new_position;
    public String s;
    private int numOfNodes;

    public Move(String s) {
        old_position = new Point();
        new_position = new Point();
        this.s = s;
        stringToIndex();
    }

    public Move(int x_old, int y_old, int x_new, int y_new) {
        this.old_position = new Point(x_old, y_old);
        this.new_position = new Point(x_new, y_new);
        indexToString();
    }

    public Move stringToIndex() {
        String [] sTab = s.split("-");
        this.old_position.x = 13 - Integer.parseInt(sTab[0].trim().substring(1)); //Soustraction car le board est inversé
        this.old_position.y = letterToY.get(sTab[0].trim().charAt(0));
        this.new_position.x = 13 - Integer.parseInt(sTab[1].trim().substring(1));
        this.new_position.y = letterToY.get(sTab[1].trim().charAt(0));
        return this;
    }

    public String indexToString() {
        String old_position = "";
        String new_position = "";
        old_position += (char) (this.old_position.y + 'A');
        old_position += (13 - this.old_position.x);
        new_position += (char) (this.new_position.y + 'A');
        new_position += (13 - this.new_position.x);
        this.s = old_position + "-" + new_position;
        return this.s;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }

    public int getNumOfNodes() {
        return this.numOfNodes;
    }
}
