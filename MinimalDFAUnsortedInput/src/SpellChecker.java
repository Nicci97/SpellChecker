import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SpellChecker {

    public static void main(String[] args) throws IOException {
        AutomatonBuilder automaton = new AutomatonBuilder();
        automaton.create(args[0]);

        //Uncomment this line to add this word to the Trie
//        automaton.addNewWord("NewWord");

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter the word you would like to spell check:");
        System.out.println("Enter 0 to exit:");

        String word = myObj.nextLine();  // Read user input
        while (!word.equals("0")) {
            System.out.println("-------");
            if (automaton.membership(word)) {
                System.out.println("The input word " + word + " is spelt correctly");
            } else {
                String[] corrections = automaton.getCorrection(word);
                if (corrections[0].equals("")) {
                    System.out.println("The input word " + word + " is spelt incorrectly, there are no suggestions available");
                } else if (corrections[1].equals("")) {
                    System.out.println("The input word " + word + " is spelt incorrectly, the following suggestion is available:");
                    System.out.println(corrections[0]);
                } else if (corrections[2].equals("")) {
                    System.out.println("The input word " + word + " is spelt incorrectly, the following suggestions are available:");
                    System.out.println(corrections[0]);
                    System.out.println(corrections[1]);
                } else {
                    System.out.println("The input word " + word + " is spelt incorrectly, the following suggestions are available:");
                    System.out.println(corrections[0]);
                    System.out.println(corrections[1]);
                    System.out.println(corrections[2]);
                }
            }
            System.out.println("-------");
            System.out.println("Enter the word you would like to spell check:");
            System.out.println("Enter 0 to exit:");
            word = myObj.nextLine();  // Read user input
        }
    }
}
