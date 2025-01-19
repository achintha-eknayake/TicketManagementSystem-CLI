package org.tms.cli;


import org.tms.configuration.ConfigUtility;
import org.tms.configuration.Configuration;

public class ConfigurationCLI {

    public static void runCLI(int stopTime){
        Configuration configuration = ConfigUtility.loadConfigFile();

        // Create producer thread
        Thread producerThread = new Thread(() -> {
            try {
                configuration.produce();
            } catch (InterruptedException e) {
                //logger.error("Producer thread interrupted.", e);
                System.out.println("Producer thread interrupted.");
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }, "Producer");

        // Create consumer thread
        Thread consumerThread = new Thread(() -> {
            try {
                configuration.consume();
            } catch (InterruptedException e) {
                //logger.error("Consumer thread interrupted.", e);
                System.out.println("Consumer thread interrupted.");
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }, "Consumer");

        // Start threads
        producerThread.start();
        consumerThread.start();

        // Run the system for a fixed duration and then stop
        try {
            Thread.sleep(stopTime); // Run for given time period in seconds
        } catch (InterruptedException e) {
            //logger.error("Main thread interrupted.", e);
            System.out.println("Main thread interrupted.");
            Thread.currentThread().interrupt();
        }
        // Signal threads to stop
        configuration.stopThreads();

        // Wait for threads to finish
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            //logger.error("Error joining threads.");
            System.out.println("Error joining threads.");
        }

        //logger.info("System has stopped.");
        System.out.println("System has stopped.");
    }

    public static void startStimulation() {

        String starLine = "***************************************************************";
        System.out.println(starLine);
        System.out.println("Welcome to Simulation Producer-Consumer Ticket Handling Service");
        System.out.println(starLine);

        int stopTime = ConfigUtility.integerInput("Enter run time for stimulation in seconds.: ");
        stopTime = stopTime*1000;

        int option;
        do {
            option = ConfigUtility.integerInput(
                    """
                            Enter 1 if you want to run using the previously saved configuration file:\
                            
                            Enter 2 if you want to new a configuration file:\
                     
                            Enter 0 to exit:\
                            
                            Enter your option:"""
            );

            if (option < 0 || option > 2) {
                //logger.error("Invalid option. Please try again.");
                System.out.println("Invalid option. Please try again.");
            }

        } while (option < 0 || option > 2);

        switch (option) {
            case 1:
                runCLI(stopTime);
                break;

            case 2:
                //logger.info("Creating a new configuration file...");
                System.out.println("Creating new configuration file.");
                ConfigUtility.saveConfigFile();
                //logger.info("Loading the saved configuration file...");
                System.out.println("Loading the saved configuration file...");
                runCLI(stopTime);
                break;

            case 0:
                //logger.info("System will exit.");
                System.out.println("Exiting...");
                break;

            default:
                //logger.error("Unexpected error.");
                System.out.println("Invalid option. Please try again.");
                break;
        }

        //logger.info("Thank you for using the Ticket Handling Stimulation !");
        System.out.println("Thank you for using the Ticket Handling Stimulation !");
    }

    public static void main(String[] args) {
        startStimulation();
    }

}


