package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");
        logger.log(Level.INFO,"App.java Program Initialised");

        String Prompt = "Enter a 4 letter word for a guess or q to quit: ";

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO,"Wordle created and connected.");
        } else {
            logger.log(Level.WARNING,"Not able to connect. Sorry!");
            //System.out.println("Something went wrong.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Wordle structures in place.");
            //System.out.println("Something went wrong.");
        } else {
            logger.log(Level.WARNING,"Not able to launch. Sorry!");
            //System.out.println("Something went wrong.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if(line.matches("^[a-z]{4}$")){
                    logger.log(Level.INFO,"Word added to list: {0}", line);
                    wordleDatabaseConnection.addValidWord(i, line);
                }else {
                    logger.log(Level.SEVERE,"Attempted to add invalid word to wordbank: {0}", line);                    
                }
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING,"Error reading user input.", e);
            System.out.println("Something went wrong.");
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
        
            while (true) {
                System.out.print(Prompt);
                guess = scanner.nextLine().trim().toLowerCase();
                if (guess.equals("q")) {
                    logger.log(Level.INFO,"User quit program");
                    break; // Exit if user types 'q'
                }
                System.out.println("You've guessed '" + guess + "'.");
                if (!guess.matches("^[a-z]{4}$")) {
                    System.out.println("You have used invalid characters or the word is not the correct length!\n");
                    logger.log(Level.WARNING,"User inputted guess with an invalid length or character/s {0}", guess);
                    continue; // Skip checking the word list and prompt again
                }
                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                    logger.log(Level.WARNING,"User inputted invalid guess: {0}", guess);

                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "User input issue.", e);
            System.out.println("Something went wrong.");

        }

    }
}