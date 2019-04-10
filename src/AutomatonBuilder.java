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
    private static ArrayList<State> registry = new ArrayList<>();
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
//        System.out.println(str);
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
        countGlobal++;
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2 = new ArrayList<>();
        ArrayList<String> results3 = new ArrayList<>();
        int size = state.statesOfLinks.size();
        for (int i = 0; i < size; i++) {
            results3 = doDFSnextStep(state.statesOfLinks.get(i));
            for (int j = 0; j < results3.size(); j++) {
                results.add(state.links.get(i) + results3.get(j));
            }
        }
        if (state.acceptState == true) {
            results.add("");
        }
        return results;
    }

    public static String nextStep(State nextState, int wordIndex, int wordLength) {
        debug("enter nextStep");
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

        int size = startState.links.size();
        boolean foundNewState = false;
        for (int i = 0; i < size; i++) {
            char firstLetter = word.charAt(0);
            if (startState.links.get(i).equals(firstLetter)) {
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
        State childState = state.lastLinkAdded;
        if (childState.hasChildren()) {
            State mergeState = replaceOrRegister(childState);
            if (mergeState != null) {
                State parentState = childState;
                State newChildSTate = childState.lastLinkAdded;
                Character charOfLastLinkAdded = parentState.charOfLastLinkAdded;
                states.set(newChildSTate.nameNumber, null);
                parentState.removeLink(newChildSTate, parentState.charOfLastLinkAdded);
                parentState.addLink(mergeState, charOfLastLinkAdded);
                newChildSTate = null;
            }
        }

        State mergeState = null;

        String checkReg = "";
        String checkChild = "";
        int registrySize = registry.size();
        for (int i = 0; i < registrySize; i++) {
            State registryState = registry.get(i);
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
            if (checkReg.equals(checkChild)) {
                mergeState = registryState;
                break;
            }
        }
        if (mergeState == null) {
            registry.add(childState);
        }
        debug("exit replaceOrRegister");
        return mergeState;
    }

    public static String addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
        debug("enter addSuffixNextStep");
        State newState;
        String rhs = "";
        if (index == (suffixlength-1)) {
            newState = new State(newStateCount++, true);
            states.add(newState);
            s.addLink(newState, currentSuffix.charAt(index));
            rhs = currentSuffix.charAt(index) + "";
            debug("exit addSuffixNextStep");
            return currentSuffix.substring(index, index) + rhs;
        } else {
            int temp = newStateCount;
            newState = new State(newStateCount++, false);
            states.add(newState);
            s.addLink(newState, currentSuffix.charAt(index));
            rhs = addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
        }
        debug("exit addSuffixNextStep");
        return currentSuffix.substring(index, index+1) + rhs;
    }

    public static void addSuffix(String currentSuffix) {
        debug("enter addSuffix");
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
                newState = new State(newStateCount++, true);
                states.add(newState);
                State lastStateState = states.get(lastState);
                // following two lines might need to be taken out
                String rhs = currentSuffix.charAt(0) + "";
                lastStateState.addLink(newState, currentSuffix.charAt(0));
            } else {
                newState = new State(newStateCount++, false);
                states.add(newState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                String rhs = addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
            }
        }
        debug("exit addSuffix");
    }

    public static void main(String[] args) throws IOException {
        debug("enter main");
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
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length(), word.length());
            if (states.get(lastState).hasChildren()) {
                State lastStateState = states.get(lastState);
                State mergeState = replaceOrRegister(states.get(lastState));
                State childState = lastStateState.lastLinkAdded;

                if (mergeState != null) {
                    Character charOfLastLinkAdded = lastStateState.charOfLastLinkAdded;
                    states.set(childState.nameNumber, null);
                    lastStateState.removeLink(childState, lastStateState.charOfLastLinkAdded);
                    lastStateState.addLink(mergeState, charOfLastLinkAdded);
                    childState = null;
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
