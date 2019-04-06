/*
 * Authored by Nicole du Toit.
 * ASCII value comparison function found at https://stackoverflow.com/questions/26553889/comparing-2-strings-by-ascii-values-in-java
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AutomatonBuilder {

    private static ArrayList<State> states = new ArrayList<State>();
    //	private static HashMap<String, State> registry = new HashMap<>();
//	private static HashMap<State, ArrayList<String>> registry = new HashMap<>();
//	private static HashMap<Integer, State> registry = new HashMap<>();
    private static ArrayList<State> registry2 = new ArrayList<>();
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
//		System.out.println("entering DFS staring at node: " + startState.nameNumber);
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();
        int size = startState.statesOfLinks.size();
        for (int i = 0; i < size; i++) {
//            System.out.println(startState.links.get(i));
            results2 = doDFSnextStep(startState.statesOfLinks.get(i));
            for (int j = 0; j < results2.size(); j++) {
                results.add(startState.links.get(i) + results2.get(j));
            }
        }
        for (int i = 0; i < results.size(); i++) {
            debug2(results.get(i));
        }
//		System.out.println("exiting DFS staring at node: " + startState.nameNumber);
    }

    public static ArrayList<String> doDFSnextStep(State state) {
//		System.out.println("entering DFS next step at node: " + state.nameNumber);
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();
        ArrayList<String> results3 = new ArrayList<>();
        int size = state.statesOfLinks.size();

//		if (size == 0) {
//			results.add("");
//		} else {
        for (int i = 0; i < size; i++) {
//            System.out.println(state.links.get(i));
            results3 = doDFSnextStep(state.statesOfLinks.get(i));
            for (int j = 0; j < results3.size(); j++) {
                results.add(state.links.get(i) + results3.get(j));
            }
//				for (int j = 0; j < results.size(); j++) {
//					results2.add(state.links.get(i) + results.get(j));
//				}
        }
//		}
        if (state.acceptState == true) {
//			System.out.println("Accept state: " + state.nameNumber);
            results.add("");
        }
//		System.out.println("exiting DFS next step at node: " + state.nameNumber);
        return results;
    }

    public static String nextStep(State nextState, int wordIndex, int wordLength) {
//		debug("entering nextStep");
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
//			nextState.addRHS(suffexToAdd);
//        } else {
//            size = nextState.rightHandStrings.size();
//            boolean updated = false;
//            for (int i = 0; i < size; i++) {
//                ArrayList<String> rightHandStrings = nextState.rightHandStrings;
//                String rhsString = rightHandStrings.get(i);
//                if (rhsString.startsWith(nextLetter + "")) {
//                    rightHandStrings.add(rhsString + suffexToAdd);
//                }
//            }
        }
//		debug("exiting nextStep");
        return commonPrefixSegment;
    }

    public static String findCommonPrefix() {
//		debug("entering findCommonPrefix");
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
//		debug("exiting findCommonPrefix");
        return commonPrefix;
    }

//	/**
//	 * Will return an integer bigger than 1 if s1 is "bigger" than s2
//	 */
//	public static int compareStrings(String s1, String s2) {
////		debug("entering compareStrings");
//	    int comparison = 0;
//	    int c1, c2;
//	    for(int i = 0; i < s1.length() && i < s2.length(); i++) {
//	        c1 = (int) s1.toLowerCase().charAt(i);   // See note 1
//	        c2 = (int) s2.toLowerCase().charAt(i);   // See note 1
//	        comparison = c1 - c2;   // See note 2
//
//	        if(comparison != 0) {    // See note 3
////	        	debug("exiting compareStrings");
//	            return comparison;
//	        }
//	    }
//	    if(s1.length() > s2.length()) {   // See note 4
////	    	debug("exiting compareStrings");
//	        return 1;
//	    } else if (s1.length() < s2.length()) {
////	    	debug("exiting compareStrings");
//	        return -1;
//	    } else {
////	    	debug("exiting compareStrings");
//	        return 0;
//	    }
//	}

    // This method compares two strings
    // lexicographically without using
    // library functions
    public static int stringCompare(String str1, String str2)
    {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        // Edge case for strings like
        // String 1="Geeks" and String 2="Geeksforgeeks"
        if (l1 != l2) {
            return l1 - l2;
        }

        // If none of the above conditions is true,
        // it implies both the strings are equal
        else {
            return 0;
        }
    }

    public static State replaceOrRegister(State state) {
//		debug("entering replaceOrRegister");
        State childState = state.lastLinkAdded;
//		System.out.println("at LEAST IN HERE");
        if (childState.hasChildren()) {
            State mergeState = replaceOrRegister(childState);
//			System.out.println("bru");
            if (mergeState != null) {
                State parentState = childState;
                State newChildSTate = childState.lastLinkAdded;
//				System.out.println("bruuuu");
//				System.out.println("removing node:::: " + newChildSTate.nameNumber + " and linking state: " + parentState.nameNumber + " "
//						+ "with state: " + mergeState.nameNumber + " with link " + parentState.charOfLastLinkAdded);
                Character charOfLastLinkAdded = parentState.charOfLastLinkAdded;
//				System.out.println("removing state " + newChildSTate.nameNumber);
//				int index = states.
//				states.remove(newChildSTate.nameNumber);//NICOLE
                states.set(newChildSTate.nameNumber, null);
                parentState.removeLink(newChildSTate, parentState.charOfLastLinkAdded);
                parentState.addLink(mergeState, charOfLastLinkAdded);
                newChildSTate = null;
            }
        }

        State mergeState = null;
////		boolean contains = true;
//        int foundStateNumber = -1;
////        int numberOfRHS = childState.rightHandStrings.size();
////		System.out.println("state: " + childState.nameNumber + " and number of rhs: " + numberOfRHS);
////		System.out.println("registry size: " + registry.size());
//        int reigstrySize = registry2.size();
//        State foundState = null;
//        ArrayList<Character> registryValues;
//        // for each node in the registry
//        boolean contains = false;
//        State stateFromRegistryToMerge = null;
//        for (int i = 0; i < reigstrySize; i++) {
//            registryValues = registry2.get(i).links;
//            int regValSize = registryValues.size();
//            if (regValSize == childState.links.size()) {
//                contains = true;
//                for (int j = 0; j < regValSize; j++) {
//                    if (!registryValues.contains(childState.links.get(j))) {
//                        contains = false;
//                    }
//                }
//                if (contains == true) {
//                    stateFromRegistryToMerge = registry2.get(i);
//                    break;
//                }
//            } else {
//                contains = false;
//            }
//        }


        String checkReg = "";
        String checkChild = "";
        int registrySize = registry2.size();
        for (int i = 0; i < registrySize; i++) {
            State registryState = registry2.get(i);
            ArrayList<Character> registryOutgoingEdges = registryState.links;
            ArrayList<Character> childOutgoingEdges = childState.links;
            // GET STRING FOR REGISTRY STATE
            if (registryState.acceptState == true) {
                checkReg = "1";
            } else {
                checkReg = "0";
            }
            int numberOfEdges = registryOutgoingEdges.size();
            for (int j = 0; j < numberOfEdges; j++) {
                checkReg = checkReg + registryOutgoingEdges.get(j) + registryState.statesOfLinks.get(j).nameNumber;
            }
            // GET STRING FOR CHILD STATE
            if (childState.acceptState == true) {
                checkChild = "1";
            } else {
                checkChild = "0";
            }
            numberOfEdges = childOutgoingEdges.size();
            for (int j = 0; j < numberOfEdges; j++) {
                checkChild = checkChild + childOutgoingEdges.get(j) + childState.statesOfLinks.get(j).nameNumber;
            }
            // COMPARE THE TWO STRINGS
//            System.out.println("strings: " + checkReg + " " + checkChild);
            if (checkReg.equals(checkChild)) {
                mergeState = registryState;
                break;
            }
        }


//        if (contains && (registry2.size() != 0)) {
//            if (mergeState == null) {
//                System.out.println("oh dear");
//            }
//            mergeState = stateFromRegistryToMerge;
//            int size = childState.links.size();
////			System.out.println("WENT INTO CONTAINS: " + size + " ........ " + childState.nameNumber);
//            if (size == 0) {
////				System.out.println("in here");
//                int regSize = registry2.size();
//                for (int k = 0; k < regSize; k++) {
//                    ArrayList<String> rightHandStrs = registry2.get(k).rightHandStrings;
//                    if (rightHandStrs.size() == 0) {
//                        mergeState = registry2.get(k);
//                    }
//                }
//                // do merge
//            } else if (size == 1) {
//                int regSize = registry2.size();
//                for (int k = 0; k < regSize; k++) {
//                    ArrayList<String> rightHandStrs = registry2.get(k).rightHandStrings;
//                    if (rightHandStrs.size() == 1) {
//                        if (rightHandStrs.contains(childState.rightHandStrings.get(0))) {
//                            mergeState = registry2.get(k);
//                        }
//                    }
//                }
////				mergeState = registry.get(childState.rightHandStrings.get(0));
//            } else {
//                String highest = "";
//                for (int i = 1; i < size; i++) {
//                    String rhsOne = childState.rightHandStrings.get(i-1);
//                    String rhsTwo = childState.rightHandStrings.get(i);
//                    int result = stringCompare(rhsOne, rhsTwo);
//                    if (result > 0) {
//                        highest = rhsOne;
//                    } else if (result < 0) {
//                        highest = rhsTwo;
//                    } else {
//                        System.out.println(result);
//                        System.out.println("rhsOne: " + rhsOne + " rhsTwo: " + rhsTwo);
//                        System.out.println("error - might have duplicate right hand sides stored");
//                    }
//                }
//
//                int regSize = registry2.size();
//                for (int k = 0; k < regSize; k++) {
//                    ArrayList<String> rightHandStrs = registry2.get(k).rightHandStrings;
//                    if (rightHandStrs.size() == size) {
//                        if (rightHandStrs.contains(highest)) {
//                            mergeState = registry2.get(k);
//                        }
//                    }
//                }
////				mergeState = registry.get(highest);
//            }
//        } else {
//			System.out.println("putting the following in the registry}}}: " + childState.nameNumber);
//            registry2.add(childState);
//			int size = childState.rightHandStrings.size();
//			if (size == 0) {
//				System.out.println("putting the following in the registry1: " + childState.nameNumber);
////				ArrayList<String> arr = new ArrayList<>();
////				arr.add(" ");
//				registry2.add(childState);
////				registry.put(childState.nameNumber, childState);
////				registry.put(childState, arr);
////				registry.put(" ", childState);
//			} else {
//				for (int i = 0; i < size; i++) {
//					System.out.println("putting the following in the registry2: " + childState.nameNumber + " : " + childState.rightHandStrings.get(i));
////					ArrayList<String> arr = new ArrayList<>();
////					arr.add(childState.rightHandStrings.get(i));
////					registry.put(childState, arr);
//					registry2.add(childState);
////					registry.put(childState.nameNumber, childState);
////					registry.put(childState.rightHandStrings.get(i), childState);
//				}
//			}
//        }
//		debug("exiting replaceOrRegister");
        if (mergeState == null) {
            registry2.add(childState);
        }
        return mergeState;
    }

    public static String addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
//		debug("entering addSuffixNextStep");
        State newState;
        String rhs = "";
        if (index == (suffixlength-1)) {
//			System.out.println("Creating new state1: " + newStateCount + " suffex for prev node: " + currentSuffix.charAt(index));
//			System.out.println("ADDING FINAL STATE: " + newStateCount);
            newState = new State(newStateCount++, true);
            states.add(newState);
            s.addLink(newState, currentSuffix.charAt(index));
            rhs = currentSuffix.charAt(index) + "";
//			System.out.println("*********adding this to rhs of this: " + rhs + " : " + s.nameNumber);
//            s.rightHandStrings.add(rhs);
            return currentSuffix.substring(index, index) + rhs;
        } else {
            int temp = newStateCount;

            newState = new State(newStateCount++, false);
            states.add(newState);
            s.addLink(newState, currentSuffix.charAt(index));
            rhs = addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
//			System.out.println("Creating new state2: " + temp + " suffex for prev node: " + currentSuffix.charAt(index) + rhs);
//			System.out.println("*********adding this to rhs of this2: " + currentSuffix.charAt(index) + rhs + " : " + s.nameNumber);
//            s.rightHandStrings.add(currentSuffix.charAt(index) + rhs);
        }
//		debug("exiting addSuffixNextStep");
        return currentSuffix.substring(index, index+1) + rhs;
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
//		debug("entering addSuffixNextStep");
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
//				System.out.println("ADDING FINAL STATE: " + newStateCount);
                newState = new State(newStateCount++, true);
                states.add(newState);
                State lastStateState = states.get(lastState);
//				System.out.println("adding linkPPP: " + currentSuffix.charAt(0));
//				String rhs = addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
//				System.out.println("Adding stateeee: " + (newStateCount-1) + " to state " + lastStateState.nameNumber);
                // following two lines might need to be taken out
                String rhs = currentSuffix.charAt(0) + "";
//				System.out.println("*********adding this to rhs of this3: " + rhs + " : " + lastStateState.nameNumber);
//                lastStateState.rightHandStrings.add(rhs);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
            } else {
                newState = new State(newStateCount++, false);
                states.add(newState);
                State lastStateState = states.get(lastState);
//				System.out.println("Adding stateeee2: " + (newStateCount-1) + "current suffix to add: " + currentSuffix.charAt(0));
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                String rhs = addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
//				System.out.println("*********adding this to rhs of this4: " + currentSuffix.charAt(0) + rhs + " : " + lastStateState.nameNumber);
//                lastStateState.rightHandStrings.add(currentSuffix.charAt(0) + rhs);
            }
        }
//		debug("exiting addSuffixNextStep");
    }

    public static void main(String[] args) throws IOException {
        String commonPrefix = null;
        String currentSuffix = null;

        //Create the start state
//		System.out.println("ADDING FINAL STATE: " + 0);
        startState = new State(0, true);
        states.add(startState);

        //set up reader for input of asciibetically sorted dictionary in the format one word per line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        word = br.readLine();
        while (word != null) {
            System.out.println(word);
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length(), word.length());

//			System.out.println(" word: " + word);
//			System.out.println(" common Prefix: " + commonPrefix);
//			System.out.println(" currentSuffix: " + currentSuffix);
//			System.out.println(" last state: " + lastState);
//			System.out.println("-------------");

            if (states.get(lastState).hasChildren()) {
                State lastStateState = states.get(lastState);
                State mergeState = replaceOrRegister(states.get(lastState));
                State childState = lastStateState.lastLinkAdded;
//				System.out.println(lastStateState.nameNumber + " chlid " + childState.nameNumber);

                if (mergeState != null) {
//					System.out.println("removing node...: " + childState.nameNumber + " and linking state: " + lastStateState.nameNumber + " "
//							+ "with state: " + mergeState.nameNumber + " with link " + lastStateState.charOfLastLinkAdded);
                    Character charOfLastLinkAdded = lastStateState.charOfLastLinkAdded;
//					System.out.println("removing state " + childState.nameNumber);
//					states.remove(childState.nameNumber);//Nicole
                    states.set(childState.nameNumber, null);
                    lastStateState.removeLink(childState, lastStateState.charOfLastLinkAdded);
                    lastStateState.addLink(mergeState, charOfLastLinkAdded);
                    childState = null;
                }
//				System.out.println("HERE");
            }
            addSuffix(currentSuffix);



            word = br.readLine();
        }
        replaceOrRegister(startState);
        System.out.println("do DFS");
        doDFS();
    }

}
