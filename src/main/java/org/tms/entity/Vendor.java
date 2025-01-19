package org.tms.entity;

import java.util.concurrent.TimeUnit;

public class Vendor implements Runnable {

    //private static final Logger logger = LoggerFactory.getLogger(Vendor.class);

    private int vendorId;
    private int ticketsPerRelease;
    private int releaseInterval;
    private TicketPool ticketPool;
    private volatile boolean isRunning = true;

    public Vendor(int vendorId, int ticketsPerRelease, int releaseInterval, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.ticketsPerRelease = ticketsPerRelease;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
    }
    public Vendor(int vendorId, int ticketsPerRelease, int releaseInterval) {
        this.vendorId = vendorId;
        this.ticketsPerRelease = ticketsPerRelease;
        this.releaseInterval = releaseInterval;
    }
    public Vendor() {
        this.vendorId = 0;
        this.ticketsPerRelease = 1;
        this.releaseInterval = 1000; // Default interval (1 second)
        this.ticketPool = null;
    }

    @Override
    public void run() {
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                //logger.info("Vendor {} is releasing {} tickets.", vendorId, ticketsPerRelease);
                System.out.println("Vendor " + vendorId + " is releasing "+ticketsPerRelease+" tickets.");
                ticketPool.addTicket(ticketsPerRelease, vendorId);
                TimeUnit.MILLISECONDS.sleep(releaseInterval);
            } catch (InterruptedException e) {
                //logger.error("Vendor {} was interrupted during ticket release.", vendorId);
                System.out.println("Vendor " + vendorId + " was interrupted during ticket release.");
                Thread.currentThread().interrupt();
            }
        }
        //logger.info("Vendor {} has stopped running.", vendorId);
        System.out.println("Vendor " + vendorId + " has stopped running.");
    }

    public void stop() {
        isRunning = false;
    }
}

