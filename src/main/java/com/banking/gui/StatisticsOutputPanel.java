package com.banking.gui;

import com.banking.model.QueueStatistics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Game-styled dashboard displaying queue statistics with visual cards.
 */
public class StatisticsOutputPanel extends JPanel {

    private final Runnable onClear;
    private final StatCard totalCustomersCard;
    private final StatCard avgWaitingCard;
    private final StatCard avgServiceCard;
    private final StatCard avgIatCard;
    private final StatCard avgTimeInSystemCard;
    private final StatCard customersWaitedCard;
    private final StatCard totalSimTimeCard;
    private final StatCard totalIdleTimeCard;
    private final StatCard avgQueueLengthCard;
    private final StatCard maxQueueLengthCard;
    
    private final GaugePanel serverUtilGauge;
    private final GaugePanel probWaitingGauge;

    public StatisticsOutputPanel(Runnable onClear) {
        this.onClear = onClear;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title section
        JPanel titlePanel = createTitleSection();

        // Create stat cards
        totalCustomersCard = new StatCard("👥", "Total Customers", "—", GameTheme.PRIMARY);
        avgWaitingCard = new StatCard("⏳", "Avg Wait Time", "— min", GameTheme.ACCENT);
        avgServiceCard = new StatCard("🔧", "Avg Service Time", "— min", GameTheme.SECONDARY);
        avgIatCard = new StatCard("🕐", "Avg Inter-Arrival", "— min", GameTheme.PRIMARY);
        avgTimeInSystemCard = new StatCard("⏱️", "Avg Time in System", "— min", GameTheme.DANGER);
        customersWaitedCard = new StatCard("🚶", "Customers Waited", "—", GameTheme.ACCENT);
        totalSimTimeCard = new StatCard("📊", "Total Sim Time", "— min", GameTheme.PRIMARY);
        totalIdleTimeCard = new StatCard("💤", "Total Idle Time", "— min", GameTheme.TEXT_MUTED);
        avgQueueLengthCard = new StatCard("📏", "Avg Queue Length", "—", GameTheme.SECONDARY);
        maxQueueLengthCard = new StatCard("📈", "Max Queue Length", "—", GameTheme.DANGER);
        
        // Create gauges
        serverUtilGauge = new GaugePanel("Server Utilization", 0, GameTheme.PRIMARY);
        probWaitingGauge = new GaugePanel("Probability of Waiting", 0, GameTheme.ACCENT);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        
        // Top section - Key metrics with gauges
        JPanel topSection = new JPanel(new GridLayout(1, 2, 20, 0));
        topSection.setOpaque(false);
        topSection.add(serverUtilGauge);
        topSection.add(probWaitingGauge);
        
        // Grid of stat cards
        JPanel statsGrid = new JPanel(new GridLayout(2, 5, 15, 15));
        statsGrid.setOpaque(false);
        
        statsGrid.add(totalCustomersCard);
        statsGrid.add(avgWaitingCard);
        statsGrid.add(avgServiceCard);
        statsGrid.add(avgIatCard);
        statsGrid.add(avgTimeInSystemCard);
        statsGrid.add(customersWaitedCard);
        statsGrid.add(totalSimTimeCard);
        statsGrid.add(totalIdleTimeCard);
        statsGrid.add(avgQueueLengthCard);
        statsGrid.add(maxQueueLengthCard);
        
        contentPanel.add(topSection, BorderLayout.NORTH);
        contentPanel.add(statsGrid, BorderLayout.CENTER);
        
        // Wrap in scrollable container
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("📈");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel title = new JLabel("Analytics Dashboard");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("  •  Queue Performance Metrics");
        subtitle.setFont(GameTheme.FONT_BODY);
        subtitle.setForeground(GameTheme.TEXT_SECONDARY);

        left.add(icon);
        left.add(title);
        left.add(subtitle);

        JButton clearBtn = GameTheme.createGameButton("Clear", GameTheme.DANGER);
        clearBtn.addActionListener(e -> onClear.run());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(clearBtn);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    public void displayStatistics(QueueStatistics stats) {
        totalCustomersCard.setValue(String.valueOf(stats.getTotalCustomers()));
        avgWaitingCard.setValue(format(stats.getAverageWaitingTime()) + " min");
        avgServiceCard.setValue(format(stats.getAverageServiceTime()) + " min");
        avgIatCard.setValue(format(stats.getAverageInterArrivalTime()) + " min");
        avgTimeInSystemCard.setValue(format(stats.getAverageTimeInSystem()) + " min");
        customersWaitedCard.setValue(stats.getCustomersWhoWaited() + " / " + stats.getTotalCustomers());
        totalSimTimeCard.setValue(format(stats.getTotalSimulationTime()) + " min");
        totalIdleTimeCard.setValue(format(stats.getTotalIdleTime()) + " min");
        avgQueueLengthCard.setValue(format(stats.getAverageQueueLength()));
        maxQueueLengthCard.setValue(String.valueOf(stats.getMaxQueueLength()));
        
        serverUtilGauge.setValue(stats.getServerUtilization() * 100);
        probWaitingGauge.setValue(stats.getProbabilityOfWaiting() * 100);
    }

    public void clear() {
        totalCustomersCard.setValue("—");
        avgWaitingCard.setValue("— min");
        avgServiceCard.setValue("— min");
        avgIatCard.setValue("— min");
        avgTimeInSystemCard.setValue("— min");
        customersWaitedCard.setValue("—");
        totalSimTimeCard.setValue("— min");
        totalIdleTimeCard.setValue("— min");
        avgQueueLengthCard.setValue("—");
        maxQueueLengthCard.setValue("—");
        
        serverUtilGauge.setValue(0);
        probWaitingGauge.setValue(0);
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }
    
    /**
     * Custom stat card component.
     */
    private static class StatCard extends JPanel {
        private final JLabel valueLabel;
        private final Color accentColor;
        
        public StatCard(String icon, String title, String value, Color accent) {
            this.accentColor = accent;
            setOpaque(false);
            setLayout(new BorderLayout(10, 8));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Icon
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            
            // Title
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(GameTheme.FONT_SMALL);
            titleLabel.setForeground(GameTheme.TEXT_SECONDARY);
            
            // Value
            valueLabel = new JLabel(value);
            valueLabel.setFont(GameTheme.FONT_SUBHEADER);
            valueLabel.setForeground(GameTheme.TEXT_PRIMARY);
            
            JPanel textPanel = new JPanel(new BorderLayout(0, 5));
            textPanel.setOpaque(false);
            textPanel.add(titleLabel, BorderLayout.NORTH);
            textPanel.add(valueLabel, BorderLayout.CENTER);
            
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            topRow.add(iconLabel, BorderLayout.WEST);
            
            add(topRow, BorderLayout.NORTH);
            add(textPanel, BorderLayout.CENTER);
        }
        
        public void setValue(String value) {
            valueLabel.setText(value);
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 16, 16));
            
            // Background
            g2d.setColor(GameTheme.BACKGROUND_CARD);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));
            
            // Left accent bar
            g2d.setColor(accentColor);
            g2d.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight() - 3, 4, 4));
            
            g2d.dispose();
            super.paintComponent(g);
        }
    }
    
    /**
     * Circular gauge component for percentages.
     */
    private static class GaugePanel extends JPanel {
        private final String title;
        private double value;
        private final Color color;
        
        public GaugePanel(String title, double value, Color color) {
            this.title = title;
            this.value = value;
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(200, 220));
        }
        
        public void setValue(double value) {
            this.value = value;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Card background
            g2d.setColor(GameTheme.BACKGROUND_CARD);
            g2d.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
            
            // Gauge dimensions
            int size = Math.min(w, h - 60) - 40;
            int x = (w - size) / 2;
            int y = 30;
            int thickness = 12;
            
            // Background arc
            g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.OPEN));
            
            // Value arc
            double angle = (value / 100.0) * 360;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(new Arc2D.Double(x, y, size, size, 90, -angle, Arc2D.OPEN));
            
            // Glow effect
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            g2d.setStroke(new BasicStroke(thickness + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(new Arc2D.Double(x, y, size, size, 90, -angle, Arc2D.OPEN));
            
            // Value text
            String valueText = String.format("%.1f%%", value);
            g2d.setFont(GameTheme.FONT_HEADER);
            g2d.setColor(GameTheme.TEXT_PRIMARY);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (size - fm.stringWidth(valueText)) / 2;
            int textY = y + size / 2 + fm.getAscent() / 2 - 5;
            g2d.drawString(valueText, textX, textY);
            
            // Title text
            g2d.setFont(GameTheme.FONT_BODY);
            g2d.setColor(GameTheme.TEXT_SECONDARY);
            fm = g2d.getFontMetrics();
            textX = (w - fm.stringWidth(title)) / 2;
            textY = y + size + 35;
            g2d.drawString(title, textX, textY);
            
            g2d.dispose();
        }
    }
}
