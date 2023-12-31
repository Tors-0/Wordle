import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
        int randomLength = (int)(Math.random() * Math.random() * 14) + 2;
        randomWord = "";
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
    // scrabble letter scores for scoring each word
    static int[] scrabblePoints = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
    public static int scrabblePoints(String text) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            total += scrabblePoints[text.charAt(i) - 97];
        }
        return total;
    }
    // score-tracking
    static int highScore = 0;
    static String highScoreName;
    static int currentScore = 0;
    static String currentPlayerName;
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
        gameStats();
        System.out.print("Would you like to enable high contrast? (y/n): ");
        String userChoiceHCMode = scanny.nextLine().toLowerCase();
        highContrast = !userChoiceHCMode.isEmpty() && userChoiceHCMode.charAt(0) == 'y';
        playing = true;
        while (playing) { // keep playing the game until the user indicates they no longer wish to play
            gameOver = false;
            gameLoop();
            if (gameOver) {
                saveHighScore();
                currentScore = 0;
            }
            System.out.print("Would you like to play again? (y/n): ");
            String selection = scanny.nextLine().toLowerCase();
            playing = !selection.isEmpty() && selection.charAt(0) == 'y';
        }
        saveHighScore();
    }
    private static void saveHighScore() {
        if (currentScore > highScore) {
            System.out.printf("%nYou have obtained a high score of %s, please enter your name: ", currentScore);
            currentPlayerName = scanny.nextLine();
            saveToFile(currentPlayerName + "|" + currentScore);
        }
    }
    public static void gameLoop() {
        regenRandomWord();
        int guesses = Math.max(11 - randomWord.length(), 5);
        System.out.printf("%nType in a %s letter word. You get %s guesses. I will give you feedback on each guess...%n", randomWord.length(), guesses);
        System.out.printf("Letters highlighted in %s are in the correct position, %n" +
                        "and letters highlighted in %s are in the word, but in the wrong position.%n",
                green(highContrast ? "red" : "green"), yellow(highContrast ? "blue" : "yellow"));
        for (int g = 1; !gameOver && g <= guesses; g++) { // repeats g times for each of the user's guesses
            System.out.printf("Turn %s: ", g);
            currentGuess = scanny.nextLine();
            currentGuess = cheatCodes();
            currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
            while (currentGuess.length() != randomWord.length() || !WORDS.contains(currentGuess)) { // input validation
                System.out.printf("Turn %s: ", g);
                currentGuess = scanny.nextLine();
                currentGuess = cheatCodes();
                currentGuess = currentGuess.toLowerCase().replaceAll("[^a-z]", "");
            }
            System.out.printf("\t%s%n", checkString(currentGuess));
            if (currentGuess.equals(randomWord)) {
                break;
            }
        }
        if (currentGuess.equals(randomWord) && !gameOver) {
            currentScore += scrabblePoints(randomWord);
            System.out.printf("You WIN!\tYou gain %s points, for a total of %s points.%n", scrabblePoints(randomWord), currentScore);
        } else {
            System.out.printf("You lost =( The word was %s%n", randomWord);
        }
    }
    public static String cheatCodes() {
        if (currentGuess.contains("!g")) {
            System.out.printf("Okay you cheater, the word is %s...\r", randomWord);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            System.out.print("                                                     \r");
            return "";
        } else if (currentGuess.contains("!skip")) {
            gameOver = true;
            return randomWord;
        }
        return currentGuess;
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
    // methods for high-score saving and managing
    static File highScoreFile = new File("highscore.txt");
    private static void saveToFile(String data) {
        try {
            FileWriter myWriter = new FileWriter(highScoreFile,true);
            String text = "\n";
            if (highScoreFile.createNewFile()) {
                System.out.println("File created: " + highScoreFile.getName());
                myWriter.append("// This file is for Statistical tracking and debugging purposes, please do not " +
                        "edit, delete, or move this file //");
            } else {
                System.out.println("Save file already exists.");
            }
            myWriter.append(text);
            myWriter.append(data);
            System.out.println("Successfully saved game data.\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
    public static void gameStats() {
        String temp;
        String name = "";
        int score = 0;
        try {
            Scanner files = new Scanner(highScoreFile);
            while (files.hasNextLine()) {
                temp = files.nextLine();
                if (temp.contains("|")) {
                    name = temp.substring(0, temp.indexOf('|'));
                    score = Integer.parseInt(temp.substring(temp.indexOf('|') + 1));
                }
                if (score > highScore) {
                    highScore = score;
                    highScoreName = name;
                }
            }
            System.out.printf("The high score is currently %s, and is held by %s.%n%n", highScore, highScoreName);
        } catch (FileNotFoundException ignored) {
            System.out.printf("There is no current high score holder! You could be the first!%n%n");
        }
    }
}
