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

        panel.add(createParagraph(
                "This app models a single bank teller serving customers one at a time. " +
                "You control how many customers to simulate and the time distribution bounds. " +
                "For each customer the simulation:"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createBulletPoint("•", "Generates random inter-arrival times using Uniform(lower, upper)"));
        panel.add(createBulletPoint("•", "Generates random service times using Uniform(lower, upper)"));
        panel.add(createBulletPoint("•", "Tracks arrivals, waiting, service start, and departure"));
        panel.add(createBulletPoint("•", "Computes per-customer and aggregate statistics"));

        return panel;
    }

    private JPanel createHowToSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createSubheader("Quick way — Configuration tab:"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("1", "Open Configuration", "Click 'Configuration' in the left menu"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("2", "Set Parameters", "Enter number of customers, arrival bounds, and service bounds"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("3", "Run Simulation", "Click 'Run Simulation' — random numbers are generated for you"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("4", "View Results", "You are taken to the Results tab automatically"));

        panel.add(Box.createVerticalStrut(20));
        panel.add(createSubheader("Advanced way — Simulation tab:"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("1", "Open Simulation", "Click 'Simulation' in the left menu"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("2", "Enter Random Numbers", "Paste 100 values (0 to 1) per dataset, or click 'Generate Random'"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("3", "Load from Excel", "Alternatively load your own numbers via 'Load Excel'"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("4", "Run & Review", "Click 'Run Simulation', then check Results and Analytics tabs"));

        panel.add(Box.createVerticalStrut(20));
        panel.add(createSubheader("Exporting & resetting:"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStep("5", "Export to Excel", "Click 'Export to Excel' in the top-right header to save results"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStep("6", "Start Fresh", "Click 'Clear Simulation' in the header to reset everything"));

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
        columnsGrid.add(createColumnExplainer("IAT", "Time since previous customer arrived"));
        columnsGrid.add(createColumnExplainer("Arrival", "When customer joined the queue"));
        columnsGrid.add(createColumnExplainer("Service", "Duration of service at the teller"));
        columnsGrid.add(createColumnExplainer("Start", "When service actually began"));
        columnsGrid.add(createColumnExplainer("Wait", "Time spent waiting before service"));
        columnsGrid.add(createColumnExplainer("Departure", "When customer left the bank"));
        columnsGrid.add(createColumnExplainer("Time in System", "Total time = wait + service"));

        panel.add(columnsGrid);
        panel.add(Box.createVerticalStrut(18));

        panel.add(createSubheader("Row colour coding:"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createBulletPoint("—", "Green row: customer was served immediately, no waiting time"));
        panel.add(createBulletPoint("—", "Yellow row: customer had to wait in the queue"));

        panel.add(Box.createVerticalStrut(18));
        panel.add(createSubheader("Analytics Tab shows:"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createBulletPoint("•", "Average waiting time, service time, and time in system"));
        panel.add(createBulletPoint("•", "Server utilisation and idle time"));
        panel.add(createBulletPoint("•", "Probability a customer waits, average queue length, max queue"));

        return panel;
    }

    private JPanel createInsightsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createInsightCard("1", "Arrival vs Service Rate",
                "When customers arrive faster than they are served, queues build up. Narrow the gap between arrival upper bound and service upper bound to observe this."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("2", "Single Server Bottleneck",
                "With one teller, any burst of arrivals forces later customers to wait. Yellow rows show where backlog accumulates."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("3", "High Utilisation = Long Waits",
                "Server utilisation near 100% means near-constant service but also longer queues for arriving customers."));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createInsightCard("4", "Bounds Drive the Outcome",
                "Changing the lower and upper bounds in Configuration directly shifts average arrival and service times, letting you model fast vs slow scenarios."));

        return panel;
    }

    private JPanel createTipsSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createTip("Use Configuration for quick experiments — no manual number entry needed"));
        panel.add(createTip("Try different customer counts (e.g. 20 vs 200) to see how queue behaviour scales"));
        panel.add(createTip("Widen service bounds to simulate an unpredictable teller"));
        panel.add(createTip("Yellow rows in Results show which customers were impacted by the queue"));
        panel.add(createTip("Export to Excel after each run to compare results side by side"));
        panel.add(createTip("Use Clear Simulation to reset before starting a new experiment"));

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
