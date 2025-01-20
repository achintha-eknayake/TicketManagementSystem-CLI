package org.tms;

import org.tms.threadExecutor.Executor;
import org.tms.configuration.ConfigUtility;

/**
 * Main class to execute the Producer-Consumer Ticket Handling Simulation.
 * Provides options to use an existing configuration or create a new one.
 */
public class Main {
    public static void main(String[] args) {
        // Decorative separator for console output
        String starLine = "***************************************************************";
        System.out.println(starLine);
        System.out.println("Welcome to Simulation Producer-Consumer Ticket Handling Service");
        System.out.println(starLine);

        int option;
        do {
            // Prompt user for input
            option = ConfigUtility.integerInput(
                    """
                    Enter 1 if you want to run using the previously saved configuration file:
    
                    Enter 2 if you want to create a new configuration file:
    
                    Enter 0 to exit:
    
                    Enter your option:"""
            );

            // Validate user input
            if (option < 0 || option > 2) {
                System.out.println("Invalid option. Please try again.");
                continue;
            }

            // Process the user's choice
            switch (option) {
                case 1:
                    // Run the ticket handling simulation using the saved configuration
                    Executor.runVendorCustomerCLI();
                    break;

                case 2:
                    // Create a new configuration file
                    System.out.println("Creating new configuration file.");
                    ConfigUtility.saveConfigFile();
                    break;

                case 0:
                    // Exit the application
                    System.out.println("Exiting...");
                    break;

                default:
                    // Handle unexpected cases (shouldn't occur due to validation)
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

        } while (option != 0); // Loop until the user chooses to exit

        // Farewell message
        System.out.println("Thank you for using the Ticket Handling Simulation!");
    }
}
