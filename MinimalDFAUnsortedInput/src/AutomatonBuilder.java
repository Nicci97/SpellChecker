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
    private static String suffexToAdd;
    private static int countGlobal = 0;
    private static State firstState = null;
    private static State secondLastState = null;
    private static int currentIndex = 0;
    private static State currentState = null;

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
        System.out.println("Number of words in the output language: " + results.size());
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
        if (wordIndex != wordLength) {
            nextLetter = word.charAt(wordIndex);
            for (EdgeInfo edgeInfo : nextState.getEdges().values()) {


                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    if (edgeInfo.getEdgeChars().get(k).equals(nextLetter)) {
                        if (wordLength == wordIndex+1) {
                            commonPrefixSegment = nextLetter + "";
                            lastState = edgeInfo.getEdgeToState().getNameNumber();
                            foundNewState = true;
                            break;
                        } else {

                        commonPrefixSegment = nextLetter + nextStep(edgeInfo.getEdgeToState(), wordIndex + 1, wordLength);
                        foundNewState = true;
                        break;
                        }
//                        if (wordIndex == wordLength) {
//                            commonPrefixSegment = nextLetter + "";
//                            System.out.println("last state here: " + edgeInfo.getEdgeToState().getNameNumber());
//                            lastState = edgeInfo.getEdgeToState().getNameNumber();
////                        commonPrefixSegment = nextLetter + "";
////                        System.out.println("making state final: " + edgeInfo.getEdgeToState().getNameNumber());
////                        edgeInfo.getEdgeToState().makeFinal();
//                            foundNewState = true;
//                        } else {
                        // commonPrefixSegment = nextLetter + nextStep(edgeInfo.getEdgeToState(), wordIndex + 1, wordLength);
                        // foundNewState = true;
//                        }
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
//        }

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
//            System.out.println("WORD LENGTH: " + word.length());
            char firstLetter = word.charAt(0);

            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                if (edgeInfo.getEdgeChars().get(k).equals(firstLetter)) {
                    if (wordLength == 1) {
                        commonPrefix = firstLetter + "";
                        lastState = edgeInfo.getEdgeToState().getNameNumber();
//                        System.out.println("making state final: " + edgeInfo.getEdgeToState().getNameNumber());
//                        edgeInfo.getEdgeToState().makeFinal();
                        foundNewState = true;
                        break;
                    } else {
                        commonPrefix = firstLetter + nextStep(edgeInfo.getEdgeToState(), 1, wordLength);
                        foundNewState = true;
                        break;
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

    public static void replaceOrRegister(State state, Character nextStep) {
        debug("enter replaceOrRegister");
//        System.out.println("in replace or register");

        State childState = null;
        Character edge = null;
//        System.out.println(" i am " + state.getNameNumber());
        for (EdgeInfo edgeInfo: state.getEdges().values()) {
            int sameEdgesSize = edgeInfo.getEdgeChars().size();
            for (int j = 0; j < sameEdgesSize; j++) {
                if (edgeInfo.getEdgeChars().get(j).equals(nextStep)) {
                    childState = edgeInfo.getEdgeToState();
//                    System.out.println(edgeInfo.getEdgeChars().get(j));
                    edge = edgeInfo.getEdgeChars().get(j);
                }
//                System.out.println("all: " + edgeInfo.getEdgeChars().get(j));
            }
        }
//
//        System.out.println("STATTE " + state.getNameNumber() + ":::::::" + nextStep);
//        System.out.println("edge: " + edge);
        if (childState != null) {
//            System.out.println("here1");
            String hash = getHash(childState);
            if (registry.get(hash) != null) {

                State mergeState = registry.get(hash);
//                System.out.println("replacing state: " + childState.getNameNumber() + " with hash " + hash + " and merging with state: " + mergeState.getNameNumber());
//                System.out.println("making state: " + state.getNameNumber() + " point to " + mergeState.getNameNumber() + " on a " + edge);

//                State parentState = childState;
//                State newChildState = childState.getLastLinkAdded();
//                Character charOfLastLinkAdded = parentState.getCharOfLastLinkAdded();

//                System.out.println("removing state: " + childState.getNameNumber());
                state.removeLink(childState, edge);
                states.remove(childState.getNameNumber());
                state.addLink(mergeState, edge);


                String oldHash = null;
                for (String currentHash: registry.keySet()) {
                    if (registry.get(currentHash).getNameNumber() == state.getNameNumber()) {
                        registry.remove(currentHash);
                        registry.put(hash, state);
                                break;
                    }
                }
                /////////////////
//                State mergeState = registry.get(hash);
//                State parentState = childState;
//                State newChildState = childState.getLastLinkAdded();
//                Character charOfLastLinkAdded = parentState.getCharOfLastLinkAdded();
//                states.remove(childState.getNameNumber());
//                parentState.removeLink(newChildState, parentState.getCharOfLastLinkAdded());
//                parentState.addLink(mergeState, charOfLastLinkAdded);
            } else {
//                System.out.println("registering state: " + childState.getNameNumber() + " with hash " + hash);
                hash = getHash(childState);
                String oldHash = null;
                for (String h: registry.keySet()) {
                    if (registry.get(h).getNameNumber() == childState.getNameNumber()) {
                        oldHash = h;
//                        System.out.println("removing old hash111: " + oldHash + " from registry with node: " + registry.get(h).getNameNumber());
                        registry.remove(h);
                        break;
                    }
                }
//                System.out.println("putting in registry hash111: " + hash + " from registry with node: " + childState.getNameNumber());
                registry.put(hash, childState);


//                registry.put(hash, childState);
            }
        }




//        State childState = state.getLastLinkAdded();
//        if (childState.hasChildren()) {
//            State mergeState = replaceOrRegister(childState);
//            if (mergeState != null) {
//                State parentState = childState;
//                State newChildState = childState.getLastLinkAdded();
//                Character charOfLastLinkAdded = parentState.getCharOfLastLinkAdded();
//                states.remove(childState.getNameNumber());
//                parentState.removeLink(newChildState, parentState.getCharOfLastLinkAdded());
//                parentState.addLink(mergeState, charOfLastLinkAdded);
//            }
//        }
//
//        State mergeState = null;
//
//        String checkReg = "";
//        String checkChild = "";
//        HashMap<Integer, EdgeInfo> childOutgoingEdges = childState.getEdges();
//        // GET STRING FOR CHILD STATE
//        if (childState.isAcceptState() == true) {
//            checkChild = "1";
//        } else {
//            checkChild = "0";
//        }
//
//        for (EdgeInfo edgeInfo : childOutgoingEdges.values()) {
//            int numEdges = edgeInfo.getEdgeChars().size();
//            for (int k = 0; k < numEdges; k++) {
//                checkChild = checkChild + edgeInfo.getEdgeChars().get(k) + edgeInfo.getEdgeToState().getNameNumber();
//            }
//        }
//
//        mergeState = registry.get(checkChild);
//
//        if (mergeState == null) {
//            registry.put(checkChild, childState);
//        }
//
//        debug("exit replaceOrRegister");
//        return mergeState;
//        System.out.println("out replace or register");
        debug("exit replaceOrRegister");
    }

    public static void addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
        debug("enter addSuffixNextStep");
        State newState;
        if (index == (suffixlength-1)) {
            newState = new State(newStateCount++, true);
            states.put(newState.getNameNumber(), newState);
            s.addLink(newState, currentSuffix.charAt(index));
            secondLastState = s;
            debug("exit addSuffixNextStep");
        } else {
            newState = new State(newStateCount++, false);
            states.put(newState.getNameNumber(), newState);
            s.addLink(newState, currentSuffix.charAt(index));
            addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
        }
        // new line.. hope this is right
        replaceOrRegister(s, currentSuffix.charAt(index));
        debug("exit addSuffixNextStep");
    }

    public static void addSuffix(String currentSuffix) {
        secondLastState = null;
        debug("enter addSuffix");
        if (currentSuffix.equals("")) {
//            System.out.println("making state: " + states.get(lastState).getNameNumber() + " final");
            states.get(lastState).makeFinal();
        }
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
                newState = new State(newStateCount++, true);
                states.put(newState.getNameNumber(), newState);
                State lastStateState = states.get(lastState);
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                secondLastState = lastStateState;
                replaceOrRegister(lastStateState, currentSuffix.charAt(0));
            } else {
                newState = new State(newStateCount++, false);
                states.put(newState.getNameNumber(), newState);
//                System.out.println(lastState);
                State lastStateState = states.get(lastState);
//                System.out.println("lastlastlastlast state: " + lastStateState.getNameNumber());
//                System.out.println("HERERERERERER: " + currentSuffix.charAt(0));
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                addSuffixNextStep(newState, 1, suffixLength, currentSuffix);
                replaceOrRegister(lastStateState, currentSuffix.charAt(0));
            }
        }
        debug("exit addSuffix");
    }

    public static void firstStateNextStep(State currentState, String commonPrefix, int index) {
        currentIndex++;
        int cpLength = commonPrefix.length();
        if (cpLength != index) {
//            System.out.println("current state num incoming edges: " + currentState.getNameNumber() + " : " + currentState.getNumberIncomingEdges());
            if (currentState.getNumberIncomingEdges() > 1) {
                firstState = currentState;
            } else {
                for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
                    int sameEdgesSize = edgeInfo.getEdgeChars().size();
                    for (int j = 0; j < sameEdgesSize; j++) {
                        if (edgeInfo.getEdgeChars().get(j).equals(commonPrefix.charAt(index))) {
                            firstStateNextStep(edgeInfo.getEdgeToState(), commonPrefix, index+1);
                        }
                    }
                }
            }
        } else if (cpLength == index) {
//            System.out.println("current state num incoming edges: " + currentState.getNameNumber() + " : " + currentState.getNumberIncomingEdges());
            if (currentState.getNumberIncomingEdges() > 1) {
                firstState = currentState;
            }
        }
    }

    public static void firstState(String commonPrefix) {
        currentIndex = 0;
        firstState = null;
        if (commonPrefix.length() != 0) {
            for (EdgeInfo edgeInfo: startState.getEdges().values()) {
                int sameEdgesSize = edgeInfo.getEdgeChars().size();
                for (int j = 0; j < sameEdgesSize; j++) {
                    if (edgeInfo.getEdgeChars().get(j).equals(commonPrefix.charAt(0))) {
                        firstStateNextStep(edgeInfo.getEdgeToState(), commonPrefix, 1);
                    }
                }
            }
        }
    }

//    public static void loopForRR(State s) {
//        if (s.getNameNumber() != secondLastState.getNameNumber()) {
//            loopForRR();
//        } else {
//
//        }
//
//    }


    public static State magicLoopNextStep(State state, String commonPrefix, int index, int upperIndex) {
        index++;
//        State currentState = null;
        if (upperIndex >= index) {
            for (EdgeInfo edgeInfo: state.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(commonPrefix.charAt(index))) {
//                        System.out.println("before next magic loop: " + commonPrefix.charAt(index));
//                        System.out.println("sending to next magic loop: " + edgeInfo.getEdgeToState().getNameNumber());
                        currentState = magicLoopNextStep(edgeInfo.getEdgeToState(), commonPrefix, index, upperIndex);
                        break;
                    }
                }
            }
            if (index >= currentIndex) {
//                System.out.println("to clone: " + state.getNameNumber());
                currentState = clone(state);
                Character transitionToReplace = commonPrefix.charAt(index);
//                System.out.println("trasitionToReplace: " + transitionToReplace);

                for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
                    int numberOfChars = edgeInfo.getEdgeChars().size();
                    for (int i = 0; i < numberOfChars; i++) {
//                        System.out.println(edgeInfo.getEdgeChars().get(i));
                        if (edgeInfo.getEdgeChars().get(i).equals(transitionToReplace)) {
//                            System.out.println("removing link from " + currentState.getNameNumber() + " to " +
//                                    edgeInfo.getEdgeToState().getNameNumber() + " with " + transitionToReplace);
                            currentState.removeLink(edgeInfo.getEdgeToState(), transitionToReplace);
                            break;
                        }
                    }
                }

//                for (EdgeInfo edgeInfo: state.getEdges().values()) {
//                    if (edgeInfo.getEdgeChars().contains(transitionToReplace)) {
//                        System.out.println("removing link from " + currentState.getNameNumber() + " to " +
//                                edgeInfo.getEdgeToState().getNameNumber() + " with " + transitionToReplace);
//                        currentState.removeLink(edgeInfo.getEdgeToState(), transitionToReplace);
//                        break;
//                    }
//                }
//                System.out.println("current state here: " + currentState.getNameNumber());
//                System.out.println("last state here : " + states.get(lastState).getNameNumber());
//                System.out.println("adding link from " + currentState.getNameNumber() + " to " +
//                        states.get(lastState).getNameNumber() + " with " + transitionToReplace);
                currentState.addLink(states.get(lastState), transitionToReplace);
                replaceOrRegister(currentState, commonPrefix.charAt(index));
                lastState = currentState.getNameNumber();
            }
        }
        return currentState;
    }

    public static void magicLoop(String commonPrefix) {
//        State currentState = null;
        int upperIndex = commonPrefix.length()-1;
        int index = 0;

//        System.out.println("upper index: " + upperIndex);
//        System.out.println("current index: " + currentIndex);

        if (upperIndex >= index) {
            for (EdgeInfo edgeInfo: startState.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(commonPrefix.charAt(index))) {
                        currentState = magicLoopNextStep(edgeInfo.getEdgeToState(), commonPrefix, index, upperIndex);
                        break;
                    }
                }
            }
            if (index >= currentIndex) {
                currentState = clone(startState);

                Character transitionToReplace = commonPrefix.charAt(index);
//                System.out.println("trasitionToReplace2: " + transitionToReplace);
                for (EdgeInfo edgeInfo: startState.getEdges().values()) {
                    if (edgeInfo.getEdgeChars().contains(transitionToReplace)) {
//                        System.out.println("removing link from " + currentState.getNameNumber() + " to " +
//                                edgeInfo.getEdgeToState().getNameNumber() + " with " + transitionToReplace);
                        currentState.removeLink(edgeInfo.getEdgeToState(), transitionToReplace);
                        break;
                    }
                }
//                System.out.println("adding linke: " + currentState.getNameNumber() + " " + states.get(lastState).getNameNumber() + " on " + transitionToReplace);
                currentState.addLink(states.get(lastState), transitionToReplace);
                replaceOrRegister(currentState, commonPrefix.charAt(index));
                lastState = currentState.getNameNumber();
            }
        }
//        return currentState;
    }

    private static Character getStateAtIndex(State state, int currentIndex, int index) {
        index++;
        if (currentIndex == index) {
            currentState = state;
//            return state;
            return word.charAt(index);
        } else {
            for (EdgeInfo edgeInfo: state.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(word.charAt(index))) {
//                        currentState = getStateAtIndex(edgeInfo.getEdgeToState(), currentIndex, index);
                        return getStateAtIndex(edgeInfo.getEdgeToState(), currentIndex, index);
                    }
                }
            }
        }
        return null;
    }

    private static String getHash(State state) {
        String hash = "";
        HashMap<Integer, EdgeInfo> childOutgoingEdges = state.getEdges();
        // GET STRING FOR CHILD STATE
        if (state.isAcceptState() == true) {
            hash = "1";
        } else {
            hash = "0";
        }

        for (EdgeInfo edgeInfo : childOutgoingEdges.values()) {
            int numEdges = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numEdges; k++) {
                hash = hash + edgeInfo.getEdgeChars().get(k) + edgeInfo.getEdgeToState().getNameNumber();
            }
        }
        return hash;
    }

    private static void removeFromRegister(State state) {
        String hash = getHash(states.get(lastState));
        registry.remove(hash);
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
        int count = 0;
        while (word != null) {
            lastState = 0;
            firstState = null;
            count++;
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length());
//            System.out.println("---------");
//            System.out.println("word: " + word);
//            System.out.println("common Prefix: " + commonPrefix);
//            System.out.println("current Suffix: " + currentSuffix);
//            System.out.println("---------");

            //TODO if currentSuffix = empty and the transition is valid element of F.... in algorithm, not really sure whats up
//            System.out.println("last state: ::::; " + lastState);
            if (currentSuffix.equals("") && states.get(lastState).isAcceptState()) {
                word = br.readLine();
                continue;
            }

//            if (firstState != null) {
//                System.out.println("first State before: " + firstState.getNameNumber());
//            } else {
//                System.out.println("first State before: " + firstState);
//            }
//            System.out.println("last State before: " + states.get(lastState).getNameNumber());

            firstState(commonPrefix);
//            if (firstState != null) {
//                System.out.println("first State after: " + firstState.getNameNumber());
//            } else {
//                System.out.println("first State after: " + firstState);
//            }
//            System.out.println("last State after: " + states.get(lastState).getNameNumber());
//
//            System.out.println("FIRST STATETEETETETETEETETE: " + firstState);
            if (firstState != null) {
                // make a copy of the last state and make lastState equal to it
//                State lastStateState = states.get(lastState);
//                State newState =  new State(newStateCount++, lastStateState.isAcceptState());
//                for (EdgeInfo edgeInfo: lastStateState.getEdges().values()) {
//                    if (edgeInfo.getEdgeChars().size() == 1) {
//                        newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(0));
//                    } else {
//                        newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(0));
//                        for (int i = 1; i < edgeInfo.getEdgeChars().size(); i++) {
//                        newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(i));
//                        }
//                    }
//                }
//                newState.updatelastAdded(lastStateState.getLastLinkAdded(), lastStateState.getCharOfLastLinkAdded());
//                lastState = newState.getNameNumber();

                lastState = clone(states.get(lastState)).getNameNumber();
//                System.out.println("cloning last state: " + lastState);
            }

            //                    if (currentIndex > 0) {
            removeFromRegister(states.get(lastState));
//                    }
            addSuffix(currentSuffix);

//            State currentState = null;
            State oldState = null;
            if (firstState != null) {
                firstState(commonPrefix);
                magicLoop(commonPrefix);

///////////////////////
                Character nextStep = null;
                if (currentIndex-1 > 0) {
                    nextStep = getStateAtIndex(startState, currentIndex-1, -1);
                } else {
                    nextStep = word.charAt(0);
                    currentState = startState;
                }
                State childState = null;
                Character edge = null;

//                if (currentState == null) {
//                    currentState = startState;
//                    edge = word.charAt(0);
//                    nextStep = edge;
//                    System.out.println("next step::: " + nextStep);
//                }
//                System.out.println("I'm debuggging: " + currentState);
                for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
                    int sameEdgesSize = edgeInfo.getEdgeChars().size();
                    for (int j = 0; j < sameEdgesSize; j++) {
                        if (edgeInfo.getEdgeChars().get(j).equals(nextStep)) {
                            childState = edgeInfo.getEdgeToState();
                            edge = edgeInfo.getEdgeChars().get(j);
                        }
                    }
                }


                if (states.get(lastState) != null) {
//                    System.out.println("making link from : " + currentState.getNameNumber() + " to " +
//                            states.get(lastState).getNameNumber() + " on " + edge);
//                    System.out.println("chilstate: " + childState.getNameNumber());
                    currentState.removeLink(childState, edge);
//                        System.out.println(lastState);
                    currentState.addLink(states.get(lastState), edge);
                    String hash = getHash(currentState);

                    for (String currentHash: registry.keySet()) {
                        if (registry.get(currentHash).getNameNumber() == currentState.getNameNumber()) {
                            registry.remove(currentHash);
                            registry.put(hash, currentState);
                            break;
                        }
                    }
                }
///////////////////////

            } else {
                currentIndex = commonPrefix.length();
            }
//            System.out.println("current index: " + currentIndex);
            boolean changed = true;
            while (changed) {
                currentIndex = currentIndex - 1;
                Character nextStep = null;
                if (currentIndex > 0) {
                    nextStep = getStateAtIndex(startState, currentIndex, -1);
                }
//                if (currentIndex == -1) {
//                    nextStep = getStateAtIndex(startState, 0, -1);
//                }
//                Character nextStep = getStateAtIndex(startState, currentIndex, -1);
//                System.out.println("next step: " + nextStep);
                oldState = states.get(lastState);
                if (currentIndex > 0) {
                    removeFromRegister(states.get(lastState));
                }
//                if (currentIndex == -1) {
//                    currentState = startState;
//                }
//                System.out.println("++++" + currentState.getNameNumber());

                if (nextStep != null) {
//                    System.out.println("IN HERE ONE LAST TIME");

//                    State childState = null;
//                    Character edge = null;
//                    for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
//                        int sameEdgesSize = edgeInfo.getEdgeChars().size();
//                        for (int j = 0; j < sameEdgesSize; j++) {
//                            if (edgeInfo.getEdgeChars().get(j).equals(nextStep)) {
//                                childState = edgeInfo.getEdgeToState();
//                                edge = edgeInfo.getEdgeChars().get(j);
//                            }
//                        }
//                    }
//
//
//                    if (states.get(lastState) != null) {
//                        System.out.println("making link from : " + currentState.getNameNumber() + " to " +
//                                states.get(lastState).getNameNumber() + " on " + edge);
//                        System.out.println("chilstate: " + childState.getNameNumber());
//                        currentState.removeLink(childState, edge);
////                        System.out.println(lastState);
//                        currentState.addLink(states.get(lastState), edge);
//                        String hash = getHash(currentState);
//
//                        for (String currentHash: registry.keySet()) {
//                            if (registry.get(currentHash).getNameNumber() == currentState.getNameNumber()) {
//                                registry.remove(currentHash);
//                                registry.put(hash, currentState);
//                                break;
//                            }
//                        }
//                    }
                    replaceOrRegister(currentState, nextStep);

                }
//                replaceOrRegister(currentState, nextStep);

                changed = (oldState.getNameNumber() != states.get(lastState).getNameNumber());
            }
//            System.out.println(currentIndex);
            if (!changed && (currentIndex > 0)) {
//                System.out.println("in here");
                String hash = getHash(currentState);
                String oldHash = null;
                for (String h: registry.keySet()) {
                    if (registry.get(h).getNameNumber() == currentState.getNameNumber()) {
                        oldHash = h;
//                        System.out.println("removing old hash: " + oldHash + " from registry with node: " + registry.get(h).getNameNumber());
                        registry.remove(h);
                        break;
                    }
                }
//                System.out.println("putting in registry hash: " + hash + " from registry with node: " + currentState.getNameNumber());
                registry.put(hash, currentState);
            }
            word = br.readLine();
        }
        debug("exit main");

        doDFS();
        System.out.println("Number of words in the input language: " + count);
        System.out.println("Number of nodes in the minimal automaton: " + countGlobal);
    }

    private static State clone(State stateToClone) {
        State lastStateState = stateToClone;
        State newState =  new State(newStateCount++, lastStateState.isAcceptState());
        for (EdgeInfo edgeInfo: lastStateState.getEdges().values()) {
            if (edgeInfo.getEdgeChars().size() == 1) {
                newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(0));
            } else {
                newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(0));
                for (int i = 1; i < edgeInfo.getEdgeChars().size(); i++) {
                    newState.addLink(edgeInfo.getEdgeToState(),edgeInfo.getEdgeChars().get(i));
                }
            }
        }
        newState.updatelastAdded(lastStateState.getLastLinkAdded(), lastStateState.getCharOfLastLinkAdded());
        states.put(newState.getNameNumber(), newState);
        return newState;
    }
}