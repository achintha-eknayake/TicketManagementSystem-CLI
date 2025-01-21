package org.tms.configuration;

/**
 * configuration settings for the ticket management system.
 * This includes settings such as the total number of available tickets at beginning, the maximum ticket release rate,
 * the maximum customer retrieval rate, and the maximum ticket capacity.
 */
public class Configuration {

    private final int totalTickets;
    private final int maximumTicketReleaseRate;
    private final int maximumCustomerRetrievalRate;
    private final int maxTicketCapacity;

    //Constructor to initialize configuration settings.
    public Configuration(int totalTickets, int maximumTicketReleaseRate, int maximumCustomerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.maximumTicketReleaseRate = maximumTicketReleaseRate;
        this.maximumCustomerRetrievalRate = maximumCustomerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

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
