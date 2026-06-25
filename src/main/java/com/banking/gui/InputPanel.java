package com.banking.gui;

import com.banking.excel.ExcelHandler;
import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.Random;

/**
 * Game-styled input screen for entering simulation parameters.
 */
public class InputPanel extends JPanel {

    public interface SimulationListener {
        void onSimulationComplete(double[] iatRandoms, double[] serviceRandoms);
    }

    private final JTextArea iatTextArea;
    private final JTextArea serviceTextArea;
    private SimulationListener listener;
    private JLabel iatCountLabel;
    private JLabel serviceCountLabel;

    public InputPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title section
        JPanel titlePanel = createTitleSection();
        
        // Main content with two data input cards
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        iatTextArea = createStyledTextArea();
        serviceTextArea = createStyledTextArea();
        
        // Add text change listeners
        iatTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCount(iatTextArea, iatCountLabel); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCount(iatTextArea, iatCountLabel); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCount(iatTextArea, iatCountLabel); }
        });
        
        serviceTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCount(serviceTextArea, serviceCountLabel); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCount(serviceTextArea, serviceCountLabel); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCount(serviceTextArea, serviceCountLabel); }
        });
        
        contentPanel.add(createDataCard("", "Inter-Arrival Times",
                "Uniform(1, 8) minutes", iatTextArea, true));
        contentPanel.add(createDataCard("", "Service Times",
                "Uniform(1, 6) minutes", serviceTextArea, false));
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Main title
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);
        
        JLabel title = new JLabel("Simulation Setup");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);

        titleRow.add(title);
        
        // Subtitle info card
        JPanel infoCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(GameTheme.PRIMARY.getRed(), GameTheme.PRIMARY.getGreen(), 
                        GameTheme.PRIMARY.getBlue(), 30));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel infoText = new JLabel("Enter 100 random numbers (0-1) for each dataset, or generate them automatically");
        infoText.setFont(GameTheme.FONT_BODY);
        infoText.setForeground(GameTheme.TEXT_SECONDARY);

        infoCard.add(infoText);
        
        panel.add(titleRow, BorderLayout.NORTH);
        panel.add(infoCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDataCard(String icon, String title, String subtitle, JTextArea textArea, boolean isIat) {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 20, 20));
                
                // Card background
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                
                // Border
                g2d.setColor(GameTheme.BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with icon and title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameTheme.FONT_SUBHEADER);
        titleLabel.setForeground(GameTheme.TEXT_PRIMARY);
        
        titleRow.add(iconLabel);
        titleRow.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(GameTheme.FONT_SMALL);
        subtitleLabel.setForeground(GameTheme.TEXT_MUTED);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 34, 0, 0));
        
        header.add(titleRow, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.CENTER);
        
        // Counter badge
        JPanel counterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        counterPanel.setOpaque(false);
        
        JLabel countLabel = new JLabel("0/100") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                String text = getText();
                boolean isComplete = text.startsWith("100/");
                
                g2d.setColor(isComplete ? GameTheme.SUCCESS : GameTheme.BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        countLabel.setFont(GameTheme.FONT_SMALL);
        countLabel.setForeground(GameTheme.TEXT_PRIMARY);
        countLabel.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
        if (isIat) {
            iatCountLabel = countLabel;
        } else {
            serviceCountLabel = countLabel;
        }
        
        counterPanel.add(countLabel);
        header.add(counterPanel, BorderLayout.EAST);
        
        // Styled scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getVerticalScrollBar().setBackground(GameTheme.BACKGROUND_ELEVATED);
        scrollPane.getHorizontalScrollBar().setBackground(GameTheme.BACKGROUND_ELEVATED);
        
        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(15, 25);
        area.setOpaque(false);
        area.setFont(GameTheme.FONT_MONO);
        area.setForeground(GameTheme.TEXT_PRIMARY);
        area.setCaretColor(GameTheme.PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        JButton generateBtn = GameTheme.createSecondaryButton("Generate Random");
        JButton loadExcelBtn = GameTheme.createPrimaryButton("Load Excel");
        JButton saveTemplateBtn = GameTheme.createGameButton("Save Template", GameTheme.BACKGROUND_ELEVATED);
        JButton clearBtn = GameTheme.createGameButton("Clear", GameTheme.DANGER);
        JButton runBtn = GameTheme.createAccentButton("Run Simulation");
        
        generateBtn.addActionListener(e -> generateRandomNumbers());
        loadExcelBtn.addActionListener(e -> loadFromExcel());
        saveTemplateBtn.addActionListener(e -> saveExcelTemplate());
        clearBtn.addActionListener(e -> clear());
        runBtn.addActionListener(e -> runSimulation());
        
        panel.add(generateBtn);
        panel.add(loadExcelBtn);
        panel.add(saveTemplateBtn);
        panel.add(clearBtn);
        panel.add(runBtn);
        
        return panel;
    }
    
    private void updateCount(JTextArea area, JLabel label) {
        if (label == null) return;
        
        String text = area.getText().trim();
        if (text.isEmpty()) {
            label.setText("0/100");
            label.repaint();
            return;
        }
        
        String[] parts = text.split("[,\\s]+");
        int count = 0;
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                count++;
            }
        }
        label.setText(count + "/100");
        label.repaint();
    }

    public void setSimulationListener(SimulationListener listener) {
        this.listener = listener;
    }

    public void clear() {
        iatTextArea.setText("");
        serviceTextArea.setText("");
        updateCount(iatTextArea, iatCountLabel);
        updateCount(serviceTextArea, serviceCountLabel);
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
        
        showSuccessToast("Random numbers generated!");
    }
    
    private void showSuccessToast(String message) {
        JDialog toast = new JDialog((Frame) SwingUtilities.getWindowAncestor(this));
        toast.setUndecorated(true);
        toast.setSize(300, 60);
        toast.setLocationRelativeTo(this);
        
        JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.SUCCESS);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2d.dispose();
            }
        };
        content.setOpaque(false);
        
        JLabel icon = new JLabel("✓");
        icon.setFont(GameTheme.FONT_BUTTON);
        icon.setForeground(Color.WHITE);
        
        JLabel text = new JLabel(message);
        text.setFont(GameTheme.FONT_BUTTON);
        text.setForeground(Color.WHITE);
        
        content.add(icon);
        content.add(text);
        
        toast.setContentPane(content);
        toast.setBackground(new Color(0, 0, 0, 0));
        toast.setVisible(true);
        
        // Auto close after 2 seconds
        Timer timer = new Timer(2000, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();
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

            showSuccessToast("Data loaded from Excel!");
        } catch (IOException ex) {
            GameTheme.showMessage(this, "Error", 
                    "Error loading Excel file:\n" + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
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
            showSuccessToast("Template saved!");
        } catch (IOException ex) {
            GameTheme.showMessage(this, "Error",
                    "Error saving Excel file:\n" + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
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
            GameTheme.showMessage(this, "Input Error",
                    ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
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
