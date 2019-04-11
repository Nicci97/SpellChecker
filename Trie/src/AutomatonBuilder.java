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

    private static HashMap<Integer, State> states = new HashMap<>();
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
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();

        for (EdgeInfo edgeInfo: startState.getEdges().values()) {
            results2 = doDFSnextStep(edgeInfo.getEdgeToState());
            for (int j = 0; j < results2.size(); j++) {
                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    results.add(edgeInfo.getEdgeChars().get(k) + results2.get(j));
                }
            }
        }

        for (int i = 0; i < results.size(); i++) {
            debug2(results.get(i));
        }
    }

    public static ArrayList<String> doDFSnextStep(State state) {
        if (state.visited == false) {
            countGlobal++;
            state.visited = true;
        }
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results3;

        for (EdgeInfo edgeInfo : state.getEdges().values()) {
            results3 = doDFSnextStep(edgeInfo.getEdgeToState());
            for (int j = 0; j < results3.size(); j++) {
                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    results.add(edgeInfo.getEdgeChars().get(k) + results3.get(j));
                }
            }
        }
        if (state.isAcceptState() == true) {
            results.add("");
        }
        return results;
    }

    public static String nextStep(State nextState, int wordIndex, int wordLength) {
        debug("enter nextStep");
        String commonPrefixSegment = "";
        boolean foundNewState = false;
        char nextLetter = '\u0000';
        for (EdgeInfo edgeInfo : nextState.getEdges().values()) {
            nextLetter = word.charAt(wordIndex);

            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                if (edgeInfo.getEdgeChars().get(k).equals(nextLetter)) {
                    if (wordIndex == wordLength) {
                        foundNewState = true;
                    } else {
                        commonPrefixSegment = nextLetter + nextStep(edgeInfo.getEdgeToState(), wordIndex + 1, wordLength);
                        foundNewState = true;
                    }
                }
            }
        }
        if (!foundNewState) {
            lastState = nextState.getNameNumber();
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
                if (edgeInfo.getEdgeChars().get(k).equals(firstLetter)) {
                    if (wordLength == 1) {
                        foundNewState = true;
                    } else {
                        commonPrefix = firstLetter + nextStep(edgeInfo.getEdgeToState(), 1, wordLength);
                        foundNewState = true;
                    }
                }
            }
        }
        if (!foundNewState) {
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
        State childState = state.getLastLinkAdded();
        if (childState.hasChildren()) {
            State mergeState = replaceOrRegister(childState);
            if (mergeState != null) {
                State parentState = childState;
                State newChildState = childState.getLastLinkAdded();
                Character charOfLastLinkAdded = parentState.getCharOfLastLinkAdded();
                states.remove(childState.getNameNumber());
                parentState.removeLink(newChildState, parentState.getCharOfLastLinkAdded());
                parentState.addLink(mergeState, charOfLastLinkAdded);
            }
        }

        State mergeState = null;

        String checkReg = "";
        String checkChild = "";
            HashMap<Integer, EdgeInfo> childOutgoingEdges = childState.getEdges();
            // GET STRING FOR CHILD STATE
            if (childState.isAcceptState() == true) {
                checkChild = "1";
            } else {
                checkChild = "0";
            }

            for (EdgeInfo edgeInfo : childOutgoingEdges.values()) {
                int numEdges = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numEdges; k++) {
                    checkChild = checkChild + edgeInfo.getEdgeChars().get(k) + edgeInfo.getEdgeToState().getNameNumber();
                }
            }

            mergeState = registry.get(checkChild);

        if (mergeState == null) {
            registry.put(checkChild, childState);
        }

        debug("exit replaceOrRegister");
        return mergeState;
    }

    public static void addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
        debug("enter addSuffixNextStep");
        State newState;
        if (index == (suffixlength-1)) {
            newState = new State(newStateCount++, true);
            states.put(newState.getNameNumber(), newState);
            s.addLink(newState, currentSuffix.charAt(index));
            debug("exit addSuffixNextStep");
        } else {
            newState = new State(newStateCount++, false);
            states.put(newState.getNameNumber(), newState);
            s.addLink(newState, currentSuffix.charAt(index));
            addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
        }
        debug("exit addSuffixNextStep");
    }

    public static void addSuffix(String currentSuffix) {
        debug("enter addSuffix");
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
                newState = new State(newStateCount++, true);
                states.put(newState.getNameNumber(), newState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
            } else {
                newState = new State(newStateCount++, false);
                states.put(newState.getNameNumber(), newState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
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
        startState = new State(0, true);
        states.put(startState.getNameNumber(), startState);

        //set up reader for input of asciibetically sorted dictionary in the format one word per line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        word = br.readLine();
        while (word != null) {
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length());
            if (states.get(lastState).hasChildren()) {
                State lastStateState = states.get(lastState);
                State mergeState = replaceOrRegister(states.get(lastState));
                State childState = lastStateState.getLastLinkAdded();
                if (mergeState != null) {
                    Character charOfLastLinkAdded = lastStateState.getCharOfLastLinkAdded();
                    states.remove(childState.getNameNumber());
                    lastStateState.removeLink(childState, lastStateState.getCharOfLastLinkAdded());
                    lastStateState.addLink(mergeState, charOfLastLinkAdded);
                }
            }
            addSuffix(currentSuffix);
            word = br.readLine();
        }
        replaceOrRegister(startState);
        debug("exit main");

        doDFS();
        System.out.println("count: " + countGlobal);
    }

}
