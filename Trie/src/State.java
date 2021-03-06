import java.util.ArrayList;
import java.util.HashMap;

public class State {

	private int nameNumber;
	private boolean acceptState;

	boolean visited = false;

	private HashMap<Integer, EdgeInfo> outgoingEdges = new HashMap<>(); // map of node numbers to edges

	private State lastLinkAdded = null;
	private Character charOfLastLinkAdded = null;
	private ArrayList<String> rightHandStrings = new ArrayList<>();
	private int childrenCount = 0;
	
	public State(int NN, boolean accept) {
		nameNumber = NN;
		acceptState = accept;
	}
	
	public void removeLink(State s, Character ch) {
		childrenCount--;
		outgoingEdges.remove(s.nameNumber);
		lastLinkAdded = null;
		charOfLastLinkAdded = null;
	}
	
	public void addLink(State s, Character ch) {
		childrenCount++;
		if (outgoingEdges.get(s.nameNumber) != null) {
			EdgeInfo updateEdge = outgoingEdges.get(s.nameNumber);
			updateEdge.addEdge(ch);
		} else {
			EdgeInfo newEdgeInfo = new EdgeInfo(ch, s);
			outgoingEdges.put(s.nameNumber, newEdgeInfo);
		}
		lastLinkAdded = s;
		charOfLastLinkAdded = ch;
	}

	public int getNameNumber() {
		return nameNumber;
	}

	public boolean isAcceptState() {
		return acceptState;
	}

	public State getLastLinkAdded() {
		return lastLinkAdded;
	}

	public Character getCharOfLastLinkAdded() {
		return charOfLastLinkAdded;
	}

	public HashMap<Integer, EdgeInfo> getEdges() {
		return outgoingEdges;
	}

	public boolean hasChildren() {
		if (childrenCount > 0) {
			return true;
		} 
		return false;
	}
}
