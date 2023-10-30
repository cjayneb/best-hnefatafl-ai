import java.util.HashMap;
import java.util.Map;
import java.awt.Point;

public class Move {

    Map<Character, Integer> letterToY = new HashMap<>();
    public Point old_position;
    public Point new_position;
    public String [] s;


    public Move(String [] s) {
        InitHash();
        old_position = new Point();
        new_position = new Point();
        this.s = s;
        stringToIndex();
    }

    public Move(int [] m){
        InitHash();
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

    public Move stringToIndex(){

        old_position.x = 13 - Integer.parseInt(this.s[0].trim().substring(1)); //Soustraction car le board est inversé
        old_position.y = letterToY.get(this.s[0].trim().charAt(0));
        new_position.x = 13 - Integer.parseInt(this.s[1].trim().substring(1));
        new_position.y = letterToY.get(this.s[1].trim().charAt(0));
        return this;
    }

}
