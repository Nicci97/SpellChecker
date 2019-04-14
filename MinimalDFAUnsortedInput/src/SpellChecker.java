import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SpellChecker {

    public static void main(String[] args) throws IOException {

        AutomatonBuilder automaton = new AutomatonBuilder();
        automaton.create(args[0]);
//        System.out.println("membership a: " + automaton.membership("a"));
//        System.out.println("membership assent: " + automaton.membership("assent"));
//        System.out.println("membership ons: " + automaton.membership("ons"));
//        System.out.println("membership onss: " + automaton.membership("onss"));
//        System.out.println("membership e: " + automaton.membership("e"));
//        automaton.addNewWord("onss");
//        System.out.println("membership a: " + automaton.membership("a"));
//        System.out.println("membership assent: " + automaton.membership("assent"));
//        System.out.println("membership ons: " + automaton.membership("ons"));
//        System.out.println("membership onss: " + automaton.membership("onss"));
//        System.out.println("membership e: " + automaton.membership("e"));


//        File f1=new File(args[1]); //Creation of File Descriptor for input file
//        String[] words=null;  //Intialize the word Array
//        FileReader fr = new FileReader(f1);  //Creation of File Reader object
//        BufferedReader br = new BufferedReader(fr); //Creation of BufferedReader object
//        String s;
//        String input="Java";   // Input word to be searched
//        int count=0;   //Intialize the word to zero
//        while((s=br.readLine())!=null)   //Reading Content from the file
//        {
//            words=s.split(" ");  //Split the word using space
//            for (String word : words)
//            {
//                if (automaton.membership(word)) {
//                    System.out.println("Contains " + word);
//                } else {
//                    String correction = automaton.getCorrection(word);
//                    System.out.println("Does not contain " + word + " correction is " + correction);
//                }
//            }
//        }
//
//        fr.close();
    }
}
