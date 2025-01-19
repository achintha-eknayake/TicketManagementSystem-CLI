package org.tms.configuration;

import java.util.concurrent.ThreadLocalRandom;


public class Configuration {

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    private final Object LOCK = new Object();
    private volatile boolean isRunning = true; // Ensures proper visibility across threads

    // Constructor
    public Configuration(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

//    public Configuration(){}

    public int getTotalTickets(){
        return totalTickets;
    }

    public int getTicketReleaseRate(){
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate(){
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity(){
        return maxTicketCapacity;
    }

    // Method to gracefully stop threads
    public void stopThreads() {
        isRunning = false;
        synchronized (LOCK) {
            LOCK.notifyAll(); // Wake up any waiting threads
        }
        //logger.info("Threads have been signaled to stop.");
        System.out.println("Threads have been signaled to stop");
    }

    // Producer method
    public void produce() throws InterruptedException {
        while (isRunning) {
            synchronized (LOCK) {
                while (totalTickets >= maxTicketCapacity && isRunning) {
                    //logger.info("Producer: Maximum capacity reached. Waiting...");
                    System.out.println("Producer: Maximum capacity reached. Waiting...");
                    LOCK.wait();
                }

                int randomRelease = ThreadLocalRandom.current().nextInt(ticketReleaseRate)+1;

                int ticketsToAdd = Math.min(randomRelease, maxTicketCapacity - totalTickets);
                totalTickets += ticketsToAdd;
                //logger.info("Producer: Added {} tickets. Total tickets: {}", ticketsToAdd, totalTickets);
                System.out.println("Producer: added "+ ticketsToAdd +". Total tickets: " + totalTickets);
                LOCK.notifyAll();
            }
            Thread.sleep(1000); // Simulate production delay
        }
        //logger.info("Producer has stopped.");
        System.out.println("Producer has stopped");
    }

    // Consumer method
    public void consume() throws InterruptedException {
        while (isRunning) {
            synchronized (LOCK) {
                while (totalTickets <= 0 && isRunning) {
                    //logger.info("Consumer: No tickets available. Waiting...");
                    System.out.println("Consumer: No tickets available. Waiting...");
                    LOCK.wait();
                }

                int randomRetrieve =ThreadLocalRandom.current().nextInt(customerRetrievalRate)+1;

                int ticketsToRetrieve = Math.min(randomRetrieve, totalTickets);
                totalTickets -= ticketsToRetrieve;
                //logger.info("Consumer: Retrieved {} tickets. Total tickets: {}", ticketsToRetrieve, totalTickets);
                System.out.println("Consumer: retrieved "+ ticketsToRetrieve+". Total tickets: " + totalTickets);

                LOCK.notifyAll();
            }
            Thread.sleep(1000); // Simulate consumption delay
        }
        //logger.info("Consumer has stopped.");
        System.out.println("Consumer has stopped");
    }

    // Main method
    public static void main(String[] args) {
        // Initialize configuration with realistic rates
        ConfigUtility.saveConfigFile();
        Configuration configuration = ConfigUtility.loadConfigFile();
        // Create producer thread
        Thread producerThread = new Thread(() -> {
            try {
                configuration.produce();
            } catch (InterruptedException e) {
                //logger.error("Producer thread interrupted.", e);
                System.out.println("Producer thread interrupted" + e.getMessage());
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }, "Producer");

        // Create consumer thread
        Thread consumerThread = new Thread(() -> {
            try {
                configuration.consume();
            } catch (InterruptedException e) {
                //logger.error("Consumer thread interrupted.", e);
                System.out.println("Consumer thread interrupted" + e.getMessage());
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }, "Consumer");

        // Start threads
        producerThread.start();
        consumerThread.start();

        // Run the system for a fixed duration and then stop
        try {
            Thread.sleep(15000); // Let the system run for 15 seconds
        } catch (InterruptedException e) {
            //logger.error("Main thread interrupted.", e);
            System.out.println("Main Thread interrupted" + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Signal threads to stop
        configuration.stopThreads();

        // Wait for threads to finish
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            //logger.error("Error joining threads.", e);
            System.out.println("Error joining threads " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        //logger.info("System has stopped.");
        System.out.println("System has stopped");
    }
}
