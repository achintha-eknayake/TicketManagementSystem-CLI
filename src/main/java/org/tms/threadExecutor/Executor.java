package org.tms.threadExecutor;

import org.tms.configuration.ConfigUtility;
import org.tms.configuration.Configuration;
import org.tms.thread.Customer;
import org.tms.ticketPool.TicketPool;
import org.tms.thread.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages the execution of the ticket pool simulation involving vendors and customers.
 * The simulation includes:</br>
 * - Vendors adding tickets to the ticket pool at specified intervals.</br>
 * - Customers retrieving tickets from the ticket pool at specified intervals.</br>
 * The simulation runs for a user-defined duration, after which all threads are stopped.
 */
public class Executor {

    /**
     * Starts the vendor-customer simulation using a command-line interface.
     * Initializes the ticket pool, vendors, and customers, and runs the simulation
     * for the specified duration.
     */
    public static void runVendorCustomerCLI() {

        TicketPool ticketPool = new TicketPool();
        ticketPool.initializeAvailableTickets(); // Initialize total tickets given by configuration class

        // Create a thread pool for managing vendors and customers
        ExecutorService executor = Executors.newCachedThreadPool();

        // Lists to store vendors and customers
        List<Vendor> vendors = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        // Get the simulation runtime duration from the user
        int timeDuration = ConfigUtility.integerInput("Enter simulation running time in seconds ");

        // Setup vendors and customers by given user input
        configureVendorsAndCustomers(ticketPool, vendors, customers);

        try {
            // Start the simulation
            System.out.println("Starting ticket pool simulation...");
            startSimulation(executor, vendors, customers);

            // Run the simulation for the specified duration
            runSimulation(timeDuration);

        } catch (InterruptedException e) {
            // Handle simulation interruption
            System.out.println("Simulation interrupted");
            Thread.currentThread().interrupt();
        } finally {
            // Stop all threads and save ticket history
            stopSimulation(executor, vendors, customers);
            ticketPool.saveTicketHistory();
        }

        // End of simulation
        System.out.println("Simulation ended.");
    }

    /**
     * Configures vendors and customers based on user input.
     *
     * @param ticketPool the shared ticket pool
     * @param vendors    the list to store vendor instances
     * @param customers  the list to store customer instances
     */
    private static void configureVendorsAndCustomers(TicketPool ticketPool, List<Vendor> vendors, List<Customer> customers) {
        Configuration configuration = ConfigUtility.getConfiguration();

        // Configure vendors
        int vendorCount = ConfigUtility.integerInput("Enter the number of vendors: ");
        for (int i = 1; i <= vendorCount; i++) {
            int ticketsReleaseRate;

            // Ensure the release rate does not exceed the maximum allowed
            do {
                ticketsReleaseRate = ConfigUtility.integerInput("Enter tickets per release for Vendor " + i + ":");
                if (ticketsReleaseRate > configuration.getMaximumCustomerRetrievalRate()) {
                    System.out.println("Ticket release rate should be lower than " + configuration.getMaximumTicketReleaseRate());
                }
            } while (ticketsReleaseRate > configuration.getMaximumCustomerRetrievalRate());

            int releaseInterval = ConfigUtility.integerInput("Enter release interval (ms) for Vendor " + i + ":");
            vendors.add(new Vendor(i, ticketsReleaseRate, releaseInterval, ticketPool));
        }

        // Configure customers details
        int customerCount = ConfigUtility.integerInput("Enter the number of customers: ");
        for (int i = 1; i <= customerCount; i++) {
            int retrievalRate;

            // Ensure the retrieval rate does not exceed the maximum allowed
            do {
                retrievalRate = ConfigUtility.integerInput("Enter retrieval rate for Customer " + i + ":");
                if (retrievalRate > configuration.getMaximumCustomerRetrievalRate()) {
                    System.out.println("Ticket retrieval rate should be lower than " + configuration.getMaximumCustomerRetrievalRate());
                }
            } while (retrievalRate > configuration.getMaximumTicketReleaseRate());

            int retrievalInterval = ConfigUtility.integerInput("Enter retrieval interval (ms) for Customer " + i + ":");
            customers.add(new Customer(i, retrievalInterval, retrievalRate, ticketPool));
        }
    }

    /**
     * Starts the simulation by submitting vendor and customer threads to the executor service.
     *
     * @param executor  the thread pool executor
     * @param vendors   the list of vendor instances
     * @param customers the list of customer instances
     */
    private static void startSimulation(ExecutorService executor, List<Vendor> vendors, List<Customer> customers) {
        // Submit vendors to the executor
        for (Vendor vendor : vendors) {
            executor.submit(vendor);
        }

        // Submit customers to the executor
        for (Customer customer : customers) {
            executor.submit(customer);
        }
    }

    /**
     * Runs the simulation for the specified duration.
     *
     * @param durationInSeconds the simulation runtime in seconds
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private static void runSimulation(int durationInSeconds) throws InterruptedException {
        for (int i = 0; i < durationInSeconds; i++) {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Simulation running... Second: " + (i + 1));
        }
    }

    /**
     * Stops the simulation by shutting down all threads and the executor service.
     *
     * @param executor  the thread pool executor
     * @param vendors   the list of vendor instances
     * @param customers the list of customer instances
     */
    private static void stopSimulation(ExecutorService executor, List<Vendor> vendors, List<Customer> customers) {
        // Stop all vendor threads
        for (Vendor vendor : vendors) {
            vendor.stop();
        }

        // Stop all customer threads
        for (Customer customer : customers) {
            customer.stop();
        }

        // Shut down the executor service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Forcing shutdown of remaining threads.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}