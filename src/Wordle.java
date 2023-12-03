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
        exampleOfMakingTextColored();
    }

    public static void exampleOfMakingTextColored(){
        String text = "normal " + GREEN + "green" + RESET + " and " + YELLOW + "yellow" + RESET;
        System.out.println(text);
    }

}
