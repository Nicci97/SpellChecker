import java.util.ArrayList;

public class State {
	
//	private int creationOrderNumber;
	public int nameNumber;
	public boolean acceptState;
	
	public ArrayList<Character> links = new ArrayList<>();
	public ArrayList<State> statesOfLinks = new ArrayList<>();
	
	boolean hasChildren = false;
	State lastLinkAdded = null;
	Character charOfLastLinkAdded = null;
	ArrayList<String> rightHandStrings = new ArrayList<>();
	
	public State(int NN, boolean accept) {
//		creationOrderNumber = CON;
		nameNumber = NN;
		acceptState = accept;
	}
	
	public void removeLink(State s, Character ch) {
		links.remove(ch);
		statesOfLinks.remove(s);
		lastLinkAdded = null;
		charOfLastLinkAdded = null;
	}
	
	public void addLink(State s, Character ch) {
		links.add(ch);
		statesOfLinks.add(s);
		lastLinkAdded = s;
		charOfLastLinkAdded = ch;
	}
	
	public void addRHS(String rhs) {
		hasChildren = true;
		rightHandStrings.add(rhs);
	}
	
	public void removeRHS(String rhs) {
		// look through this again.. not even sure if I'll ever need this
		hasChildren = false;
		int index = -1;
		for (int i = 0; i < rightHandStrings.size(); i++) {
			if (rightHandStrings.get(i).equals(rhs)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			System.out.println("error - trying to remove a RHS value that does not exist from a state");
		} else {
			rightHandStrings.remove(index);
		}
		
	}
	
	public boolean containsRHS(String rhs) {
		if (rightHandStrings.contains(rhs)) {
			return true;
		}
		return false;
	}
	
	public boolean hasChildren() {
		return hasChildren;
	}
	
}
