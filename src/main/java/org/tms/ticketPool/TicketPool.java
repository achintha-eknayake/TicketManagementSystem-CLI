package org.tms.ticketPool;

import org.tms.configuration.ConfigUtility;
import org.tms.configuration.Configuration;

import java.io.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages a pool of tickets, allowing vendors to add tickets and customers to retrieve them.
 * The class also maintains a history of ticket transactions and supports saving this history to a file.
 */
public class TicketPool {

    // Queue to store available tickets
    private final Queue<Integer> tickets = new ConcurrentLinkedQueue<>();

    // Counter for generating unique ticket IDs
    private final AtomicInteger ticketCounter = new AtomicInteger(0);

    // Map to maintain ticket history (e.g., availability, who bought it)
    private final Map<Integer, String> ticketHistory = new ConcurrentHashMap<>();

    // Lock for thread synchronization
    private final ReentrantLock lock = new ReentrantLock();

    // Condition for managing vendor and customer turns
    private final Condition condition = lock.newCondition();

    // Flag to alternate turns between vendors and customers
    private boolean isVendorTurn = true;

    // Counter to track the total number of tickets sold
    private final AtomicInteger totalTicketsSold = new AtomicInteger(0);

    // File path for saving ticket history
    private static final String TICKET_HISTORY_FILE = "src/main/resources/TicketHistory.txt";

    // Configuration instance for retrieving system settings
    Configuration configuration = ConfigUtility.getConfiguration();

    /**
     * Initializes the ticket pool with a predefined number of tickets from the configuration.
     * Each ticket is assigned a unique ID and marked as available.
     */
    public void initializeAvailableTickets() {
        int totalAvailableTickets = configuration.getTotalTickets();
        for (int j = 0; j < totalAvailableTickets; j++) {
            int ticketId = ticketCounter.incrementAndGet();
            tickets.add(ticketId);
            ticketHistory.put(ticketId, "Available");
        }
    }

    /**
     * Allows a vendor to add a specified number of tickets to the pool.
     *
     * @param count    the number of tickets to add
     * @param vendorId the ID of the vendor adding the tickets
     * @throws InterruptedException if the thread is interrupted while waiting for its turn
     */
    public void addTicket(int count, int vendorId) throws InterruptedException {
        lock.lock();
        try {
            // Wait until it is the vendor's turn
            while (!isVendorTurn) {
                condition.await();
            }
            // Add tickets to the pool and update history
            for (int i = 0; i < count; i++) {
                int ticketId = ticketCounter.incrementAndGet();
                tickets.add(ticketId);
                ticketHistory.put(ticketId, "Added by Vendor " + vendorId);
            }
            // Switch to customer's turn and signal all waiting threads
            isVendorTurn = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Allows a customer to retrieve a specified number of tickets from the pool.
     *
     * @param count      the number of tickets to retrieve
     * @param customerId the ID of the customer retrieving the tickets
     * @return true if the customer successfully retrieves the requested number of tickets, false otherwise
     * @throws InterruptedException if the thread is interrupted while waiting for its turn
     */
    public boolean removeTickets(int count, int customerId) throws InterruptedException {
        lock.lock();
        try {
            // Wait until it is the customer's turn
            while (isVendorTurn) {
                condition.await();
            }
            int retrieved = 0;
            // Retrieve tickets from the pool
            for (int i = 0; i < count; i++) {
                Integer ticket = tickets.poll();
                if (ticket != null) {
                    ticketHistory.put(ticket, ticketHistory.get(ticket) + " Bought by Customer " + customerId);
                    retrieved++;
                } else {
                    break; // No more tickets available
                }
            }
            // Switch to vendor's turn and signal all waiting threads
            isVendorTurn = true;
            condition.signalAll();
            return retrieved == count; // Return true if all requested tickets were retrieved
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves the current number of tickets available in the pool.
     *
     * @return the count of available tickets
     */
    public int getTicketCount() {
        return tickets.size();
    }

    /**
     * Retrieves the transaction history of all tickets.
     *
     * @return a map containing ticket IDs and their transaction history
     */
    public Map<Integer, String> getTicketHistory() {
        return ticketHistory;
    }

    /**
     * Saves the ticket history to a file for future reference.
     * Each ticket's ID and transaction details are written to the file.
     */
    public void saveTicketHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKET_HISTORY_FILE))) {
            for (Map.Entry<Integer, String> entry : ticketHistory.entrySet()) {
                writer.write("Ticket ID: " + entry.getKey() + ", " + entry.getValue());
                writer.newLine();
            }
            System.out.println("Ticket history saved");
        } catch (IOException e) {
            System.err.println("Failed to save ticket history to file:");
            e.printStackTrace();
        }
    }
}
