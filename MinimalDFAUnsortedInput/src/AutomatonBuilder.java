/*
 * Authored by Nicole du Toit.
 * ASCII value comparison function found at https://stackoverflow.com/questions/26553889/comparing-2-strings-by-ascii-values-in-java
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AutomatonBuilder {

    private static HashMap<String, State> registry = new HashMap<>();
    private static String word = null;
    private static State startState = null;
    private static State lastState = startState;
    private static int newStateCount = 1;
    private static int countGlobal = 0;
    private static State firstState = null;
    private static int currentIndex = 0;
    private static State currentState = null;

    public static void debug(String str) {
//		System.out.println(str);
    }

    public static void debug2(String str) {
        System.out.println(str);
    }

    public static ArrayList<String> doDFS() {
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> results2;

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
        return results;
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
        char nextLetter;
        if (wordIndex != wordLength) {
            nextLetter = word.charAt(wordIndex);
            for (EdgeInfo edgeInfo : nextState.getEdges().values()) {
                int numChars = edgeInfo.getEdgeChars().size();
                for (int k = 0; k < numChars; k++) {
                    if (edgeInfo.getEdgeChars().get(k).equals(nextLetter)) {
                        if (wordLength == wordIndex+1) {
                            commonPrefixSegment = nextLetter + "";
                            lastState = edgeInfo.getEdgeToState();
                            foundNewState = true;
                            break;
                        } else {

                        commonPrefixSegment = nextLetter + nextStep(edgeInfo.getEdgeToState(), wordIndex + 1, wordLength);
                        foundNewState = true;
                        break;
                        }
                    }
                }
            }
        }
            if (!foundNewState) {
                lastState = nextState;
                if (wordIndex != wordLength) {
                    String suffexToAdd = word.substring(wordIndex + 1, word.length());
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
            char firstLetter = word.charAt(0);

            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                if (edgeInfo.getEdgeChars().get(k).equals(firstLetter)) {
                    if (wordLength == 1) {
                        commonPrefix = firstLetter + "";
                        lastState = edgeInfo.getEdgeToState();
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
            lastState = startState;
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
        State childState = null;
        Character edge = null;
        for (EdgeInfo edgeInfo: state.getEdges().values()) {
            int sameEdgesSize = edgeInfo.getEdgeChars().size();
            for (int j = 0; j < sameEdgesSize; j++) {
                if (edgeInfo.getEdgeChars().get(j).equals(nextStep)) {
                    childState = edgeInfo.getEdgeToState();
                    edge = edgeInfo.getEdgeChars().get(j);
                }
            }
        }
        if (childState != null) {
            String hash = getHash(childState);
            if (registry.get(hash) != null) {

                State mergeState = registry.get(hash);
                boolean createdLoop = false;
                for (EdgeInfo edgeInfo: mergeState.getEdges().values()) {
                    State pointingToState = edgeInfo.getEdgeToState();
                    if (pointingToState.getNameNumber() == state.getNameNumber()) {
                        createdLoop = true;
                    }
                }
                if ((mergeState.getNameNumber() != state.getNameNumber()) && !createdLoop) {
                    state.removeLink(childState, edge);
                    state.addLink(mergeState, edge);
                }


                for (String currentHash: registry.keySet()) {
                    if (registry.get(currentHash).getNameNumber() == state.getNameNumber()) {
                        registry.remove(currentHash);

                        hash = getHash(state);
                        registry.put(hash, state);
                                break;
                    }
                }
            } else {
                hash = getHash(childState);
                for (String h: registry.keySet()) {
                    if (registry.get(h).getNameNumber() == childState.getNameNumber()) {
                        registry.remove(h);
                        break;
                    }
                }
                registry.put(hash, childState);
            }
        }
        debug("exit replaceOrRegister");
    }

    public static void addSuffixNextStep(State s, int index, int suffixlength, String currentSuffix) {
        debug("enter addSuffixNextStep");
        State newState;
        if (index == (suffixlength-1)) {
            newState = new State(newStateCount++, true);
            s.addLink(newState, currentSuffix.charAt(index));
            debug("exit addSuffixNextStep");
        } else {
            newState = new State(newStateCount++, false);
            s.addLink(newState, currentSuffix.charAt(index));
            addSuffixNextStep(newState, index+1, suffixlength, currentSuffix);
        }
        replaceOrRegister(s, currentSuffix.charAt(index));
        debug("exit addSuffixNextStep");
    }

    public static void addSuffix(String currentSuffix) {
        debug("enter addSuffix");
        if (currentSuffix.equals("")) {
            lastState.makeFinal();
        }
        int suffixLength = currentSuffix.length();
        if (suffixLength != 0) {
            State newState;
            if (suffixLength == 1) {
                newState = new State(newStateCount++, true);
                State lastStateState = lastState;
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                removeFromRegister(lastStateState);

                replaceOrRegister(lastStateState, currentSuffix.charAt(0));
            } else {
                newState = new State(newStateCount++, false);
                State lastStateState = lastState;
                lastStateState.addLink(newState, currentSuffix.charAt(0));
                removeFromRegister(lastStateState);

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

    public static State magicLoopNextStep(State state, String commonPrefix, int index, int upperIndex) {
        index++;
        if (upperIndex >= index) {
            for (EdgeInfo edgeInfo: state.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(commonPrefix.charAt(index))) {
                        State newStatePointer = magicLoopNextStep(edgeInfo.getEdgeToState(), commonPrefix, index, upperIndex);
                        currentState = newStatePointer;
                        break;
                    }
                }
            }
            if (index >= currentIndex) {
                currentState = clone(state);
                Character transitionToReplace = commonPrefix.charAt(index);
                State toRemoveEdgeInfo = null;
                for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
                    int numberOfChars = edgeInfo.getEdgeChars().size();
                    for (int i = 0; i < numberOfChars; i++) {
                        if (edgeInfo.getEdgeChars().get(i).equals(transitionToReplace)) {
                            toRemoveEdgeInfo = edgeInfo.getEdgeToState();
                            break;
                        }
                    }
                }
                currentState.removeLink(toRemoveEdgeInfo, transitionToReplace);
                currentState.addLink(lastState, transitionToReplace);
                replaceOrRegister(currentState, commonPrefix.charAt(index));
                lastState = currentState;
            }
        }
        return currentState;
    }

    public static void magicLoop(String commonPrefix) {
        int upperIndex = commonPrefix.length()-1;
        int index = 0;
        if (upperIndex >= index) {
            for (EdgeInfo edgeInfo: startState.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(commonPrefix.charAt(index))) {
                        State newStatePointer = magicLoopNextStep(edgeInfo.getEdgeToState(), commonPrefix, index, upperIndex);
                        currentState = newStatePointer;
                        break;
                    }
                }
            }
            if (index >= currentIndex) {
                currentState = clone(startState);
                Character transitionToReplace = commonPrefix.charAt(index);
                for (EdgeInfo edgeInfo: startState.getEdges().values()) {
                    if (edgeInfo.getEdgeChars().contains(transitionToReplace)) {
                        currentState.removeLink(edgeInfo.getEdgeToState(), transitionToReplace);
                        break;
                    }
                }
                currentState.addLink(lastState, transitionToReplace);
                replaceOrRegister(currentState, commonPrefix.charAt(index));
                lastState = currentState;
            }
        }
    }

    private static Character getStateAtIndex(State state, int currentIndex, int index) {
        index++;
        if (currentIndex == index) {
            currentState = state;
            return word.charAt(index);
        } else {
            for (EdgeInfo edgeInfo: state.getEdges().values()) {
                int numberOfChars = edgeInfo.getEdgeChars().size();
                for (int i = 0; i < numberOfChars; i++) {
                    if (edgeInfo.getEdgeChars().get(i).equals(word.charAt(index))) {
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
        String hashToRemove = null;
        for (String h: registry.keySet()) {
            if (registry.get(h).getNameNumber() == state.getNameNumber()) {
                hashToRemove = h;
            }
        }
        registry.remove(hashToRemove);

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
        return newState;
    }

    public void create(String filePath) throws IOException {
        long start = System.currentTimeMillis();

        String commonPrefix;
        String currentSuffix;
        startState = new State(0, true);

        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(targetStream));
        word = br.readLine();
        int count = 0;
        while (word != null) {
            lastState = startState;
            firstState = null;
            count++;
            commonPrefix = findCommonPrefix();
            currentSuffix = word.substring(commonPrefix.length());
            if (currentSuffix.equals("") && lastState.isAcceptState()) {
                word = br.readLine();
                continue;
            }
            firstState(commonPrefix);
            if (firstState != null) {
                lastState = clone(lastState);
            }

            addSuffix(currentSuffix);
            State oldState = null;
            if (firstState != null) {
                firstState(commonPrefix);
                magicLoop(commonPrefix);

                Character nextStep = null;
                if (currentIndex-1 > 0) {
                    nextStep = getStateAtIndex(startState, currentIndex-1, -1);
                } else {
                    nextStep = word.charAt(0);
                    currentState = startState;
                }
                State childState = null;
                Character edge = null;
                for (EdgeInfo edgeInfo: currentState.getEdges().values()) {
                    int sameEdgesSize = edgeInfo.getEdgeChars().size();
                    for (int j = 0; j < sameEdgesSize; j++) {
                        if (edgeInfo.getEdgeChars().get(j).equals(nextStep)) {
                            childState = edgeInfo.getEdgeToState();
                            edge = edgeInfo.getEdgeChars().get(j);
                        }
                    }
                }

                if (lastState != null) {
                    currentState.removeLink(childState, edge);
                    currentState.addLink(lastState, edge);
                    String hash = getHash(currentState);
                    for (String currentHash: registry.keySet()) {
                        if (registry.get(currentHash).getNameNumber() == currentState.getNameNumber()) {
                            registry.remove(currentHash);
                            registry.put(hash, currentState);
                            break;
                        }
                    }
                }
            } else {
                currentIndex = commonPrefix.length();
            }
            boolean changed = true;
            while (changed) {
                currentIndex = currentIndex - 1;
                Character nextStep = null;
                if (currentIndex > 0) {
                    nextStep = getStateAtIndex(startState, currentIndex, -1);
                }
                oldState = lastState;
                if (currentIndex > 0) {
                    removeFromRegister(lastState);
                }

                if (nextStep != null) {
                    replaceOrRegister(currentState, nextStep);

                }
                if (oldState == null || lastState == null) {
                    changed = false;
                } else {
                    changed = (oldState.getNameNumber() != lastState.getNameNumber());
                }

            }
            if (!changed && (currentIndex > 0)) {
                String hash = getHash(currentState);
                for (String h: registry.keySet()) {
                    if (registry.get(h).getNameNumber() == currentState.getNameNumber()) {
                        registry.remove(h);
                        break;
                    }
                }

                registry.put(hash, currentState);
            }
            word = br.readLine();
        }

        // ...
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;


        doDFS();

        System.out.println("Number of words in the input language: " + count);
        System.out.println("Number of nodes in the minimal automaton: " + countGlobal);
        System.out.println("The program took " + timeElapsed/100 + " seconds to run");
    }

    public boolean membership(String word) {
        int index = 0;
        boolean found = false;
        if (index == word.length()) {
            return true;
        }
        for (EdgeInfo edgeInfo: startState.getEdges().values()) {
            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                if (edgeInfo.getEdgeChars().get(k).equals(word.charAt(index))) {
                    State nextState = edgeInfo.getEdgeToState();
                    found = membershipNextStep(nextState, word, index+1);
                    break;
                }
            }
        }
        return found;
    }

    public boolean membershipNextStep(State state, String word, int index) {
        boolean found = false;
        if (index == word.length()) {
            return true;
        }
        for (EdgeInfo edgeInfo: state.getEdges().values()) {
            int numChars = edgeInfo.getEdgeChars().size();
            for (int k = 0; k < numChars; k++) {
                if (edgeInfo.getEdgeChars().get(k).equals(word.charAt(index))) {
                    State nextState = edgeInfo.getEdgeToState();
                    found = membershipNextStep(nextState, word, index+1);
                    break;
                }
            }
        }
        return found;
    }

    public void addNewWord(String w) {
    }

    public String getCorrection(String word) {
        ArrayList<String> results;
        results = doDFS();
        String newCorrection = "";
        int bestDistance = getLevenshteinDistance(word, results.get(0));;
        int size = results.size();
        for (int i = 1; i < size; i++) {
            int levenshteinDistance = getLevenshteinDistance(word, results.get(i));
            if (levenshteinDistance < bestDistance) {
                bestDistance = levenshteinDistance;
                newCorrection = results.get(i);
            }
        }
        System.out.println("levenstein distance to return with " + newCorrection + " " + bestDistance);
        return newCorrection;
    }

    /* derived from https://www.baeldung.com/java-levenshtein-distance */
    public int getLevenshteinDistance(String wrongWord, String actualWord) {
        int[][] dp = new int[wrongWord.length() + 1][actualWord.length() + 1];

        for (int i = 0; i <= wrongWord.length(); i++) {
            for (int j = 0; j <= actualWord.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]  + costOfSubstitution(wrongWord.charAt(i - 1), actualWord.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[wrongWord.length()][actualWord.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}