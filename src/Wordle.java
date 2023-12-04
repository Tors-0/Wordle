import java.awt.*;
import java.util.Arrays;
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
        for (char c = 'a'; c <= 'z'; c++) {
            if (!randomWordMap.containsKey(c)) {
                randomWordMap.put(c, 0);
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
    static final Toolkit tk = Toolkit.getDefaultToolkit();
    /*
    Scanner to be used throughout the program
     */
    static Scanner scanny = new Scanner(System.in);

    public static void main(String[] args){
        boolean gameOver = false;
        boolean playing = false;
        System.out.printf("Welcome to %s%s! Type in a five letter word. You have six attempts to guess the correct word.%n", green("Wor"), yellow("dle"));
        System.out.println("Type in a five letter word. I will give you feedback on each guess...");
        String currentGuess = "";
        playing = true;
        while (playing) {
            for (int g = 1; g <= 6; g++) {
                currentGuess = scanny.nextLine();
                currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
                while (currentGuess.length() != 5) {
                    tk.beep();
                    System.out.println("Invalid guess, please try again:");
                    currentGuess = scanny.nextLine();
                    currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
                }
                System.out.println(checkString(currentGuess));
                if (currentGuess.equals(randomWord)) {
                    break;
                }
            }
            System.out.println("You WIN!\nWould you like to play again? (y/n): ");
            String selection = scanny.nextLine().toLowerCase();
            playing = !selection.isEmpty() && selection.charAt(0) == 'y';
        }
    }
    public static String checkString(String guess) {
        String[] formattedGuess = new String[5];
        Arrays.fill(formattedGuess,"");
        HashMap<Character,Integer> formattedGuessMap = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) { // populate the map for all lowercase chars (case irrelevant to current scope)
            formattedGuessMap.put(c, 0);
        }
        for (int i = 0; i < formattedGuess.length; i++) {
            formattedGuess[i] = String.valueOf(guess.charAt(i));
        }
        for (int i = 0; i < guess.length(); i++) { // create green characters in output
            if (guess.charAt(i) == randomWord.charAt(i)) {
                formattedGuess[i] = green(String.valueOf(guess.charAt(i)));
                formattedGuessMap.put(guess.charAt(i), formattedGuessMap.get(guess.charAt(i)) + 1);
            }
        }
        for (int i = 0; i < guess.length(); i++) { // create yellow characters in output
            if (randomWord.indexOf(guess.charAt(i)) != -1) {
                if (formattedGuessMap.get(guess.charAt(i)) < randomWordMap.get(guess.charAt(i))
                        && formattedGuess[i].length() == 1
                ) {
                    formattedGuess[i] = yellow(String.valueOf(guess.charAt(i)));
                    formattedGuessMap.put(guess.charAt(i), formattedGuessMap.get(guess.charAt(i)) + 1);
                }
            }
        }
        String formattedGuessFinal = "";
        for (String s : formattedGuess) {
            formattedGuessFinal += s;
        }
        return formattedGuessFinal;
    }
    public static String green(String text) {
        return GREEN + text + RESET;
    }
    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }
}
