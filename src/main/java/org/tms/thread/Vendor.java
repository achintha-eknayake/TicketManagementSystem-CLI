package org.tms.thread;

import org.tms.ticketPool.TicketPool;

import java.util.concurrent.TimeUnit;

/**
 * Represents a Vendor that periodically adds tickets to a shared TicketPool.
 * Vendors releases a specific number of tickets at defined intervals. The
 * operation can be stopped by invoking the {@link #stop()} method.
 */
public class Vendor implements Runnable {

    // Unique ID for the vendor
    private int vendorId;

    // Number of tickets to release per interval
    private int ticketsPerRelease;

    // Interval in milliseconds between ticket releases
    private int releaseInterval;

    // Shared TicketPool object to which tickets are added
    private TicketPool ticketPool;

    // Flag to indicate whether the vendor thread should continue running
    private volatile boolean isRunning = true;

    /**
     * Constructor for Vendor instance.
     */
    public Vendor(int vendorId, int ticketsPerRelease, int releaseInterval, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.ticketsPerRelease = ticketsPerRelease;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
    }


    /**
     * The main execution method for the vendor thread.
     * Continuously releases tickets to the TicketPool at the specified interval
     * until stopped or interrupted.
     */
    @Override
    public void run() {
        // Keep running while the isRunning flag is true and the thread is not interrupted
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // Log the ticket release
                System.out.println("Vendor " + vendorId + " is releasing " + ticketsPerRelease + " tickets.");

                // Add tickets to the ticket pool
                ticketPool.addTicket(ticketsPerRelease, vendorId);

                // Wait for the next release interval
                TimeUnit.MILLISECONDS.sleep(releaseInterval);
            } catch (InterruptedException e) {
                // Handle interruption, log the event, and exit the loop
                System.out.println("Vendor " + vendorId + " was interrupted during ticket release.");
                Thread.currentThread().interrupt(); // Preserve the interrupt status
            }
        }
        // Log when the vendor stops running
        System.out.println("Vendor " + vendorId + " has stopped running.");
    }

    /**
     * Stops the vendor thread by setting the isRunning flag to false.
     */
    public void stop() {
        isRunning = false;
    }
}
