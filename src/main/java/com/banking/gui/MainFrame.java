package com.banking.gui;

import com.banking.excel.ExcelHandler;
import com.banking.model.Customer;
import com.banking.model.QueueStatistics;
import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Main application window with input screen and two output screens.
 */
public class MainFrame extends JFrame {

    private final InputPanel inputPanel;
    private final TableOutputPanel tableOutputPanel;
    private final StatisticsOutputPanel statisticsOutputPanel;
    private final QueueSimulator simulator = new QueueSimulator();

    private List<Customer> lastResults;
    private QueueStatistics lastStatistics;

    public MainFrame() {
        super("Banking Queue Simulation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        inputPanel = new InputPanel();
        tableOutputPanel = new TableOutputPanel();
        statisticsOutputPanel = new StatisticsOutputPanel();

        inputPanel.setSimulationListener(this::runSimulation);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Input", inputPanel);
        tabbedPane.addTab("Output 1: Table", tableOutputPanel);
        tabbedPane.addTab("Output 2: Statistics", statisticsOutputPanel);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("Export Results to Excel");
        exportBtn.addActionListener(e -> exportToExcel());
        exportPanel.add(exportBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(exportPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void runSimulation(double[] iatRandoms, double[] serviceRandoms) {
        double[] interArrivalTimes = QueueSimulator.generateInterArrivalTimes(iatRandoms);
        double[] serviceTimes = QueueSimulator.generateServiceTimes(serviceRandoms);

        lastResults = simulator.simulate(interArrivalTimes, serviceTimes);
        lastStatistics = simulator.computeStatistics(lastResults);

        tableOutputPanel.displayResults(lastResults);
        statisticsOutputPanel.displayStatistics(lastStatistics);

        JOptionPane.showMessageDialog(this,
                "Simulation complete for 100 customers.\n"
                        + "View results in Output 1 (Table) and Output 2 (Statistics).",
                "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToExcel() {
        if (lastResults == null || lastStatistics == null) {
            JOptionPane.showMessageDialog(this,
                    "Please run the simulation first before exporting.",
                    "No Results", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new java.io.File("simulation_results.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".xlsx")) {
            path += ".xlsx";
        }

        try {
            ExcelHandler.exportResults(path, lastResults, lastStatistics);
            JOptionPane.showMessageDialog(this,
                    "Results exported to:\n" + path,
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting to Excel:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
