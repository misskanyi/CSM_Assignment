package com.banking.gui;

import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class ConfigurationPanel extends JPanel {

    public interface ConfigListener {
        void onRunSimulation(double[] iatTimes, double[] serviceTimes);
    }

    private ConfigListener listener;

    private final JTextField txtCustomers;
    private final JTextField txtArrivalMin;
    private final JTextField txtArrivalMax;
    private final JTextField txtServiceMin;
    private final JTextField txtServiceMax;

    public ConfigurationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        txtCustomers  = createStyledField("100");
        txtArrivalMin = createStyledField("1.0");
        txtArrivalMax = createStyledField("8.0");
        txtServiceMin = createStyledField("1.0");
        txtServiceMax = createStyledField("6.0");

        // Title
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Simulation Parameters Configuration");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        titleRow.add(title);
        add(titleRow, BorderLayout.NORTH);

        // Centre: card wrapper
        JPanel centreWrapper = new JPanel(new GridBagLayout());
        centreWrapper.setOpaque(false);

        JPanel formCard = buildFormCard();
        centreWrapper.add(formCard);
        add(centreWrapper, BorderLayout.CENTER);
    }

    public void setConfigListener(ConfigListener l) { this.listener = l; }

    // ── form card ────────────────────────────────────────────────────────────

    private JPanel buildFormCard() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GameTheme.BACKGROUND_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(GameTheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(36, 48, 30, 48));
        card.setPreferredSize(new Dimension(600, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(10, 10, 10, 10);

        addRow(card, gbc, 0, "Number of Customers:",       txtCustomers);
        addRow(card, gbc, 1, "Arrival Lower Bound (min):", txtArrivalMin);
        addRow(card, gbc, 2, "Arrival Upper Bound (min):", txtArrivalMax);
        addRow(card, gbc, 3, "Service Lower Bound (min):", txtServiceMin);
        addRow(card, gbc, 4, "Service Upper Bound (min):", txtServiceMax);

        // Buttons
        gbc.gridy     = 5;
        gbc.gridx     = 0;
        gbc.gridwidth = 2;
        gbc.insets    = new Insets(24, 10, 0, 10);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);

        JButton resetBtn = GameTheme.createGameButton("↺  Reset", GameTheme.DANGER);
        JButton runBtn   = GameTheme.createAccentButton("▶  Run Simulation");
        resetBtn.addActionListener(e -> resetToDefaults());
        runBtn.addActionListener(e -> runSimulation());

        btnRow.add(resetBtn);
        btnRow.add(runBtn);
        card.add(btnRow, gbc);

        return card;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String labelText, JTextField field) {
        gbc.gridy     = row;
        gbc.gridwidth = 1;

        gbc.gridx   = 0;
        gbc.weightx = 0.5;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(GameTheme.FONT_BODY);
        lbl.setForeground(GameTheme.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx   = 1;
        gbc.weightx = 0.5;
        panel.add(field, gbc);
    }

    private JTextField createStyledField(String val) {
        JTextField f = new JTextField(val, 14);
        f.setFont(GameTheme.FONT_BODY);
        f.setBackground(GameTheme.BACKGROUND_ELEVATED);
        f.setForeground(GameTheme.TEXT_PRIMARY);
        f.setCaretColor(GameTheme.PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ── actions ──────────────────────────────────────────────────────────────

    public void resetToDefaults() {
        txtCustomers.setText("100");
        txtArrivalMin.setText("1.0");
        txtArrivalMax.setText("8.0");
        txtServiceMin.setText("1.0");
        txtServiceMax.setText("6.0");
    }

    private void runSimulation() {
        int n;
        double aMin, aMax, sMin, sMax;
        try {
            n    = Integer.parseInt(txtCustomers.getText().trim());
            aMin = Double.parseDouble(txtArrivalMin.getText().trim());
            aMax = Double.parseDouble(txtArrivalMax.getText().trim());
            sMin = Double.parseDouble(txtServiceMin.getText().trim());
            sMax = Double.parseDouble(txtServiceMax.getText().trim());
        } catch (NumberFormatException ex) {
            GameTheme.showMessage(SwingUtilities.getWindowAncestor(this),
                    "Invalid Input", "All fields must be numeric.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (n < 1) {
            GameTheme.showMessage(SwingUtilities.getWindowAncestor(this),
                    "Invalid Input", "Number of customers must be at least 1.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (aMin >= aMax) {
            GameTheme.showMessage(SwingUtilities.getWindowAncestor(this),
                    "Invalid Input", "Arrival Lower Bound must be less than Upper Bound.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sMin >= sMax) {
            GameTheme.showMessage(SwingUtilities.getWindowAncestor(this),
                    "Invalid Input", "Service Lower Bound must be less than Upper Bound.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Random rng = new Random();
        double[] iatR = new double[n];
        double[] svcR = new double[n];
        for (int i = 0; i < n; i++) {
            iatR[i] = rng.nextDouble();
            svcR[i] = rng.nextDouble();
        }

        double[] iatTimes = new double[n];
        double[] svcTimes = new double[n];
        for (int i = 0; i < n; i++) {
            iatTimes[i] = QueueSimulator.uniform(iatR[i], aMin, aMax);
            svcTimes[i] = QueueSimulator.uniform(svcR[i], sMin, sMax);
        }

        if (listener != null) {
            listener.onRunSimulation(iatTimes, svcTimes);
        }
    }

    // ── getters used by MainFrame ────────────────────────────────────────────

    public int    getCustomerCount() { return Integer.parseInt(txtCustomers.getText().trim()); }
    public double getArrivalMin()    { return Double.parseDouble(txtArrivalMin.getText().trim()); }
    public double getArrivalMax()    { return Double.parseDouble(txtArrivalMax.getText().trim()); }
    public double getServiceMin()    { return Double.parseDouble(txtServiceMin.getText().trim()); }
    public double getServiceMax()    { return Double.parseDouble(txtServiceMax.getText().trim()); }
}
