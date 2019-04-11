import java.util.ArrayList;
import java.util.HashMap;

public class State {
	
//	private int creationOrderNumber;
	private int nameNumber;
	private boolean acceptState;

	boolean visited = false;
	
//	public ArrayList<Character> links = new ArrayList<>();
//	public ArrayList<State> statesOfLinks = new ArrayList<>();
	private HashMap<Integer, EdgeInfo> outgoingEdges = new HashMap<>(); // map of node numbers to edges

	private State lastLinkAdded = null;
	private Character charOfLastLinkAdded = null;
	private ArrayList<String> rightHandStrings = new ArrayList<>();
	private int childrenCount = 0;
	
	public State(int NN, boolean accept) {
//		creationOrderNumber = CON;
		nameNumber = NN;
		acceptState = accept;
	}
	
	public void removeLink(State s, Character ch) {
		System.out.println("removing link");
		childrenCount--;
//		links.remove(ch);
//		statesOfLinks.remove(s);
		outgoingEdges.remove(s.nameNumber);
		lastLinkAdded = null;
		charOfLastLinkAdded = null;
		if (outgoingEdges.size() == 0) {
			System.out.println("no outgoing edges to remove");
		}
		for (EdgeInfo edgeInfo : outgoingEdges.values()) {
			int numEdges = edgeInfo.getEdgeChars().size();
			for (int i = 0; i < numEdges; i++) {
				System.out.println("Link from state: " + nameNumber + " : " + edgeInfo.getedgeToState().nameNumber
						+ "on edge: " + edgeInfo.getEdgeChars().get(i));
			}
		}
//		for (int i = 0; i < outgoingEdges.size(); i++) {
//			System.out.println("Link from state: " + this.nameNumber + " : " + outgoingEdges.get(i).getedgeToState().nameNumber
//			+ "on edge: " + outgoingEdges.get(i).getEdgeChar());
//		}
	}
	
	public void addLink(State s, Character ch) {
		System.out.println("**************Adding link from: " +  nameNumber + " to " + s.nameNumber + " with edge " + ch);
		childrenCount++;
//		links.add(ch);
//		statesOfLinks.add(s);
//		EdgeInfo newEdgeInfo = new EdgeInfo(ch, s);
		System.out.println("outgoing edds size: " + outgoingEdges.size());
		if (outgoingEdges.get(s.nameNumber) != null) {
			EdgeInfo updateEdge = outgoingEdges.get(s.nameNumber);
			updateEdge.addEdge(ch);
//			outgoingEdges.put(s.nameNumber, newEdgeInfo);
		} else {
			EdgeInfo newEdgeInfo = new EdgeInfo(ch, s);
			outgoingEdges.put(s.nameNumber, newEdgeInfo);
		}



		lastLinkAdded = s;
		charOfLastLinkAdded = ch;
		for (EdgeInfo edgeInfo : outgoingEdges.values()) {
			int numEdges = edgeInfo.getEdgeChars().size();
			for (int i = 0; i < numEdges; i++) {
				System.out.println("After adding link from state: " + nameNumber + " : " + edgeInfo.getedgeToState().nameNumber
						+ "on edge: " + edgeInfo.getEdgeChars().get(i));
			}


		}

	}

//	public void addRHS(String rhs) {
////		hasChildren = true;
//		rightHandStrings.add(rhs);
//	}

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

//	public static void removeRHS(String rhs) {
//		// look through this again.. not even sure if I'll ever need this
////		hasChildren = false;
//		int index = -1;
//		for (int i = 0; i < rightHandStrings.size(); i++) {
//			if (rightHandStrings.get(i).equals(rhs)) {
//				index = i;
//				break;
//			}
//		}
//		if (index == -1) {
//			System.out.println("error - trying to remove a RHS value that does not exist from a state");
//		} else {
//			rightHandStrings.remove(index);
//		}
//
//	}

//	public static boolean containsRHS(String rhs) {
//		if (rightHandStrings.contains(rhs)) {
//			return true;
//		}
//		return false;
//	}

	public boolean hasChildren() {
		if (childrenCount > 0) {
			return true;
		} 
		return false;
	}
	
}
