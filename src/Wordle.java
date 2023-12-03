import org.w3c.dom.Text;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Scanner;

public class Wordle {

    /*
    You will want to add at least 10-20 more words to WORDS, which is  called
    and Array.  It lets us store lots of information in one place.   In this
    case it lets us store all the words we want to use in our game

    randomWord is the word that the user is trying to guess in the game
     */
    static final String[] WORDS = {"clown", "pizza", "eagle", "shady"};
    static final String randomWord = WORDS[(int)(Math.random()*WORDS.length)];
    static final HashMap<Character,Integer> randomWordMap = new HashMap<>();
    static {
        for (int i = 0; i < randomWord.length(); i++) {
            char currentChar = randomWord.charAt(i);
            if (!randomWordMap.containsKey(currentChar)) {
                randomWordMap.put(currentChar,1);
            } else {
                randomWordMap.put(currentChar,randomWordMap.get(currentChar) + 1);
            }
        }
    }
    /*
    These variables are used to be able to change the color of the text
    In the official game, green means correct letter and position, yellow
    means correct letter but not position
     */
    static final String RESET = "\u001B[0m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";

    /*
    Scanner to be used throughout the program
     */
    static Scanner scanny = new Scanner(System.in);

    public static void main(String[] args){

    }
    public static void checkString(String guess) {
        HashMap<Character,Integer> guessMap = new HashMap<>();
        for (int i = 0; i < guess.length(); i++) {
            char currentChar = guess.charAt(i);
            if (!guessMap.containsKey(currentChar)) {
                guessMap.put(currentChar, 1);
            } else {
                guessMap.put(currentChar, guessMap.get(currentChar) + 1);
            }
        }
        String formattedGuess = guess;

    }
    public static String green(String text) {
        return GREEN + text + RESET;
    }
    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }
}
