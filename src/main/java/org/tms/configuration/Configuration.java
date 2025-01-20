package org.tms.configuration;

/**
 * Holds the configuration settings for the ticket management system.
 * This includes settings such as the total number of tickets, the maximum ticket release rate,
 * the maximum customer retrieval rate, and the maximum ticket capacity.
 */
public class Configuration {

    // Total number of tickets available in the system
    private final int totalTickets;

    // Maximum number of tickets a vendor can release at a time
    private final int maximumTicketReleaseRate;

    // Maximum number of tickets a customer can retrieve at a time
    private final int maximumCustomerRetrievalRate;

    // Maximum capacity of the ticket pool
    private final int maxTicketCapacity;

    /**
     * Constructor to initialize configuration settings.
     *
     * @param totalTickets              the total number of tickets available
     * @param maximumTicketReleaseRate  the maximum number of tickets a vendor can release at a time
     * @param maximumCustomerRetrievalRate the maximum number of tickets a customer can retrieve at a time
     * @param maxTicketCapacity         the maximum capacity of the ticket pool
     */
    public Configuration(int totalTickets, int maximumTicketReleaseRate, int maximumCustomerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.maximumTicketReleaseRate = maximumTicketReleaseRate;
        this.maximumCustomerRetrievalRate = maximumCustomerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Uncomment if needed for default configuration
    // public Configuration() {}

    /**
     * Gets the total number of tickets available.
     *
     * @return the total number of tickets
     */
    public int getTotalTickets() {
        return totalTickets;
    }

    /**
     * Gets the maximum number of tickets a vendor can release at a time.
     *
     * @return the maximum ticket release rate
     */
    public int getMaximumTicketReleaseRate() {
        return maximumTicketReleaseRate;
    }

    /**
     * Gets the maximum number of tickets a customer can retrieve at a time.
     *
     * @return the maximum customer retrieval rate
     */
    public int getMaximumCustomerRetrievalRate() {
        return maximumCustomerRetrievalRate;
    }

    /**
     * Gets the maximum capacity of the ticket pool.
     *
     * @return the maximum ticket capacity
     */
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}
