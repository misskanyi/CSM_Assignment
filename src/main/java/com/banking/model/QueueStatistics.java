package com.banking.model;

/**
 * Aggregate queue statistics computed from a simulation run.
 */
public class QueueStatistics {
    private final int totalCustomers;
    private final double averageWaitingTime;
    private final double averageServiceTime;
    private final double averageInterArrivalTime;
    private final double averageTimeInSystem;
    private final double serverUtilization;
    private final double probabilityOfWaiting;
    private final int customersWhoWaited;
    private final double totalSimulationTime;
    private final double totalIdleTime;
    private final double averageQueueLength;
    private final int maxQueueLength;

    public QueueStatistics(int totalCustomers, double averageWaitingTime, double averageServiceTime,
                           double averageInterArrivalTime, double averageTimeInSystem,
                           double serverUtilization, double probabilityOfWaiting,
                           int customersWhoWaited, double totalSimulationTime,
                           double totalIdleTime, double averageQueueLength, int maxQueueLength) {
        this.totalCustomers = totalCustomers;
        this.averageWaitingTime = averageWaitingTime;
        this.averageServiceTime = averageServiceTime;
        this.averageInterArrivalTime = averageInterArrivalTime;
        this.averageTimeInSystem = averageTimeInSystem;
        this.serverUtilization = serverUtilization;
        this.probabilityOfWaiting = probabilityOfWaiting;
        this.customersWhoWaited = customersWhoWaited;
        this.totalSimulationTime = totalSimulationTime;
        this.totalIdleTime = totalIdleTime;
        this.averageQueueLength = averageQueueLength;
        this.maxQueueLength = maxQueueLength;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public double getAverageInterArrivalTime() {
        return averageInterArrivalTime;
    }

    public double getAverageTimeInSystem() {
        return averageTimeInSystem;
    }

    public double getServerUtilization() {
        return serverUtilization;
    }

    public double getProbabilityOfWaiting() {
        return probabilityOfWaiting;
    }

    public int getCustomersWhoWaited() {
        return customersWhoWaited;
    }

    public double getTotalSimulationTime() {
        return totalSimulationTime;
    }

    public double getTotalIdleTime() {
        return totalIdleTime;
    }

    public double getAverageQueueLength() {
        return averageQueueLength;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }
}