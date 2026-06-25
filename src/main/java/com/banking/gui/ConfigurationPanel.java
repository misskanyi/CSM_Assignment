package com.banking.gui;

import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class ConfigurationPanel extends JPanel {

    public interface ConfigListener {
        void onRunSimulation(double[] iatTimes, double[] serviceTimes);
    }

    private final JSpinner numCustomersSpinner;
    private final JSpinner arrivalLowSpinner;
    private final JSpinner arrivalHighSpinner;
    private final JSpinner serviceLowSpinner;
    private final JSpinner serviceHighSpinner;
    private JLabel summaryLabel;

    private ConfigListener listener;

    public ConfigurationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        numCustomersSpinner = createSpinner(100, 1, 1000, 10);
        arrivalLowSpinner   = createSpinner(1,   1, 998,  1);
        arrivalHighSpinner  = createSpinner(8,   2, 999,  1);
        serviceLowSpinner   = createSpinner(1,   1, 998,  1);
        serviceHighSpinner  = createSpinner(6,   2, 999,  1);

        ChangeListener refresh = e -> updateSummary();
        numCustomersSpinner.addChangeListener(refresh);
        arrivalLowSpinner.addChangeListener(refresh);
        arrivalHighSpinner.addChangeListener(refresh);
        serviceLowSpinner.addChangeListener(refresh);
        serviceHighSpinner.addChangeListener(refresh);

        add(createTitleSection(), BorderLayout.NORTH);
        add(createCenterArea(),   BorderLayout.CENTER);
        add(createButtonPanel(),  BorderLayout.SOUTH);

        updateSummary();
    }

    public void setConfigListener(ConfigListener listener) {
        this.listener = listener;
    }

    public void reset() {
        numCustomersSpinner.setValue(100);
        arrivalLowSpinner.setValue(1);
        arrivalHighSpinner.setValue(8);
        serviceLowSpinner.setValue(1);
        serviceHighSpinner.setValue(6);
        updateSummary();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private JSpinner createSpinner(int value, int min, int max, int step) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        styleSpinner(s);
        return s;
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(GameTheme.FONT_BODY);
        s.setBackground(GameTheme.BACKGROUND_ELEVATED);
        s.setBorder(BorderFactory.createLineBorder(GameTheme.BORDER_COLOR, 1, true));
        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            JTextField tf = de.getTextField();
            tf.setFont(GameTheme.FONT_BODY);
            tf.setForeground(GameTheme.TEXT_PRIMARY);
            tf.setBackground(GameTheme.BACKGROUND_ELEVATED);
            tf.setCaretColor(GameTheme.PRIMARY);
            tf.setHorizontalAlignment(JTextField.RIGHT);
            tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
    }

    private void updateSummary() {
        if (summaryLabel == null) return;
        int n    = (int) numCustomersSpinner.getValue();
        int aLow = (int) arrivalLowSpinner.getValue();
        int aHi  = (int) arrivalHighSpinner.getValue();
        int sLow = (int) serviceLowSpinner.getValue();
        int sHi  = (int) serviceHighSpinner.getValue();
        summaryLabel.setText("<html><center>"
                + "<b>" + n + "</b> customers &nbsp;·&nbsp; "
                + "Arrival: Uniform(<b>" + aLow + "</b>, <b>" + aHi + "</b>) min &nbsp;·&nbsp; "
                + "Service: Uniform(<b>" + sLow + "</b>, <b>" + sHi + "</b>) min"
                + "</center></html>");
    }

    // ── layout sections ──────────────────────────────────────────────────────

    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel title = new JLabel("Configuration");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);

        JPanel infoCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(GameTheme.PRIMARY.getRed(),
                        GameTheme.PRIMARY.getGreen(), GameTheme.PRIMARY.getBlue(), 25));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setBorder(BorderFactory.createEmptyBorder(2, 16, 2, 16));
        JLabel infoText = new JLabel("Set parameters below — random numbers are generated automatically");
        infoText.setFont(GameTheme.FONT_BODY);
        infoText.setForeground(GameTheme.TEXT_SECONDARY);
        infoCard.add(infoText);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setOpaque(false);
        titleRow.add(title);

        panel.add(titleRow, BorderLayout.NORTH);
        panel.add(infoCard,  BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCenterArea() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(820, 340));

        row.add(buildGroupCard("Simulation",
                new String[]{"Number of Customers"},
                new JSpinner[]{numCustomersSpinner},
                GameTheme.PRIMARY));

        JPanel distCards = new JPanel(new GridLayout(2, 1, 0, 16));
        distCards.setOpaque(false);
        distCards.add(buildGroupCard("Arrival Time  (minutes)",
                new String[]{"Lower Bound", "Upper Bound"},
                new JSpinner[]{arrivalLowSpinner, arrivalHighSpinner},
                GameTheme.ACCENT));
        distCards.add(buildGroupCard("Service Time  (minutes)",
                new String[]{"Lower Bound", "Upper Bound"},
                new JSpinner[]{serviceLowSpinner, serviceHighSpinner},
                GameTheme.SUCCESS));
        row.add(distCards);

        outer.add(row);

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(outer, BorderLayout.CENTER);
        wrapper.add(createSummaryBar(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildGroupCard(String title,
                                   String[] labels, JSpinner[] spinners, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 18)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 18, 18));
                g2.setColor(GameTheme.BACKGROUND_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 18, 18));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, 4, 4, 4));
                g2.setColor(GameTheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(GameTheme.FONT_SUBHEADER);
        titleLbl.setForeground(GameTheme.TEXT_PRIMARY);
        header.add(titleLbl);

        // fields
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 12);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.5;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(GameTheme.FONT_BODY);
            lbl.setForeground(GameTheme.TEXT_SECONDARY);
            fields.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 0.5;
            spinners[i].setPreferredSize(new Dimension(120, 34));
            fields.add(spinners[i], gc);
        }

        card.add(header, BorderLayout.NORTH);
        card.add(fields,  BorderLayout.CENTER);
        return card;
    }

    private JPanel createSummaryBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GameTheme.BACKGROUND_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(GameTheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        summaryLabel = new JLabel();
        summaryLabel.setFont(GameTheme.FONT_BODY);
        summaryLabel.setForeground(GameTheme.TEXT_SECONDARY);
        bar.add(summaryLabel);
        return bar;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GameTheme.BACKGROUND_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 14));

        JButton runBtn   = GameTheme.createAccentButton("▶  Run Simulation");
        JButton resetBtn = GameTheme.createGameButton("↺  Reset", GameTheme.DANGER);

        runBtn.addActionListener(e -> runSimulation());
        resetBtn.addActionListener(e -> reset());

        panel.add(resetBtn);
        panel.add(runBtn);
        return panel;
    }

    // ── simulation ───────────────────────────────────────────────────────────

    private void runSimulation() {
        int    n        = (int) numCustomersSpinner.getValue();
        double aLow     = ((Number) arrivalLowSpinner.getValue()).doubleValue();
        double aHigh    = ((Number) arrivalHighSpinner.getValue()).doubleValue();
        double sLow     = ((Number) serviceLowSpinner.getValue()).doubleValue();
        double sHigh    = ((Number) serviceHighSpinner.getValue()).doubleValue();

        if (aLow >= aHigh) {
            GameTheme.showMessage(this, "Config Error",
                    "Arrival Lower Bound must be less than Upper Bound.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sLow >= sHigh) {
            GameTheme.showMessage(this, "Config Error",
                    "Service Lower Bound must be less than Upper Bound.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Random rng = new Random();
        double[] iatR = new double[n];
        double[] svcR = new double[n];
        for (int i = 0; i < n; i++) {
            iatR[i] = rng.nextDouble();
            svcR[i] = rng.nextDouble();
        }

        double[] iatTimes = QueueSimulator.generateInterArrivalTimes(iatR, aLow, aHigh);
        double[] svcTimes = QueueSimulator.generateServiceTimes(svcR, sLow, sHigh);

        if (listener != null) {
            listener.onRunSimulation(iatTimes, svcTimes);
        }
    }
}
