import java.util.ArrayList;

public class EdgeInfo {

//    private Character edgeChar;
    private ArrayList<Character> edgeChars = new ArrayList<>();
    private State edgeToState;

    public EdgeInfo(Character ch, State s) {
//        edgeChar = ch;
        edgeChars.add(ch);
        edgeToState = s;
    }

    public ArrayList<Character> getEdgeChars() {
        return edgeChars;
    }

    public State getedgeToState() {
        return edgeToState;
    }

    public void addEdge(Character ch) {
        edgeChars.add(ch);
    }

}
