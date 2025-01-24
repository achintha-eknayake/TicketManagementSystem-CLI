package org.tms.thread;

import org.tms.ticketPool.TicketPool;

import java.util.concurrent.TimeUnit;

/**
 * Represents a Customer that attempts to purchase tickets from a TicketPool.
 * The customer retrieves a specified number of tickets
 * at defined intervals. The behavior of the customer can be stopped by invoking
 * the {@link #stop()} method.
 */
public class Customer implements Runnable {

    // Unique ID for the customer
    private final int customerId;

    // Interval in milliseconds between ticket retrieval attempts
    private final int retrievalInterval;

    // Number of tickets the customer tries to retrieve in each attempt
    private final int retrievalRate;

    // Shared TicketPool object from which tickets are retrieved
    private final TicketPool ticketPool;

    // Flag to indicate whether the customer thread should continue running
    private volatile boolean isRunning = true;

    /**
     * Constructor for Customer instance.
     */
    public Customer(int customerId, int retrievalInterval, int retrievalRate, TicketPool ticketPool) {
        this.customerId = customerId;
        this.retrievalRate = retrievalRate;
        this.retrievalInterval = retrievalInterval;
        this.ticketPool = ticketPool;
    }

    /**
     * The main execution method for the customer thread.
     * Continuously attempts to retrieve tickets from the ticket pool with specified time intervals {@code retrievalRate}
     */
    @Override
    public void run() {
        // Keep running while the isRunning flag is true and the thread is not interrupted
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {

                System.out.println("Customer " + customerId + " is attempting to purchase " + retrievalRate + " Tickets");
                // Attempt to retrieve tickets from the ticket pool
                boolean success = ticketPool.removeTickets(retrievalRate, customerId);

                if (success) {
                    // Log success and wait for the next interval
                    System.out.println("Customer " + customerId + " successfully purchased " + retrievalRate + " Tickets");
                    TimeUnit.MILLISECONDS.sleep(retrievalInterval);
                } else {
                    // Log failure and retry after a shorter interval
                    System.out.println("Customer " + customerId + " could not retrieve the requested tickets. Retrying soon...");
                    TimeUnit.MILLISECONDS.sleep(retrievalInterval / 2);
                }
            } catch (InterruptedException e) {
                // Handle interruption, log the event, and exit the loop
                System.out.println("Customer " + customerId + " was interrupted. Exiting...");
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }
        System.out.println("Customer " + customerId + " has stopped.");
    }

    /**
     * Stops the customer thread by setting the isRunning flag to false.
     */
    public void stop() {
        isRunning = false;
    }
}
