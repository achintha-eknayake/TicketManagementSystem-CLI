package org.tms.entity;


import org.tms.logging.Logger;

import java.util.concurrent.TimeUnit;

public class Customer implements Runnable {

    private int customerId;
    private int retrievalInterval;
    private int retrievalRate;
    private TicketPool ticketPool;
    private volatile boolean isRunning = true;

    public Customer(int customerId, int retrievalInterval, int retrievalRate, TicketPool ticketPool) {
        this.customerId = customerId;
        this.retrievalRate = retrievalRate;
        this.retrievalInterval = retrievalInterval;
        this.ticketPool = ticketPool;
    }

    public Customer() {
    }

    public Customer(int retrievalRate, int retrievalInterval, int customerId) {
        this.retrievalRate = retrievalRate;
        this.retrievalInterval = retrievalInterval;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                //logger.info("Customer {} is attempting to purchase {} tickets.", customerId, retrievalRate);
                System.out.println("Customer " + customerId + " is attempting to purchase "+ retrievalInterval + " Tickets");
                boolean success = ticketPool.removeTickets(retrievalRate, customerId);
                if (success) {
                    //logger.info("Customer {} successfully purchased {} tickets.", customerId, retrievalRate);
                    System.out.println("Customer " + customerId + " successfully purchased "+ retrievalInterval + " Tickets");
                    TimeUnit.MILLISECONDS.sleep(retrievalInterval);
                } else {
                    //logger.warn("Customer {} could not retrieve the requested tickets. Retrying soon...");
                    System.out.println("Customer "+ customerId +" could not retrieve the requested tickets. Retrying soon...");
                    TimeUnit.MILLISECONDS.sleep(retrievalInterval / 2); // Shorter wait for retry
                }
            } catch (InterruptedException e) {
                //logger.error("Customer {} was interrupted.", customerId);
                System.out.println("Customer " + customerId + " was interrupted. Exiting...");
                Thread.currentThread().interrupt();
            }
        }
        //logger.info("Customer {} has stopped.", customerId);
        System.out.println("Customer " + customerId + " has stopped.");
    }

    public void stop() {
        isRunning = false;
    }
}
