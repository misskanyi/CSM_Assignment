package com.banking.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Panel to capture custom configuration values for the simulation parameters.
public class ConfigurationPanel extends JPanel {

    private final JTextField txtCustomers;
    private final JTextField txtArrivalMin;
    private final JTextField txtArrivalMax;
    private final JTextField txtServiceMin;
    private final JTextField txtServiceMax;

    public ConfigurationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title section
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Simulation Parameters Configuration");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        titleRow.add(title);
        add(titleRow, BorderLayout.NORTH);

        // Form fields container layout card
        JPanel formCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(GameTheme.BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2d.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCustomers = createStyledField("100");
        txtArrivalMin = createStyledField("1.0");
        txtArrivalMax = createStyledField("8.0");
        txtServiceMin = createStyledField("1.0");
        txtServiceMax = createStyledField("6.0");

        addFormRow(formCard, gbc, 0, "Number of Customers:", txtCustomers);
        addFormRow(formCard, gbc, 1, "Arrival Lower Bound (min):", txtArrivalMin);
        addFormRow(formCard, gbc, 2, "Arrival Upper Bound (min):", txtArrivalMax);
        addFormRow(formCard, gbc, 3, "Service Lower Bound (min):", txtServiceMin);
        addFormRow(formCard, gbc, 4, "Service Upper Bound (min):", txtServiceMax);

        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerContainer.setOpaque(false);
        centerContainer.add(formCard);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JTextField createStyledField(String defaultVal) {
        JTextField field = new JTextField(defaultVal, 12);
        field.setFont(GameTheme.FONT_BODY);
        field.setBackground(GameTheme.BACKGROUND_ELEVATED);
        field.setForeground(GameTheme.TEXT_PRIMARY);
        field.setCaretColor(GameTheme.PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(GameTheme.FONT_BODY);
        lbl.setForeground(GameTheme.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(field, gbc);
    }

    public void resetToDefaults() {
        txtCustomers.setText("100");
        txtArrivalMin.setText("1.0");
        txtArrivalMax.setText("8.0");
        txtServiceMin.setText("1.0");
        txtServiceMax.setText("6.0");
    }

    public int getCustomerCount() { return Integer.parseInt(txtCustomers.getText().trim()); }
    public double getArrivalMin() { return Double.parseDouble(txtArrivalMin.getText().trim()); }
    public double getArrivalMax() { return Double.parseDouble(txtArrivalMax.getText().trim()); }
    public double getServiceMin() { return Double.parseDouble(txtServiceMin.getText().trim()); }
    public double getServiceMax() { return Double.parseDouble(txtServiceMax.getText().trim()); }
}