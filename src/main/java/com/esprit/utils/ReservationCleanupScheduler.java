package com.esprit.utils;

import com.esprit.services.films.TicketService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler to automatically release expired ticket reservations.
 */
@Slf4j
public class ReservationCleanupScheduler {

    private final ScheduledExecutorService scheduler;
    private final TicketService ticketService;

    public ReservationCleanupScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ReservationCleanup-Thread");
            t.setDaemon(true); // Allow JVM to exit if this is the only thread running
            return t;
        });
        this.ticketService = new TicketService();
    }

    /**
     * Starts the cleanup task.
     */
    public void start() {
        log.info("Starting reservation cleanup scheduler...");
        // Run every 5 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ticketService.releaseExpiredReservations();
            } catch (Exception e) {
                log.error("Error during reservation cleanup", e);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    /**
     * Stops the scheduler.
     */
    public void stop() {
        log.info("Stopping reservation cleanup scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
