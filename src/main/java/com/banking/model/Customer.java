package com.banking.model;

/**
 * Represents one customer in the single-server queue simulation.
 */
public class Customer {
    private final int customerNumber;
    private final double interArrivalTime;
    private final double serviceTime;
    private final double arrivalTime;
    private final double serviceStartTime;
    private final double waitingTime;
    private final double departureTime;
    private final double timeInSystem;

    public Customer(int customerNumber, double interArrivalTime, double serviceTime,
                    double arrivalTime, double serviceStartTime, double waitingTime,
                    double departureTime, double timeInSystem) {
        this.customerNumber = customerNumber;
        this.interArrivalTime = interArrivalTime;
        this.serviceTime = serviceTime;
        this.arrivalTime = arrivalTime;
        this.serviceStartTime = serviceStartTime;
        this.waitingTime = waitingTime;
        this.departureTime = departureTime;
        this.timeInSystem = timeInSystem;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public double getInterArrivalTime() {
        return interArrivalTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getServiceStartTime() {
        return serviceStartTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public double getDepartureTime() {
        return departureTime;
    }

    public double getTimeInSystem() {
        return timeInSystem;
    }
}
