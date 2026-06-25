package com.banking.gui;

import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Game-styled instructions panel with clickable accordion sections.
 */
public class InstructionsPanel extends JPanel {

    private JPanel contentPanel;

    public InstructionsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel titlePanel = createTitleSection();

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createAccordionSection("", "What This Simulates", createWhatSection(), true));
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(createAccordionSection("", "How to Play", createHowToSection(), false));
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(createAccordionSection("", "Understanding the Results", createOutputsSection(), false));
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(createAccordionSection("", "Key Insights", createInsightsSection(), false));
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(createAccordionSection("", "Pro Tips", createTipsSection(), false));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(GameTheme.BACKGROUND_DARK);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);

        JLabel title = new JLabel("Welcome to Queue Simulator!");
        title.setFont(GameTheme.FONT_TITLE);
        title.setForeground(GameTheme.TEXT_PRIMARY);

        titleRow.add(title);

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(GameTheme.PRIMARY.getRed(), GameTheme.PRIMARY.getGreen(),
                                GameTheme.PRIMARY.getBlue(), 40),
                        getWidth(), 0, new Color(GameTheme.SECONDARY.getRed(), GameTheme.SECONDARY.getGreen(),
                                GameTheme.SECONDARY.getBlue(), 40)
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel badgeText = new JLabel("Click any section below to expand or collapse it");
        badgeText.setFont(GameTheme.FONT_BODY);
        badgeText.setForeground(GameTheme.TEXT_SECONDARY);
        badge.add(badgeText);

        panel.add(titleRow, BorderLayout.NORTH);
        panel.add(badge, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccordionSection(String icon, String title, JPanel content, boolean expandedInitially) {
        final boolean[] hover = {false};

        JPanel section = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 20, 20));

                g2d.setColor(hover[0] ? GameTheme.BACKGROUND_ELEVATED : GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));

                g2d.setColor(GameTheme.BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));

                g2d.dispose();
            }
        };
        section.setOpaque(false);
        section.setLayout(new BorderLayout());
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel chevron = new JLabel(expandedInitially ? "▼" : "▶");
        chevron.setFont(GameTheme.FONT_BUTTON);
        chevron.setForeground(GameTheme.PRIMARY);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameTheme.FONT_SUBHEADER);
        titleLabel.setForeground(GameTheme.TEXT_PRIMARY);

        JLabel hintLabel = new JLabel("Click to expand");
        hintLabel.setFont(GameTheme.FONT_SMALL);
        hintLabel.setForeground(GameTheme.TEXT_MUTED);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(chevron);
        leftHeader.add(iconLabel);
        leftHeader.add(titleLabel);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(hintLabel, BorderLayout.EAST);

        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.setOpaque(false);
        bodyWrapper.setBorder(BorderFactory.createEmptyBorder(0, 22, 20, 22));
        bodyWrapper.add(content, BorderLayout.CENTER);
        bodyWrapper.setVisible(expandedInitially);

        if (expandedInitially) {
            hintLabel.setText("Click to collapse");
        }

        MouseAdapter toggleListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean willExpand = !bodyWrapper.isVisible();
                bodyWrapper.setVisible(willExpand);
                chevron.setText(willExpand ? "▼" : "▶");
                hintLabel.setText(willExpand ? "Click to collapse" : "Click to expand");
                section.revalidate();
                contentPanel.revalidate();
                contentPanel.repaint();
                Container scrollParent = SwingUtilities.getAncestorOfClass(JViewport.class, contentPanel);
                if (scrollParent != null) {
                    scrollParent.revalidate();
                    scrollParent.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover[0] = true;
                section.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover[0] = false;
                section.repaint();
            }
        };

        header.addMouseListener(toggleListener);
        chevron.addMouseListener(toggleListener);
        iconLabel.addMouseListener(toggleListener);
        titleLabel.addMouseListener(toggleListener);
        hintLabel.addMouseListener(toggleListener);

        section.add(header, BorderLayout.NORTH);
        section.add(bodyWrapper, BorderLayout.CENTER);

        return section;
    }

   private JPanel createWhatSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        String text = "This app models a single bank teller serving customers one at a time. " +
                "For each customer, the simulation:";
        panel.add(createParagraph(text));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createBulletPoint("•", "Generates random inter-arrival times based on configuration limits"));
        panel.add(createBulletPoint("•", "Generates random service times based on configuration limits"));
        panel.add(createBulletPoint("•", "Tracks arrivals, waiting, service, and departures"));
        return panel;
    }

    private JPanel createHowToSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createStep("1", "Navigate to Simulation Tab", "Click 'Simulation' in the left menu"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("2", "Enter Random Numbers", "Generate automatically or input 100 values (0-1) per dataset"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("3", "Run Simulation", "Click the 'Run Simulation' button"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("4", "View Results", "Check 'Results' for the table and 'Analytics' for statistics"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("5", "Export Data", "Click 'Export to Excel' to save your results"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("6", "Start Fresh", "Use 'Clear Simulation' to reset everything when done"));

        return panel;
    }

    private JPanel createOutputsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createSubheader("Results Table Columns:"));
        panel.add(Box.createVerticalStrut(10));

        JPanel columnsGrid = new JPanel(new GridLayout(0, 2, 20, 8));
        columnsGrid.setOpaque(false);

        columnsGrid.add(createColumnExplainer("#", "Customer number"));
        columnsGrid.add(createColumnExplainer("IAT", "Time since last arrival"));
        columnsGrid.add(createColumnExplainer("Arrival", "When customer joined queue"));
        columnsGrid.add(createColumnExplainer("Service", "Time with teller"));
        columnsGrid.add(createColumnExplainer("Start", "When service began"));
        columnsGrid.add(createColumnExplainer("Wait", "Time spent waiting"));
        columnsGrid.add(createColumnExplainer("Departure", "When customer left"));
        columnsGrid.add(createColumnExplainer("Time in System", "Total time (wait + service)"));

        panel.add(columnsGrid);

        return panel;
    }

    private JPanel createInsightsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createInsightCard("1", "Arrival vs Service Rate",
                "When customers arrive faster than they can be served, queues build up quickly."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("2", "Single Server Bottleneck",
                "With one teller, any burst of arrivals forces later customers to wait."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("3", "High Utilization = Long Waits",
                "Server utilization near 100% means efficiency but also longer waiting times."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("4", "Variability Matters",
                "Same averages can produce different queues due to random variation."));

        return panel;
    }

    private JPanel createTipsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createTip("Run multiple simulations with different random numbers"));
        panel.add(createTip("Compare statistics across runs to see variability"));
        panel.add(createTip("Export results to Excel for deeper analysis"));
        panel.add(createTip("Watch for high wait times - they indicate bottlenecks"));
        panel.add(createTip("Use Clear Simulation when you're done to start over"));

        return panel;
    }

    private JPanel createParagraph(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("<html><p style='width: 600px'>" + text + "</p></html>");
        label.setFont(GameTheme.FONT_BODY);
        label.setForeground(GameTheme.TEXT_SECONDARY);

        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    private JPanel createBulletPoint(String icon, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(GameTheme.FONT_BODY);
        textLabel.setForeground(GameTheme.TEXT_PRIMARY);

        panel.add(iconLabel);
        panel.add(textLabel);

        return panel;
    }

    private JPanel createStep(String number, String title, String description) {
        JPanel panel = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel numberLabel = new JLabel(number) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.PRIMARY);
                g2d.fillOval(0, 0, 32, 32);
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(32, 32);
            }
        };
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setFont(GameTheme.FONT_BUTTON);
        numberLabel.setForeground(GameTheme.TEXT_PRIMARY);

        JPanel textPanel = new JPanel(new BorderLayout(0, 3));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameTheme.FONT_BUTTON);
        titleLabel.setForeground(GameTheme.TEXT_PRIMARY);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(GameTheme.FONT_SMALL);
        descLabel.setForeground(GameTheme.TEXT_MUTED);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);

        panel.add(numberLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createSubheader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(GameTheme.FONT_BUTTON);
        label.setForeground(GameTheme.TEXT_PRIMARY);
        return label;
    }

    private JPanel createColumnExplainer(String column, String description) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        JLabel colLabel = new JLabel(column);
        colLabel.setFont(GameTheme.FONT_BUTTON);
        colLabel.setForeground(GameTheme.PRIMARY);

        JLabel arrow = new JLabel("→");
        arrow.setForeground(GameTheme.TEXT_MUTED);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(GameTheme.FONT_SMALL);
        descLabel.setForeground(GameTheme.TEXT_SECONDARY);

        panel.add(colLabel);
        panel.add(arrow);
        panel.add(descLabel);

        return panel;
    }

    private JPanel createInsightCard(String icon, String title, String text) {
        JPanel card = new JPanel(new BorderLayout(12, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameTheme.FONT_BUTTON);
        titleLabel.setForeground(GameTheme.TEXT_PRIMARY);

        JLabel textLabel = new JLabel("<html><p style='width: 500px'>" + text + "</p></html>");
        textLabel.setFont(GameTheme.FONT_SMALL);
        textLabel.setForeground(GameTheme.TEXT_SECONDARY);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(textLabel, BorderLayout.CENTER);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTip(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel bullet = new JLabel("✓");
        bullet.setFont(GameTheme.FONT_BUTTON);
        bullet.setForeground(GameTheme.SUCCESS);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(GameTheme.FONT_BODY);
        textLabel.setForeground(GameTheme.TEXT_PRIMARY);

        panel.add(bullet);
        panel.add(textLabel);

        return panel;
    }
}
