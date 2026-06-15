package com.banking.gui;

import com.banking.excel.ExcelHandler;
import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

/**
 * Input screen with two datasets: inter-arrival and service time random numbers.
 */
public class InputPanel extends JPanel {

    public interface SimulationListener {
        void onSimulationComplete(double[] iatRandoms, double[] serviceRandoms);
    }

    private final JTextArea iatTextArea;
    private final JTextArea serviceTextArea;
    private SimulationListener listener;

    public InputPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Input Screen — Random Number Datasets", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JLabel info = new JLabel(
                "<html><center>Enter or load <b>100 uniform random numbers (0–1)</b> for each dataset.<br>" +
                "Dataset 1 → Inter-Arrival Times: Uniform(1, 8) minutes<br>" +
                "Dataset 2 → Service Times: Uniform(1, 6) minutes</center></html>",
                SwingConstants.CENTER);

        JPanel datasetsPanel = new JPanel(new GridLayout(1, 2, 15, 0));

        iatTextArea = createDataTextArea();
        serviceTextArea = createDataTextArea();

        datasetsPanel.add(wrapInTitledScroll("Dataset 1 — IAT Random Numbers (R)", iatTextArea));
        datasetsPanel.add(wrapInTitledScroll("Dataset 2 — Service Random Numbers (R)", serviceTextArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton generateBtn = new JButton("Generate Random Numbers");
        JButton loadExcelBtn = new JButton("Load from Excel");
        JButton saveTemplateBtn = new JButton("Save Excel Template");
        JButton runBtn = new JButton("Run Simulation");
        runBtn.setFont(runBtn.getFont().deriveFont(Font.BOLD));

        generateBtn.addActionListener(e -> generateRandomNumbers());
        loadExcelBtn.addActionListener(e -> loadFromExcel());
        saveTemplateBtn.addActionListener(e -> saveExcelTemplate());
        runBtn.addActionListener(e -> runSimulation());

        buttonPanel.add(generateBtn);
        buttonPanel.add(loadExcelBtn);
        buttonPanel.add(saveTemplateBtn);
        buttonPanel.add(runBtn);

        add(title, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);
        add(datasetsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fix layout: info on top, datasets in center
        remove(info);
        remove(datasetsPanel);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(info, BorderLayout.NORTH);
        center.add(datasetsPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    public void setSimulationListener(SimulationListener listener) {
        this.listener = listener;
    }

    private JTextArea createDataTextArea() {
        JTextArea area = new JTextArea(20, 25);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JScrollPane wrapInTitledScroll(String title, JTextArea textArea) {
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        return scroll;
    }

    private void generateRandomNumbers() {
        Random random = new Random();
        StringBuilder iatBuilder = new StringBuilder();
        StringBuilder serviceBuilder = new StringBuilder();

        for (int i = 0; i < QueueSimulator.CUSTOMER_COUNT; i++) {
            iatBuilder.append(String.format("%.4f", random.nextDouble()));
            serviceBuilder.append(String.format("%.4f", random.nextDouble()));
            if (i < QueueSimulator.CUSTOMER_COUNT - 1) {
                iatBuilder.append("\n");
                serviceBuilder.append("\n");
            }
        }

        iatTextArea.setText(iatBuilder.toString());
        serviceTextArea.setText(serviceBuilder.toString());
        JOptionPane.showMessageDialog(this,
                "Generated 100 random numbers for each dataset.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadFromExcel() {
        JFileChooser chooser = createExcelChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            double[][] datasets = ExcelHandler.readTwoDatasets(
                    chooser.getSelectedFile().getAbsolutePath(),
                    QueueSimulator.CUSTOMER_COUNT);

            iatTextArea.setText(formatArray(datasets[0]));
            serviceTextArea.setText(formatArray(datasets[1]));

            JOptionPane.showMessageDialog(this,
                    "Loaded random numbers from Excel successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading Excel file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveExcelTemplate() {
        double[] iatRandoms;
        double[] serviceRandoms;

        try {
            iatRandoms = parseRandomNumbers(iatTextArea.getText());
            serviceRandoms = parseRandomNumbers(serviceTextArea.getText());
        } catch (IllegalArgumentException ex) {
            Random random = new Random();
            iatRandoms = new double[QueueSimulator.CUSTOMER_COUNT];
            serviceRandoms = new double[QueueSimulator.CUSTOMER_COUNT];
            for (int i = 0; i < QueueSimulator.CUSTOMER_COUNT; i++) {
                iatRandoms[i] = random.nextDouble();
                serviceRandoms[i] = random.nextDouble();
            }
        }

        JFileChooser chooser = createExcelChooser();
        chooser.setSelectedFile(new java.io.File("simulation_input.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            ExcelHandler.createInputTemplate(
                    chooser.getSelectedFile().getAbsolutePath(),
                    iatRandoms, serviceRandoms);
            JOptionPane.showMessageDialog(this,
                    "Excel template saved successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving Excel file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runSimulation() {
        try {
            double[] iatRandoms = parseRandomNumbers(iatTextArea.getText());
            double[] serviceRandoms = parseRandomNumbers(serviceTextArea.getText());

            if (listener != null) {
                listener.onSimulationComplete(iatRandoms, serviceRandoms);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double[] parseRandomNumbers(String text) {
        String[] lines = text.trim().split("[,\\s]+");
        java.util.List<Double> values = new java.util.ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                double value = Double.parseDouble(trimmed);
                if (value < 0 || value > 1) {
                    throw new IllegalArgumentException(
                            "Random numbers must be between 0 and 1. Found: " + value);
                }
                values.add(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + trimmed);
            }
        }

        if (values.size() != QueueSimulator.CUSTOMER_COUNT) {
            throw new IllegalArgumentException(
                    "Each dataset must contain exactly " + QueueSimulator.CUSTOMER_COUNT
                            + " random numbers. Found: " + values.size());
        }

        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static String formatArray(double[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(String.format("%.4f", values[i]));
            if (i < values.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private JFileChooser createExcelChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        return chooser;
    }
}
