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

//    private static ArrayList<State> states = new ArrayList<>();
    private static HashMap<Integer, State> states = new HashMap<>();
//    private static ArrayList<State> registry = new ArrayList<>();
    private static HashMap<String, State> registry = new HashMap<>();
    private static String word = null;
    private static int lastState = 0;
    private static State startState = null;
    private static int newStateCount = 1;
    private static String suffexToAdd = "";
    private static int countGlobal = 0;

    public static void debug(String str) {
//		System.out.println(str);
    }

    public static void debug2(String str) {
        System.out.println(str);
    }

    public static void debug3(String str) {
        System.out.println(str);
    }

    public static void doDFS() {
//		System.out.println("entering DFS staring at node: " + startState.nameNumber);
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();

        for (EdgeInfo edgeInfo: startState.getEdges().values()) {
            results2 = doDFSnextStep(edgeInfo.getedgeToState());
            for (int j = 0; j < results2.size(); j++) {
                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    results.add(edgeInfo.getEdgeChars().get(k) + results2.get(j));
                }
//                results.add(edgeInfo.getEdgeChar() + results2.get(j));
            }
        }


//        int size = startState.outgoingEdges.size();
//        for (int i = 0; i < size; i++) {
////            System.out.println(startState.links.get(i));
//            results2 = doDFSnextStep(startState.outgoingEdges.get(i).getedgeToState());
//            for (int j = 0; j < results2.size(); j++) {
//                results.add(startState.outgoingEdges.get(i).getEdgeChar() + results2.get(j));
//            }
//        }
        for (int i = 0; i < results.size(); i++) {
            debug2(results.get(i));
        }
//		System.out.println("exiting DFS staring at node: " + startState.nameNumber);
    }

    public static ArrayList<String> doDFSnextStep(State state) {
        if (state.visited == false) {
            countGlobal++;
            state.visited = true;
        }
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();
        ArrayList<String> results3 = new ArrayList<>();

        for (EdgeInfo edgeInfo : state.getEdges().values()) {
            results3 = doDFSnextStep(edgeInfo.getedgeToState());
            for (int j = 0; j < results3.size(); j++) {
                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    results.add(edgeInfo.getEdgeChars().get(k) + results3.get(j));
                }

//                results.add(edgeInfo.getEdgeChar() + results3.get(j));
            }
        }

//        int size = state.outgoingEdges.size();
//        for (int i = 0; i < size; i++) {
//            results3 = doDFSnextStep(state.outgoingEdges.get(i).getedgeToState());
//            for (int j = 0; j < results3.size(); j++) {
//                results.add(state.outgoingEdges.get(i).getEdgeChar() + results3.get(j));
//            }
//        }
        if (state.isAcceptState() == true) {
            results.add("");
        }
        return results;
    }

    public static String nextStep(State nextState, int wordIndex, int wordLength) {
        debug("enter nextStep");
        String commonPrefixSegment = "";
//        int size = nextState.outgoingEdges.size();
        boolean foundNewState = false;
        char nextLetter = '\u0000';

        for (EdgeInfo edgeInfo : nextState.getEdges().values()) {
            nextLetter = word.charAt(wordIndex);

            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                System.out.println("word.charat0 next step : " + nextLetter);
                System.out.println("edge char next step : " + edgeInfo.getEdgeChars().get(k));
                System.out.println("start state number next step : " + nextState.getNameNumber());
                if (edgeInfo.getEdgeChars().get(k).equals(nextLetter)) {
                    if (wordIndex == wordLength) {
                        foundNewState = true;
                    } else {
                        commonPrefixSegment = nextLetter + nextStep(edgeInfo.getedgeToState(), wordIndex+1, wordLength);
                        foundNewState = true;
                    }
                }
            }


//            System.out.println("word.charat0 next step : " + nextLetter);
//            System.out.println("edge char next step : " + edgeInfo.getEdgeChar());
//            System.out.println("start state number next step : " + nextState.getNameNumber());
//            if (edgeInfo.getEdgeChar().equals(nextLetter)) {
//                if (wordIndex == wordLength) {
//                    foundNewState = true;
//                } else {
//                    commonPrefixSegment = nextLetter + nextStep(edgeInfo.getedgeToState(), wordIndex+1, wordLength);
//                    foundNewState = true;
//                }
//            }
        }

//        for (int i = 0; i < size; i++) {
//            nextLetter = word.charAt(wordIndex);
//            if (nextState.outgoingEdges.get(i).getEdgeChar().equals(nextLetter)) {
//                if (wordIndex == wordLength) {
//                    foundNewState = true;
//                } else {
//                    commonPrefixSegment = nextLetter + nextStep(nextState.outgoingEdges.get(i).getedgeToState(), wordIndex+1, wordLength);
//                    foundNewState = true;
//                }
//            }
//        }
        if (!foundNewState) {

            lastState = nextState.getNameNumber();
            System.out.println("last state updated in next step to: " + lastState);
            if (wordIndex != wordLength) {
                suffexToAdd = word.substring(wordIndex+1, word.length());
            }
        }
        debug("exit nextStep");
        return commonPrefixSegment;
    }

    public static String findCommonPrefix() {
        debug("enter findCommonPrefix");
        String commonPrefix = "";

        int wordLength = word.length();
        if (wordLength == 0) {
            System.out.println("error - empty work.. issue");
        }

        boolean foundNewState = false;
        for (EdgeInfo edgeInfo : startState.getEdges().values()) {
            char firstLetter = word.charAt(0);

            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                System.out.println("word.charat0: " + firstLetter);
                System.out.println("edge char: " + edgeInfo.getEdgeChars().get(k));
                System.out.println("start state number: " + startState.getNameNumber());
                if (edgeInfo.getEdgeChars().get(k).equals(firstLetter)) {
                    System.out.println("in here");
                    if (wordLength == 1) {
                        foundNewState = true;
                    } else {
                        commonPrefix = firstLetter + nextStep(edgeInfo.getedgeToState(), 1, wordLength);
                        foundNewState = true;
                    }
                }
            }

//            System.out.println("word.charat0: " + firstLetter);
//            System.out.println("edge char: " + edgeInfo.getEdgeChar());
//            System.out.println("start state number: " + startState.getNameNumber());
//            if (edgeInfo.getEdgeChar().equals(firstLetter)) {
//                System.out.println("in here");
//                if (wordLength == 1) {
//                    foundNewState = true;
//                } else {
//                    commonPrefix = firstLetter + nextStep(edgeInfo.getedgeToState(), 1, wordLength);
//                    foundNewState = true;
//                }
//            }
        }


//        int size = startState.outgoingEdges.size();
//        boolean foundNewState = false;
//        for (int i = 0; i < size; i++) {
//            char firstLetter = word.charAt(0);
//            System.out.println("word.charat0: " + firstLetter);
//            System.out.println("edge char: " + startState.outgoingEdges.get().getEdgeChar());
//            System.out.println("start state number: " + startState.nameNumber);
//            if (startState.outgoingEdges.get(i).getEdgeChar().equals(firstLetter)) {
//                System.out.println("in here");
//                if (wordLength == 1) {
//                    foundNewState = true;
//                } else {
//                    commonPrefix = firstLetter + nextStep(startState.outgoingEdges.get(i).getedgeToState(), 1, wordLength);
//                    foundNewState = true;
//                }
//            }
//        }
        if (!foundNewState) {
            System.out.println("last state updated to: " + lastState);
            lastState = 0;
        }

        // update last state in here
        debug("exit findCommonPrefix");
        return commonPrefix;
    }

    // This method compares two strings
    // lexicographically without using
    // library functions
    public static int stringCompare(String str1, String str2)
    {debug("enter stringCompare");

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                debug("exit stringCompare");
                return str1_ch - str2_ch;
            }
        }

        // Edge case for strings like
        // String 1="Geeks" and String 2="Geeksforgeeks"
        if (l1 != l2) {
            debug("exit stringCompare");
            return l1 - l2;
        }

        // If none of the above conditions is true,
        // it implies both the strings are equal
        else {
            debug("exit stringCompare");
            return 0;
        }
    }

    public static State replaceOrRegister(State state) {
        debug("enter replaceOrRegister");
        System.out.println("enter replaceOrRegister");
        State childState = state.getLastLinkAdded();
        if (childState.hasChildren()) {
            State mergeState = replaceOrRegister(childState);
            if (mergeState != null) {
                State parentState = childState;
                State newChildState = childState.getLastLinkAdded();
                Character charOfLastLinkAdded = parentState.getCharOfLastLinkAdded();
//                states.set(newChildState.getNameNumber(), null);
                states.remove(childState.getNameNumber());
                System.out.println("removing state: " + newChildState.getNameNumber());
                System.out.println("Addinglink to state: " + mergeState.getNameNumber());
                parentState.removeLink(newChildState, parentState.getCharOfLastLinkAdded());
                parentState.addLink(mergeState, charOfLastLinkAdded);
//                newChildState = null;
            }
        }

        State mergeState = null;

//        String checkReg = "";
//        String checkChild = "";
//        int registrySize = registry.size();
//        for (int i = 0; i < registrySize; i++) {
//            State registryState = registry.get(i);
//            ArrayList<Character> registryOutgoingEdges = registryState.links;
//            ArrayList<Character> childOutgoingEdges = childState.links;
//            // GET STRING FOR REGISTRY STATE
//            if (registryState.acceptState == true) {
//                checkReg = "1";
//            } else {
//                checkReg = "0";
//            }
//            int numberOfEdges = registryOutgoingEdges.size();
//            for (int j = 0; j < numberOfEdges; j++) {
//                checkReg = checkReg + registryOutgoingEdges.get(j) + registryState.statesOfLinks.get(j).nameNumber;
//            }
//            // GET STRING FOR CHILD STATE
//            if (childState.acceptState == true) {
//                checkChild = "1";
//            } else {
//                checkChild = "0";
//            }
//            numberOfEdges = childOutgoingEdges.size();
//            for (int j = 0; j < numberOfEdges; j++) {
//                checkChild = checkChild + childOutgoingEdges.get(j) + childState.statesOfLinks.get(j).nameNumber;
//            }
//            // COMPARE THE TWO STRINGS
//            if (checkReg.equals(checkChild)) {
//                mergeState = registryState;
//                break;
//            }
//        }
//        if (mergeState == null) {
//            registry.add(childState);
//        }

        String checkReg = "";
        String checkChild = "";
//        int registrySize = registry.size();
//        for (int i = 0; i < registrySize; i++) {
//            State registryState = registry.get(i);
//            ArrayList<Character> registryOutgoingEdges = registryState.links;
            HashMap<Integer, EdgeInfo> childOutgoingEdges = childState.getEdges();
//            // GET STRING FOR REGISTRY STATE
//            if (registryState.acceptState == true) {
//                checkReg = "1";
//            } else {
//                checkReg = "0";
//            }
//            int numberOfEdges = registryOutgoingEdges.size();
//            for (int j = 0; j < numberOfEdges; j++) {
//                checkReg = checkReg + registryOutgoingEdges.get(j) + registryState.statesOfLinks.get(j).nameNumber;
//            }
            // GET STRING FOR CHILD STATE
            if (childState.isAcceptState() == true) {
                checkChild = "1";
            } else {
                checkChild = "0";
            }

            for (EdgeInfo edgeInfo : childOutgoingEdges.values()) {
                int numEdges = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numEdges; k++) {
                    checkChild = checkChild + edgeInfo.getEdgeChars().get(k) + edgeInfo.getedgeToState().getNameNumber();
                }
//                checkChild = checkChild + edgeInfo.getEdgeChar() + edgeInfo.getedgeToState().getNameNumber();
            }

//            int numberOfEdges = childOutgoingEdges.size();
//            for (int j = 0; j < numberOfEdges; j++) {
//                checkChild = checkChild + childOutgoingEdges.get(j).getEdgeChar() + childState.outgoingEdges.get(j).getedgeToState().nameNumber;
//            }

            mergeState = registry.get(checkChild);
            // COMPARE THE TWO STRINGS
//            if (checkReg.equals(checkChild)) {
//                mergeState = registryState;
//                break;
//            }
//        }
        if (mergeState == null) {
            registry.put(checkChild, childState);
//            registry.add(checkChild, childState);
        }


        debug("exit replaceOrRegister");
        System.out.println("exit replaceOrRegister");
        return mergeState;
    }

    public static void addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
        debug("enter addSuffixNextStep");
        State newState;
//        String rhs = "";
        if (index == (suffixlength-1)) {
            System.out.println("creating new state1: " + newStateCount);
            newState = new State(newStateCount++, true);
//            states.add(newState);
            states.put(newState.getNameNumber(), newState);
            s.addLink(newState, currentSuffix.charAt(index));
//            rhs = currentSuffix.charAt(index) + "";
            debug("exit addSuffixNextStep");
//            return currentSuffix.substring(index, index) + rhs;

        } else {
            int temp = newStateCount;
            System.out.println("creating new state2: " + newStateCount);
            newState = new State(newStateCount++, false);
            states.put(newState.getNameNumber(), newState);
//            states.add(newState);
            s.addLink(newState, currentSuffix.charAt(index));
//            rhs = addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
            addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
        }
        debug("exit addSuffixNextStep");
//        System.out.println(currentSuffix.substring(index, index+1) + rhs);
//        return currentSuffix.substring(index, index+1) + rhs;
//
    }

    public static void addSuffix(String currentSuffix) {
        debug("enter addSuffix");
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
                System.out.println("creating new state3: " + newStateCount);
                newState = new State(newStateCount++, true);
//                states.add(newState);
                states.put(newState.getNameNumber(), newState);
                System.out.println("last state here: " + lastState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
            } else {
                System.out.println("creating new state4: " + newStateCount);
                newState = new State(newStateCount++, false);
//                states.add(newState);
                states.put(newState.getNameNumber(), newState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
//                String rhs = addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
                addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
            }
        }
        debug("exit addSuffix");
    }

    public static void main(String[] args) throws IOException {
        debug("enter main");
        String commonPrefix;
        String currentSuffix;

        //Create the start state
//		System.out.println("ADDING FINAL STATE: " + 0);
        System.out.println("creating new state: " + 0);
        startState = new State(0, true);
//        states.add(startState);
        states.put(startState.getNameNumber(), startState);

        //set up reader for input of asciibetically sorted dictionary in the format one word per line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        word = br.readLine();
        while (word != null) {
            System.out.println("__________________");
            System.out.println("word is: " + word);
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length());
            System.out.println("commonPrefix: " + commonPrefix);
            System.out.println("currentSuffix: " + currentSuffix);
            System.out.println("__________________");
            if (states.get(lastState).hasChildren()) {
                State lastStateState = states.get(lastState);
                State mergeState = replaceOrRegister(states.get(lastState));
                State childState = lastStateState.getLastLinkAdded();

                if (mergeState != null) {
                    Character charOfLastLinkAdded = lastStateState.getCharOfLastLinkAdded();
//                    states.set(childState.getNameNumber(), null);
                    states.remove(childState.getNameNumber());
                    System.out.println("removing state: " + childState.getNameNumber());
                    System.out.println("Addinglink to state: " + mergeState.getNameNumber());
                    lastStateState.removeLink(childState, lastStateState.getCharOfLastLinkAdded());
                    lastStateState.addLink(mergeState, charOfLastLinkAdded);
//                    childState = null;
                }
            }
            addSuffix(currentSuffix);

            word = br.readLine();
        }
        replaceOrRegister(startState);
        debug("exit main");

//        System.out.println("do DFS");
        doDFS();
        System.out.println("count: " + countGlobal);
    }

}
