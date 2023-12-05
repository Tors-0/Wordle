import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
    static final ArrayList<String> WORDS = new ArrayList<>();
    static {
        try {
            File wordList = new File("src/dictionary.txt");
            Scanner wordListReader = new Scanner(wordList);
            // grab all words from the text file provided and put them into a list
            while (wordListReader.hasNextLine()) {
                WORDS.add(wordListReader.nextLine());
            }
        } catch (FileNotFoundException ignored) {
            try {
                File wordList = new File("dictionary.txt");
                Scanner wordListReader = new Scanner(wordList);
                // try again with a new location (needed if running from terminal)
                while (wordListReader.hasNextLine()) {
                    WORDS.add(wordListReader.nextLine());
                }
            } catch (FileNotFoundException ignored1) {}
        }
    }
    static String randomWord = "";
    static HashMap<Character,Integer> randomWordMap = new HashMap<>();

    /**
     * resets the randomWord String...<br>handy for restarting the game
     */
    static void regenRandomWord() {
        int randomLength = (int)(Math.pow(Math.random(),2) * 14) + 2;
        while (randomWord.length() != randomLength) {
            randomWord = WORDS.get((int) (Math.random() * WORDS.size()));
        }
        for (char c = 'a'; c <= 'z'; c++) { // fill the randomWord's hashmap with all alphabetical characters
            randomWordMap.put(c, 0);
        }
        for (int i = 0; i < randomWord.length(); i++) { // counts the actual letters of randomWord into the hashmap
            char currentChar = randomWord.charAt(i);
            randomWordMap.put(currentChar,randomWordMap.get(currentChar) + 1);
        }
    }
    /*
    These variables are used to be able to change the color of the text
    In the official game, green means correct letter and position, yellow
    means correct letter but not position
     */
    static final String RESET = "\u001B[0m";
    static final String GREEN = "\u001B[102;30m";
    static final String YELLOW = "\u001B[103;30m";

    // The following two colors are used for high contrast mode to improve readability
    static final String BG_BLUE = "\u001B[46;30m";
    static final String BG_RED = "\u001B[41;97m";
    /*
    Scanner to be used throughout the program
     */
    static Scanner scanny = new Scanner(System.in);
    static boolean gameOver = false;
    static boolean playing = false;
    static boolean highContrast = false;
    static String currentGuess = "";
    /**
     * Runs this file as intended!
     * @param args ignored
     */
    public static void main(String[] args){
        System.out.printf("Welcome to %s-%s ! Type in a word. You have a few attempts to guess the correct word.%n",
                green("Scrabble"), yellow("dle"));
        System.out.print("Would you like to enable high contrast? (y/n): ");
        String userChoiceHCMode = scanny.nextLine().toLowerCase();
        highContrast = !userChoiceHCMode.isEmpty() && userChoiceHCMode.charAt(0) == 'y';
        playing = true;
        while (playing) { // keep playing the game until the user indicates they no longer wish to play
            gameOver = false;
            gameLoop();
            System.out.println("Would you like to play again? (y/n): ");
            String selection = scanny.nextLine().toLowerCase();
            playing = !selection.isEmpty() && selection.charAt(0) == 'y';
        }
    }
    public static void gameLoop() {
        regenRandomWord();
        System.out.printf("%nType in a %s letter word. You get %s guesses. I will give you feedback on each guess...%n", randomWord.length(), randomWord.length()+1);
        System.out.printf("Letters highlighted in %s are in the correct position, %n" +
                        "and letters highlighted in %s are in the word, but in the wrong position.%n",
                green(highContrast ? "red" : "green"), yellow(highContrast ? "blue" : "yellow"));
        for (int g = 1; g <= randomWord.length()+1 && !gameOver; g++) { // repeats 6 times for each of the users 6 guesses
            System.out.printf("Turn %s: ", g);
            currentGuess = scanny.nextLine();
            cheatCodes();
            currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
            while (currentGuess.length() != randomWord.length() || !WORDS.contains(currentGuess)) { // input validation
                System.out.printf("Turn %s: ", g);
                currentGuess = scanny.nextLine();
                cheatCodes();
                currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
            }
            System.out.printf("\t%s%n", checkString(currentGuess));
            if (currentGuess.equals(randomWord)) {
                break;
            }
        }
        if (currentGuess.equals(randomWord)) {
            System.out.println("You WIN!");
        } else {
            System.out.printf("You lost =( The word was %s%n", randomWord);
        }
    }
    public static void cheatCodes() {
        if (currentGuess.contains("!g")) {
            System.out.printf("Okay you cheater, the word is %s...\r", randomWord);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            System.out.print("                                                     \r");
        } else if (currentGuess.contains("!skip")) {
            gameOver = true;
        }
    }
    /**
     * formats the inputted string into proper wordle colors
     * @param guess must be String with length == randomWord.length()
     * @return formatted version of guess with yellow and green characters according to the value of randomWord
     */
    public static String checkString(String guess) {
        String[] formattedGuess = new String[guess.length()];
        Arrays.fill(formattedGuess,"");
        HashMap<Character,Integer> formattedGuessMap = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) { // populate the map for all alphabetical characters
            formattedGuessMap.put(c, 0);
        }
        // populate the output array so that non-colored characters aren't blank
        for (int i = 0; i < formattedGuess.length; i++) {
            formattedGuess[i] = " " + String.valueOf(guess.charAt(i)) + " ";
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
                        && formattedGuess[i].length() == 3
                ) {
                    formattedGuess[i] = yellow(String.valueOf(guess.charAt(i)));
                    formattedGuessMap.put(guess.charAt(i), formattedGuessMap.get(guess.charAt(i)) + 1);
                }
            }
        }
        String formattedGuessFinal = "";
        for (String s : formattedGuess) { // assembles each String into the final output
            formattedGuessFinal += s;
        }
        return formattedGuessFinal;
    }

    /**
     * Formats input String using ANSI codes
     * @param text String to be colored
     * @return String but green
     */
    public static String green(String text) {
        text = " " + text + " ";
        return highContrast ? BG_RED + text + RESET : GREEN + text + RESET;
    }

    /**
     * Formats input String using ANSI codes
     * @param text String to be colored
     * @return String but yellow
     */
    public static String yellow(String text) {
        text = " " + text + " ";
        return highContrast? BG_BLUE + text + RESET : YELLOW + text + RESET;
    }
}
