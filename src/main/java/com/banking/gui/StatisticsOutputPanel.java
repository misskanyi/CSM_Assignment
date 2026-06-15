package com.banking.gui;

import com.banking.model.QueueStatistics;

import javax.swing.*;
import java.awt.*;

/**
 * Output Screen 2: Displays queue statistics summary.
 */
public class StatisticsOutputPanel extends JPanel {

    private final JLabel totalCustomersLabel = createValueLabel();
    private final JLabel avgWaitingLabel = createValueLabel();
    private final JLabel avgServiceLabel = createValueLabel();
    private final JLabel avgIatLabel = createValueLabel();
    private final JLabel avgTimeInSystemLabel = createValueLabel();
    private final JLabel serverUtilLabel = createValueLabel();
    private final JLabel probWaitingLabel = createValueLabel();
    private final JLabel customersWaitedLabel = createValueLabel();
    private final JLabel totalSimTimeLabel = createValueLabel();
    private final JLabel totalIdleTimeLabel = createValueLabel();
    private final JLabel avgQueueLengthLabel = createValueLabel();
    private final JLabel maxQueueLengthLabel = createValueLabel();

    public StatisticsOutputPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Output Screen 2 — Queue Statistics", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Simulation Results"),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addStatRow(statsPanel, gbc, 0, "Total Customers:", totalCustomersLabel);
        addStatRow(statsPanel, gbc, 1, "Average Waiting Time (min):", avgWaitingLabel);
        addStatRow(statsPanel, gbc, 2, "Average Service Time (min):", avgServiceLabel);
        addStatRow(statsPanel, gbc, 3, "Average Inter-Arrival Time (min):", avgIatLabel);
        addStatRow(statsPanel, gbc, 4, "Average Time in System (min):", avgTimeInSystemLabel);
        addStatRow(statsPanel, gbc, 5, "Server Utilization:", serverUtilLabel);
        addStatRow(statsPanel, gbc, 6, "Probability of Waiting:", probWaitingLabel);
        addStatRow(statsPanel, gbc, 7, "Customers Who Waited:", customersWaitedLabel);
        addStatRow(statsPanel, gbc, 8, "Total Simulation Time (min):", totalSimTimeLabel);
        addStatRow(statsPanel, gbc, 9, "Total Idle Time (min):", totalIdleTimeLabel);
        addStatRow(statsPanel, gbc, 10, "Average Queue Length:", avgQueueLengthLabel);
        addStatRow(statsPanel, gbc, 11, "Maximum Queue Length:", maxQueueLengthLabel);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(statsPanel);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void displayStatistics(QueueStatistics stats) {
        totalCustomersLabel.setText(String.valueOf(stats.getTotalCustomers()));
        avgWaitingLabel.setText(format(stats.getAverageWaitingTime()));
        avgServiceLabel.setText(format(stats.getAverageServiceTime()));
        avgIatLabel.setText(format(stats.getAverageInterArrivalTime()));
        avgTimeInSystemLabel.setText(format(stats.getAverageTimeInSystem()));
        serverUtilLabel.setText(formatPercent(stats.getServerUtilization()));
        probWaitingLabel.setText(formatPercent(stats.getProbabilityOfWaiting()));
        customersWaitedLabel.setText(stats.getCustomersWhoWaited() + " / " + stats.getTotalCustomers());
        totalSimTimeLabel.setText(format(stats.getTotalSimulationTime()));
        totalIdleTimeLabel.setText(format(stats.getTotalIdleTime()));
        avgQueueLengthLabel.setText(format(stats.getAverageQueueLength()));
        maxQueueLengthLabel.setText(String.valueOf(stats.getMaxQueueLength()));
    }

    public void clear() {
        totalCustomersLabel.setText("—");
        avgWaitingLabel.setText("—");
        avgServiceLabel.setText("—");
        avgIatLabel.setText("—");
        avgTimeInSystemLabel.setText("—");
        serverUtilLabel.setText("—");
        probWaitingLabel.setText("—");
        customersWaitedLabel.setText("—");
        totalSimTimeLabel.setText("—");
        totalIdleTimeLabel.setText("—");
        avgQueueLengthLabel.setText("—");
        maxQueueLengthLabel.setText("—");
    }

    private void addStatRow(JPanel panel, GridBagConstraints gbc, int row,
                            String labelText, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(valueLabel, gbc);
    }

    private static JLabel createValueLabel() {
        JLabel label = new JLabel("—");
        label.setFont(label.getFont().deriveFont(14f));
        return label;
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }

    private static String formatPercent(double value) {
        return String.format("%.2f%%", value * 100);
    }
}
