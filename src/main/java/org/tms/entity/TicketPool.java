package org.tms.entity;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private final Queue<Integer> tickets = new ConcurrentLinkedQueue<>();
    private final AtomicInteger ticketCounter = new AtomicInteger(0);
    private final Map<Integer, String> ticketHistory = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean isVendorTurn = true;

    public void addTicket(int count, int vendorId) throws InterruptedException {
        lock.lock();
        try {
            while (!isVendorTurn) {
                condition.await();
            }
            for (int i = 0; i < count; i++) {
                int ticketId = ticketCounter.incrementAndGet();
                tickets.add(ticketId);
                ticketHistory.put(ticketId, "Added by Vendor " + vendorId);
            }
            isVendorTurn = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean removeTickets(int count, int customerId) throws InterruptedException {
        lock.lock();
        try {
            while (isVendorTurn) {
                condition.await();
            }
            int retrieved = 0;
            for (int i = 0; i < count; i++) {
                Integer ticket = tickets.poll();
                if (ticket != null) {
                    ticketHistory.put(ticket, "Bought by Customer " + customerId);
                    retrieved++;
                } else {
                    break;
                }
            }
            isVendorTurn = true;
            condition.signalAll();
            return retrieved == count;
        } finally {
            lock.unlock();
        }
    }

    public int getTicketCount() {
        return tickets.size();
    }

    public Map<Integer, String> getTicketHistory() {
        return ticketHistory;
    }
}
