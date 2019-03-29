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
	
	public static void debug(String str) {
//		System.out.println(str);
	}
	
	public static void debug2(String str) {
		System.out.println(str);
	}
	
	public static void doDFS() {
		ArrayList<String> results = new ArrayList<>();
		ArrayList<String> results2 = new ArrayList<>();
		int size = startState.statesOfLinks.size();
		for (int i = 0; i < size; i++) {
			results2 = doDFSnextStep(startState.statesOfLinks.get(i));
			for (int j = 0; j < results2.size(); j++) {
				results.add(startState.links.get(i) + results2.get(j));
			}
		}
		for (int i = 0; i < results.size(); i++) {
			debug2(results.get(i));
		}
	}
	
	public static ArrayList<String> doDFSnextStep(State state) {
		ArrayList<String> results = new ArrayList<>();
		ArrayList<String> results2 = new ArrayList<>();
		ArrayList<String> results3 = new ArrayList<>();
		int size = state.statesOfLinks.size();
		if (size == 0) {
			results2.add("");
		} else {
			for (int i = 0; i < size; i++) {
				results3 = doDFSnextStep(state.statesOfLinks.get(i));
				for (int j = 0; j < results3.size(); j++) {
					results.add(results3.get(j));
				}
				for (int j = 0; j < results.size(); j++) {
					results2.add(state.links.get(i) + results.get(j));
				}
			}
		}
		
		return results2;
	}
	
	public static String nextStep(State nextState, int wordIndex, int wordLength) {
		debug("entering nextStep");
		String commonPrefixSegment = "";
		int size = nextState.links.size();
		boolean foundNewState = false;
		char nextLetter = '\u0000';
		for (int i = 0; i < size; i++) {
			nextLetter = word.charAt(wordIndex);
			if (nextState.links.get(i).equals(nextLetter)) {
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
		debug("exiting nextStep");
		return commonPrefixSegment;
	}
	
	public static String findCommonPrefix() {
		debug("entering findCommonPrefix");
		String commonPrefix = "";
		
		int wordLength = word.length();
		if (wordLength == 0) {
			System.out.println("error - empty work.. issue");
		}
		
		int size = startState.links.size();
//		System.out.println("SIZE: " + size);
		boolean foundNewState = false;
		for (int i = 0; i < size; i++) {
			char firstLetter = word.charAt(0);
//			System.out.println("first letter: " + firstLetter);
			if (startState.links.get(i).equals(firstLetter)) {
//				System.out.println("ever in here");
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
		debug("exiting findCommonPrefix");
		return commonPrefix;
	}
	
	/**
	 * Will return an integer bigger than 1 if s1 is "bigger" than s2
	 */
	public static int compareStrings(String s1, String s2) {
		debug("entering compareStrings");
	    int comparison = 0;
	    int c1, c2;
	    for(int i = 0; i < s1.length() && i < s2.length(); i++) {
	        c1 = (int) s1.toLowerCase().charAt(i);   // See note 1
	        c2 = (int) s2.toLowerCase().charAt(i);   // See note 1
	        comparison = c1 - c2;   // See note 2

	        if(comparison != 0) {    // See note 3
	        	debug("exiting compareStrings");
	            return comparison;
	        }
	    }
	    if(s1.length() > s2.length()) {   // See note 4
	    	debug("exiting compareStrings");
	        return 1;
	    } else if (s1.length() < s2.length()) {
	    	debug("exiting compareStrings");
	        return -1;
	    } else {
	    	debug("exiting compareStrings");
	        return 0;
	    }
	}
	
	public static State replaceOrRegister(State state) {
		debug("entering replaceOrRegister");
		State childState = state.lastLinkAdded;
		if (childState.hasChildren()) {
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
		debug("exiting replaceOrRegister");
		return mergeState;
	}
	
	public static String addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
		debug("entering addSuffixNextStep");
		State newState;
		String rhs;
		if (index == (suffixlength-1)) {
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
		debug("exiting addSuffixNextStep");
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
		debug("entering addSuffixNextStep");
		int suffixLength = currentSuffix.length();
		if (suffixLength != 0) {
			State newState;
			if (suffixLength == 1) {
				newState = new State(newStateCount++, true);
				states.add(newState);
				State lastStateState = states.get(lastState);
//				System.out.println("adding linkPPP: " + currentSuffix.charAt(0));
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
		debug("exiting addSuffixNextStep");
	}
	
	public static void main(String[] args) throws IOException {
		String commonPrefix = null;
		String currentSuffix = null;
		
		//Create the start state
		startState = new State(0, false);
		states.add(startState);
		
		//set up reader for input of asciibetically sorted dictionary in the format one word per line
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		word = br.readLine();
		while (word != null) {
			
			
			commonPrefix = findCommonPrefix();
			currentSuffix = word.substring(commonPrefix.length(), word.length());
			
			System.out.println(" word: " + word);
			System.out.println(" common Prefix: " + commonPrefix);
			System.out.println(" currentSuffix: " + currentSuffix);
			System.out.println(" last state: " + lastState);
			System.out.println("-------------");
			
			if (states.get(lastState).hasChildren()) {
				replaceOrRegister(states.get(lastState));
			}
			addSuffix(currentSuffix);
			
			
			
			word = br.readLine();
		}
		replaceOrRegister(startState);
		
		doDFS();
	}
	
}
