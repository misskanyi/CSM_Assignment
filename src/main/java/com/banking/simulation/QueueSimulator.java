package com.banking.simulation;

import com.banking.model.Customer;
import com.banking.model.QueueStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Single-server queue simulator for a banking system with configurable settings.
 */
public class QueueSimulator {

    private int customerCount = 100;
    private double iatMin = 1.0;
    private double iatMax = 8.0;
    private double serviceMin = 1.0;
    private double serviceMax = 6.0;

    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }

    public double getIatMin() { return iatMin; }
    public void setIatMin(double iatMin) { this.iatMin = iatMin; }

    public double getIatMax() { return iatMax; }
    public void setIatMax(double iatMax) { this.iatMax = iatMax; }

    public double getServiceMin() { return serviceMin; }
    public void setServiceMin(double serviceMin) { this.serviceMin = serviceMin; }

    public double getServiceMax() { return serviceMax; }
    public void setServiceMax(double serviceMax) { this.serviceMax = serviceMax; }

    /**
     * Converts a uniform random number R in (0,1) to a value in [min, max].
     */
    public static double uniform(double randomValue, double min, double max) {
        return min + (max - min) * randomValue;
    }

    /**
     * Generates inter-arrival times from random numbers using uniform bounds.
     */
    public double[] generateInterArrivalTimes(double[] randomNumbers) {
        double[] iat = new double[randomNumbers.length];
        for (int i = 0; i < randomNumbers.length; i++) {
            iat[i] = uniform(randomNumbers[i], iatMin, iatMax);
        }
        return iat;
    }

    /**
     * Generates service times from random numbers using uniform bounds.
     */
    public double[] generateServiceTimes(double[] randomNumbers) {
        double[] st = new double[randomNumbers.length];
        for (int i = 0; i < randomNumbers.length; i++) {
            st[i] = uniform(randomNumbers[i], serviceMin, serviceMax);
        }
        return st;
    }

    /**
     * Runs the queue simulation for the customers.
     */
    public List<Customer> simulate(double[] interArrivalTimes, double[] serviceTimes) {
        if (interArrivalTimes.length != customerCount || serviceTimes.length != customerCount) {
            throw new IllegalArgumentException(
                    "Both datasets must contain exactly " + customerCount + " values.");
        }

        List<Customer> customers = new ArrayList<>(customerCount);
        double clock = 0.0;
        double serverFreeTime = 0.0;

        for (int i = 0; i < customerCount; i++) {
            int customerNumber = i + 1;
            double iat = interArrivalTimes[i];
            double serviceTime = serviceTimes[i];

            double arrivalTime = (i == 0) ? iat : customers.get(i - 1).getArrivalTime() + iat;
            double serviceStartTime = Math.max(arrivalTime, serverFreeTime);
            double waitingTime = serviceStartTime - arrivalTime;
            double departureTime = serviceStartTime + serviceTime;
            double timeInSystem = departureTime - arrivalTime;

            customers.add(new Customer(
                    customerNumber, iat, serviceTime, arrivalTime,
                    serviceStartTime, waitingTime, departureTime, timeInSystem));

            serverFreeTime = departureTime;
            clock = departureTime;
        }

        return customers;
    }

    /**
     * Computes queue statistics from the simulation results.
     */
    public QueueStatistics computeStatistics(List<Customer> customers) {
        int n = customers.size();
        double sumWait = 0;
        double sumService = 0;
        double sumIat = 0;
        double sumTimeInSystem = 0;
        int waitedCount = 0;
        double totalServiceTime = 0;

        for (Customer c : customers) {
            sumWait += c.getWaitingTime();
            sumService += c.getServiceTime();
            sumIat += c.getInterArrivalTime();
            sumTimeInSystem += c.getTimeInSystem();
            totalServiceTime += c.getServiceTime();
            if (c.getWaitingTime() > 0) {
                waitedCount++;
            }
        }

        double totalSimTime = customers.get(n - 1).getDepartureTime();
        double totalIdleTime = totalSimTime - totalServiceTime;
        double serverUtilization = totalServiceTime / totalSimTime;

        double areaUnderQueue = 0;
        int maxQueue = 0;

        for (int i = 0; i < n; i++) {
            Customer current = customers.get(i);
            int queueAtArrival = 0;
            for (int j = 0; j < i; j++) {
                Customer other = customers.get(j);
                if (other.getDepartureTime() > current.getArrivalTime()) {
                    queueAtArrival++;
                }
            }
            maxQueue = Math.max(maxQueue, queueAtArrival);

            double periodEnd = (i < n - 1)
                    ? customers.get(i + 1).getArrivalTime()
                    : current.getDepartureTime();
            double periodStart = current.getArrivalTime();
            double duration = periodEnd - periodStart;
            if (duration > 0) {
                areaUnderQueue += queueAtArrival * duration;
            }
        }

        double avgQueueLength = areaUnderQueue / totalSimTime;

        return new QueueStatistics(
                n,
                sumWait / n,
                sumService / n,
                sumIat / n,
                sumTimeInSystem / n,
                serverUtilization,
                (double) waitedCount / n,
                waitedCount,
                totalSimTime,
                totalIdleTime,
                avgQueueLength,
                maxQueue
        );
    }
}