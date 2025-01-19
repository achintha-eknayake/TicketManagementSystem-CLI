package org.tms.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tms.exception.NegativeValueException;

import java.io.*;
import java.util.Scanner;

public class ConfigUtility {

    private static final String CONFIG_FILE = "src/main/resources/ConfigurationData.json";

    public static int integerInput(String sentence) {
        Scanner scanner = new Scanner(System.in);
        int value = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(sentence); // Display the prompt
            String input = scanner.nextLine(); // Attempt to read an integer

            try {
                value = Integer.parseInt(input.trim());
                if (value < 0) {
                    throw new NegativeValueException("Negative values are not allowed!");
                }
                valid = true; // If successful, mark input as valid
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer! ");
            } catch (NegativeValueException e) {
                System.out.println(e.getMessage()); // Number isn't allowed to be negative
            }
        }
        return value; // Return the valid integer value
    }



    public static void saveConfigFile() {

        int ticketCapacity;
        int totalTickets;
        // Checks the condition that capacity should be higher than total tickets
        do{
            ticketCapacity = integerInput("Ticket capacity: ");
            totalTickets = integerInput("Total tickets: ");
            if (totalTickets > ticketCapacity) {
                System.out.println("Ticket limit exceeds capacity! Please re-enter valid capacity/total tickets.");
            }
        }while (totalTickets > ticketCapacity);

        int ticketReleaseRate = integerInput("Ticket release rate: ");
        int ticketRetrievalRate = integerInput("Ticket retrieval rate: ");

        Configuration configuration = new Configuration(totalTickets, ticketReleaseRate, ticketRetrievalRate, ticketCapacity);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("TotalTickets", configuration.getTotalTickets());
        jsonObject.addProperty("TicketReleaseRate", configuration.getTicketReleaseRate());
        jsonObject.addProperty("CustomerRetrievalRate", configuration.getCustomerRetrievalRate());
        jsonObject.addProperty("MaxTicketCapacity", configuration.getMaxTicketCapacity());

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObject);

        try (FileWriter file = new FileWriter(CONFIG_FILE)) {
            gson.toJson(jsonString, file);
            System.out.println("Configuration saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    public static Configuration loadConfigFile() {

        Configuration configuration = null;

        // Check if configuration file is available
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try(FileReader reader = new FileReader(configFile)){

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                long totalTickets = jsonObject.get("TotalTickets").getAsLong();
                long ticketReleaseRate = jsonObject.get("TicketReleaseRate").getAsLong();
                long customerRetrievalRate = jsonObject.get("CustomerRetrievalRate").getAsLong();
                long maxTicketCapacity = jsonObject.get("MaxTicketCapacity").getAsLong();

                configuration = new Configuration(
                        (int) totalTickets,
                        (int) ticketReleaseRate,
                        (int) customerRetrievalRate,
                        (int) maxTicketCapacity
                );

                System.out.println("Loaded Configuration:");
                System.out.println("Total Tickets: " + totalTickets);
                System.out.println("Ticket Release Rate: " + ticketReleaseRate);
                System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
                System.out.println("Max Ticket Capacity: " + maxTicketCapacity);


            } catch (FileNotFoundException e) {
                System.err.println("Configuration file not found: " + e.getMessage());
                System.out.println("Creating a new file.");
                saveConfigFile();
                configuration = loadConfigFile(); // reload after creating a new file
            } catch (IOException e) {
                System.err.println("Error reading the configuration file: " + e.getMessage());
            }
            System.out.println("Configuration loaded successfully!");
        }


        // load configurations


        return configuration;
    }

    public static Configuration ConfigurationParameterSetup(String sentence){

        Configuration configuration = null;


        return loadConfigFile();
    }


    public static void main(String[] args) {

        //loadConfigFile();
        saveConfigFile();

    }
}

