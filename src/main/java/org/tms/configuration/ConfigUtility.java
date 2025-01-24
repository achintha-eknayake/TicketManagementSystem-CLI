package org.tms.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tms.exception.NegativeValueException;

import java.io.*;
import java.util.Scanner;

/**
 * Utility class for managing the configuration of the ticketing system.
 * This includes methods for saving, loading, and validating configurations.
 */
public class ConfigUtility {

    // Path to the configuration file
    private static final String CONFIG_FILE = "src/main/resources/ConfigurationData.json";

    /**
     * Prompts the user for an integer input, validates it, and ensures it is non-negative.
     *
     * @param sentence the message to prompt the user
     * @return a valid, non-negative integer
     */
    public static int integerInput(String sentence) {
        Scanner scanner = new Scanner(System.in);
        int value = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(sentence); // Display the prompt
            String input = scanner.nextLine(); // Read user input

            try {
                value = Integer.parseInt(input.trim());
                if (value < 0) {
                    throw new NegativeValueException("Negative values are not allowed!");
                }
                valid = true; // If input as valid if successful exits the loop
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer!"); // Handle non-integer input
            } catch (NegativeValueException e) {
                System.out.println(e.getMessage()); // Handle negative values
            }
        }
        return value;
    }

    /**
     * Saves user input of the configuration to a JSON file.
     * Ensures ticket capacity is greater than or equal to the total number of tickets.
     * Displays the saved configuration file
     */
    public static void saveConfigFile() {
        int ticketCapacity;
        int totalTickets;

        // Ensure capacity is higher than total tickets
        do {
            ticketCapacity = integerInput("Ticket capacity: ");
            totalTickets = integerInput("Total tickets: ");
            if (totalTickets > ticketCapacity) {
                System.out.println("Ticket limit exceeds capacity! Please re-enter valid capacity/total tickets.");
            }
        } while (totalTickets > ticketCapacity);

        int ticketReleaseRate = integerInput("Maximum Ticket release rate: ");
        int ticketRetrievalRate = integerInput("Maximum Ticket retrieval rate: ");

        Configuration configuration = new Configuration(totalTickets, ticketReleaseRate, ticketRetrievalRate, ticketCapacity);

        // Create a JSON object from the configuration
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("TotalTickets", configuration.getTotalTickets());
        jsonObject.addProperty("TicketReleaseRate", configuration.getMaximumTicketReleaseRate());
        jsonObject.addProperty("CustomerRetrievalRate", configuration.getMaximumCustomerRetrievalRate());
        jsonObject.addProperty("MaxTicketCapacity", configuration.getMaxTicketCapacity());

        Gson gson = new Gson();

        try (FileWriter file = new FileWriter(CONFIG_FILE)) {
            gson.toJson(jsonObject, file); // Save the JSON object to a file
            System.out.println("Configuration saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * Loads the configuration from a JSON file.</br>
     * If the file does not exist or is invalid, prompts the user to create a new configuration.
     *
     * @return the loaded or newly created {@link Configuration}
     */
    public static Configuration loadConfigFile() {
        Configuration configuration = null;

        // Check if configuration file exists
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                long totalTickets = jsonObject.get("TotalTickets").getAsLong();
                long ticketReleaseRate = jsonObject.get("TicketReleaseRate").getAsLong();
                long customerRetrievalRate = jsonObject.get("CustomerRetrievalRate").getAsLong();
                long maxTicketCapacity = jsonObject.get("MaxTicketCapacity").getAsLong();

                // Create a Configuration object from JSON data
                configuration = new Configuration(
                        (int) totalTickets,
                        (int) ticketReleaseRate,
                        (int) customerRetrievalRate,
                        (int) maxTicketCapacity
                );

                // Display configration file
                System.out.println("Loaded Configuration:");
                System.out.println("Total Tickets: " + totalTickets);
                System.out.println("Ticket Release Rate: " + ticketReleaseRate);
                System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
                System.out.println("Max Ticket Capacity: " + maxTicketCapacity);
            } catch (IOException e) {
                System.err.println("Error reading the configuration file: " + e.getMessage());
            }
        } else {
            System.out.println("Configuration file not found. Creating a new configuration.");
            saveConfigFile();
            configuration = loadConfigFile(); // Reload after creating a new file
        }

        return configuration;
    }

    /**
     * Retrieves the configuration by loading it from the file.
     */
    public static Configuration getConfiguration() {
        Configuration configuration = null;

        // Check if configuration file exists
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                long totalTickets = jsonObject.get("TotalTickets").getAsLong();
                long ticketReleaseRate = jsonObject.get("TicketReleaseRate").getAsLong();
                long customerRetrievalRate = jsonObject.get("CustomerRetrievalRate").getAsLong();
                long maxTicketCapacity = jsonObject.get("MaxTicketCapacity").getAsLong();

                // Create a Configuration object from JSON data
                configuration = new Configuration(
                        (int) totalTickets,
                        (int) ticketReleaseRate,
                        (int) customerRetrievalRate,
                        (int) maxTicketCapacity
                );
            } catch (IOException e) {
                System.err.println("Error loading configuration: " );
            }
        }
        return configuration;
    }
}
