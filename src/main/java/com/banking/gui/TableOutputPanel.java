package com.banking.gui;

import com.banking.model.Customer;
import com.banking.model.QueueStatistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Output Screen 1: Displays the customer simulation table.
 */
public class TableOutputPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public TableOutputPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Output Screen 1 — Customer Simulation Table", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        String[] columns = {
                "Customer #", "IAT (min)", "Arrival (min)", "Service (min)",
                "Start (min)", "Wait (min)", "Departure (min)", "Time in System (min)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 500));

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayResults(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getCustomerNumber(),
                    format(c.getInterArrivalTime()),
                    format(c.getArrivalTime()),
                    format(c.getServiceTime()),
                    format(c.getServiceStartTime()),
                    format(c.getWaitingTime()),
                    format(c.getDepartureTime()),
                    format(c.getTimeInSystem())
            });
        }
    }

    public void clear() {
        tableModel.setRowCount(0);
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }
}
