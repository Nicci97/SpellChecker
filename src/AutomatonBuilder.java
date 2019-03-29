/*
 * Authored by Nicole du Toit.
 * ASCII value comparison function found at https://stackoverflow.com/questions/26553889/comparing-2-strings-by-ascii-values-in-java
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AutomatonBuilder {
	
	private static ArrayList<State> states = new ArrayList<State>();
	private static HashMap<String, State> registry = new HashMap<>();
	private static String word = null;
	private static int lastState = 0;
	private static State startState = null;
	private static int newStateCount = 1;
	private static String suffexToAdd = "";
//	private static State updateState = null;
	
	public static String nextStep(State nextState, int wordIndex, int wordLength) {
		String commonPrefixSegment = "";
		int size = nextState.links.size();
		boolean foundNewState = false;
		char nextLetter = '\u0000';
		for (int i = 0; i < size; i++) {
			nextLetter = word.charAt(wordIndex);
			if (nextState.links.equals(nextLetter)) {
				if (wordIndex == wordLength) {
					foundNewState = true;
				} else {
					commonPrefixSegment = nextLetter + nextStep(nextState.statesOfLinks.get(i), wordIndex+1, wordLength);
					foundNewState = true;
				}
			}
		}
		if (!foundNewState) {
			lastState = nextState.nameNumber;
			if (wordIndex != wordLength) {
				suffexToAdd = word.substring(wordIndex+1, word.length());
			}
			nextState.addRHS(suffexToAdd);
		} else {
			size = nextState.rightHandStrings.size();
			boolean updated = false;
			for (int i = 0; i < size; i++) {
				ArrayList<String> rightHandStrings = nextState.rightHandStrings;
				String rhsString = rightHandStrings.get(i);
				if (rhsString.startsWith(nextLetter + "")) {
					rightHandStrings.add(rhsString + suffexToAdd);
				}
			}
		}
		
		return commonPrefixSegment;
	}
	
	public static String findCommonPrefix() {
		String commonPrefix = "";
		
		int wordLength = word.length();
		if (wordLength == 0) {
			System.out.println("error - empty work.. issue");
		}
		
		int size = startState.links.size();
		boolean foundNewState = false;
		for (int i = 0; i < size; i++) {
			char firstLetter = word.charAt(0);
			if (startState.links.equals(firstLetter)) {
				if (wordLength == 1) {
					foundNewState = true;
				} else {
					commonPrefix = firstLetter + nextStep(startState.statesOfLinks.get(i), 1, wordLength);
					foundNewState = true;
				}
			}
		}
		if (!foundNewState) {
			lastState = 0;
		}
		
		// update last state in here
		return commonPrefix;
	}
	
	/**
	 * Will return an integer bigger than 1 if s1 is "bigger" than s2
	 */
	public static int compareStrings(String s1, String s2) {
	    int comparison = 0;
	    int c1, c2;
	    for(int i = 0; i < s1.length() && i < s2.length(); i++) {
	        c1 = (int) s1.toLowerCase().charAt(i);   // See note 1
	        c2 = (int) s2.toLowerCase().charAt(i);   // See note 1
	        comparison = c1 - c2;   // See note 2

	        if(comparison != 0)     // See note 3
	            return comparison;
	    }
	    if(s1.length() > s2.length())    // See note 4
	        return 1;
	    else if (s1.length() < s2.length())
	        return -1;
	    else
	        return 0;
	}
	
	public static State replaceOrRegister(State state) {
		State childState = state.lastLinkAdded;
		if (childState.hasChildren) {
			State mergeState = replaceOrRegister(childState);
			if (mergeState != null) {
				Character charOfLastLinkAdded = state.charOfLastLinkAdded;
				states.remove(childState.nameNumber);
				state.removeLink(childState, state.charOfLastLinkAdded);
				state.addLink(mergeState, charOfLastLinkAdded);
				childState = null;
			}
		}
		
		State mergeState = null;
		
		if (registry.containsValue(childState)) {
			int size = childState.rightHandStrings.size();
			if (size == 0) {
				mergeState = registry.get("");
				// do merge
			} else if (size == 1) {
				mergeState = registry.get(childState.rightHandStrings.get(0));
			} else {
				String highest = "";
				for (int i = 1; i < size; i++) {
					String rhsOne = childState.rightHandStrings.get(i-1);
					String rhsTwo = childState.rightHandStrings.get(i);
					if (compareStrings(rhsOne, rhsTwo) > 1) {
						highest = rhsOne;
					} else if (compareStrings(rhsTwo, rhsOne) > 1) {
						highest = rhsTwo;
					} else {
						System.out.println("error - might have duplicate right hand sides stored");
					}
				}
				mergeState = registry.get(highest);
			}
		} else {
			int size = childState.rightHandStrings.size();
			if (size == 0) {
				registry.put("", childState);
			} else {
				for (int i = 0; i < size; i++) {
					registry.put(childState.rightHandStrings.get(i), childState);
				}
			}
		}
		return mergeState;
	}
	
	
	public static void replaceOrRegisterStartState(State startState) {
		
	}
	
	public static String addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
		State newState;
		String rhs;
		if (index == suffixlength) {
			newState = new State(newStateCount++, true);
			states.add(newState);
			s.addLink(newState, currentSuffix.charAt(index));
			return currentSuffix.substring(index, index);
		} else {
			newState = new State(newStateCount++, false);
			states.add(newState);
			s.addLink(newState, currentSuffix.charAt(index));
			rhs = addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
			s.rightHandStrings.add(rhs);
		}
		return currentSuffix.substring(index, index) + rhs;
//		String rhs;
//		if (index != suffixlength) {
//			rhs = addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
//			s.rightHandStrings.add(rhs);
//		} else {
//			return currentSuffix.substring(index, index);
//		}
//		return currentSuffix.substring(index, index) + rhs;
	}
	
	public static void addSuffix(String currentSuffix) {
		int suffixLength = currentSuffix.length();
		if (suffixLength != 0) {
			State newState;
			if (suffixLength == 1) {
				newState = new State(newStateCount++, true);
				states.add(newState);
				State lastStateState = states.get(lastState);
				lastStateState.addLink(newState, currentSuffix.charAt(0));
			} else {
				newState = new State(newStateCount++, false);
				states.add(newState);
				State lastStateState = states.get(lastState);
				lastStateState.addLink(newState, currentSuffix.charAt(0));
				String rhs = addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
				lastStateState.rightHandStrings.add(rhs);
			}	
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		String commonPrefix = null;
		String currentSuffix = null;
		
		//Create the start state
		startState = new State(0, false);
		
		//set up reader for input of asciibetically sorted dictionary in the format one word per line
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		word = br.readLine();
		while (word != null) {
			System.out.println(word);
			commonPrefix = findCommonPrefix();
			currentSuffix = word.substring(commonPrefix.length()+1, word.length());
			if (states.get(lastState).hasChildren) {
				replaceOrRegister(states.get(lastState));
			}
			addSuffix(currentSuffix);
			word = br.readLine();
		}
		replaceOrRegisterStartState(startState);
		
	}
	
	
	
}
