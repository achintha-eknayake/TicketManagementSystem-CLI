package org.tms.cli;

import org.tms.configuration.ConfigUtility;
import org.tms.entity.Customer;
import org.tms.entity.TicketPool;
import org.tms.entity.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VendorCustomerCLI {

    public static void main(String[] args) {
        // Initialize ticket pool
        TicketPool ticketPool = new TicketPool();

        // Initialize thread pool
        ExecutorService executor = Executors.newCachedThreadPool();

        // Initialize lists for vendors and customers
        List<Vendor> vendors = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        int timeDuration = ConfigUtility.integerInput("Enter stimulation running time in seconds ");

        // Get user input to configure vendors and customers
        configureVendorsAndCustomers(ticketPool, vendors, customers);

        try {
            // Start simulation
            //logger.info("Starting ticket pool simulation...");
            System.out.println("Starting ticket pool simulation...");
            startSimulation(executor, vendors, customers);

            // Run simulation for 10 seconds
            runSimulation(timeDuration);

        } catch (InterruptedException e) {
            //logger.error("Simulation interrupted: {}", e.getMessage());
            System.out.println("Simulation interrupted");
            Thread.currentThread().interrupt();
        } finally {
            // Stop all threads and clean up
            stopSimulation(executor, vendors, customers);
        }

        //logger.info("Simulation ended.");
        System.out.println("Simulation ended.");
    }

    private static void configureVendorsAndCustomers(TicketPool ticketPool, List<Vendor> vendors, List<Customer> customers) {
        Scanner scanner = new Scanner(System.in);

        // Configure vendors
        System.out.println("Enter the number of vendors:");
        int vendorCount = scanner.nextInt();
        for (int i = 1; i <= vendorCount; i++) {
            int ticketsPerRelease = ConfigUtility.integerInput("Enter tickets per release for Vendor " + i + ":");
            int releaseInterval = ConfigUtility.integerInput("Enter release interval (ms) for Vendor " + i + ":");
            vendors.add(new Vendor(i, ticketsPerRelease, releaseInterval, ticketPool));
        }

        // Configure customers
        System.out.println("Enter the number of customers:");
        int customerCount = scanner.nextInt();
        for (int i = 1; i <= customerCount; i++) {
            int retrievalRate = ConfigUtility.integerInput("Enter retrieval rate for Customer " + i + ":");
            int retrievalInterval = ConfigUtility.integerInput("Enter retrieval interval (ms) for Customer " + i + ":");
            customers.add(new Customer(i, retrievalInterval, retrievalRate, ticketPool));
        }
    }

    private static void startSimulation(ExecutorService executor, List<Vendor> vendors, List<Customer> customers) {
        // Submit vendors to executor
        for (Vendor vendor : vendors) {
            executor.submit(vendor);
        }

        // Submit customers to executor
        for (Customer customer : customers) {
            executor.submit(customer);
        }
    }

    private static void runSimulation(int durationInSeconds) throws InterruptedException {
        for (int i = 0; i < durationInSeconds; i++) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Simulation running... Second: " + (i + 1));
        }
    }

    private static void stopSimulation(ExecutorService executor, List<Vendor> vendors, List<Customer> customers) {
        // Stop all vendor threads
        for (Vendor vendor : vendors) {
            vendor.stop();
        }

        // Stop all customer threads
        for (Customer customer : customers) {
            customer.stop();
        }

        // Shut down executor service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                //logger.warn("Forcing shutdown of remaining threads.");
                System.out.println("Forcing shutdown of remaining threads.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
