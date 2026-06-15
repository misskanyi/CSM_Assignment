package com.banking.gui;

import com.banking.excel.ExcelHandler;
import com.banking.model.Customer;
import com.banking.model.QueueStatistics;
import com.banking.simulation.QueueSimulator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.List;

/**
 * Main application window with game-inspired UI design.
 */
public class MainFrame extends JFrame {

    private final InputPanel inputPanel;
    private final TableOutputPanel tableOutputPanel;
    private final StatisticsOutputPanel statisticsOutputPanel;
    private final QueueSimulator simulator = new QueueSimulator();

    private List<Customer> lastResults;
    private QueueStatistics lastStatistics;
    
    private JPanel navPanel;
    private int selectedTab = 0;
    private final String[] tabIcons = {"📖", "🎮", "📊", "📈"};
    private final String[] tabNames = {"Guide", "Simulation", "Results", "Analytics"};
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        super("Banking Queue Simulator");
        GameTheme.applyTheme();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);
        
        // Create main container with gradient background
        JPanel mainContainer = GameTheme.createGradientPanel();
        mainContainer.setLayout(new BorderLayout());
        
        // Header panel with title and export button
        JPanel headerPanel = createHeaderPanel();
        
        // Navigation panel (left sidebar)
        navPanel = createNavigationPanel();
        
        // Content panels
        inputPanel = new InputPanel();
        tableOutputPanel = new TableOutputPanel();
        statisticsOutputPanel = new StatisticsOutputPanel();
        InstructionsPanel instructionsPanel = new InstructionsPanel();

        inputPanel.setSimulationListener(this::runSimulation);

        // Card layout for content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(instructionsPanel, "guide");
        contentPanel.add(inputPanel, "simulation");
        contentPanel.add(tableOutputPanel, "results");
        contentPanel.add(statisticsOutputPanel, "analytics");
        
        // Add padding around content
        JPanel paddedContent = new JPanel(new BorderLayout());
        paddedContent.setOpaque(false);
        paddedContent.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        paddedContent.add(contentPanel, BorderLayout.CENTER);
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(navPanel, BorderLayout.WEST);
        mainContainer.add(paddedContent, BorderLayout.CENTER);
        
        setContentPane(mainContainer);
        
        // Show first tab
        selectTab(0);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, GameTheme.BACKGROUND_CARD,
                    getWidth(), 0, new Color(30, 27, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Bottom border glow
                g2d.setColor(GameTheme.PRIMARY);
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        
        JLabel icon = new JLabel("🏦");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel title = new JLabel("Queue Simulator");
        title.setFont(GameTheme.FONT_TITLE);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        
        JLabel subtitle = new JLabel("  •  Banking System Simulation");
        subtitle.setFont(GameTheme.FONT_BODY);
        subtitle.setForeground(GameTheme.TEXT_SECONDARY);
        
        titlePanel.add(icon);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        JButton clearBtn = GameTheme.createGameButton("🔄 Clear Simulation", GameTheme.DANGER);
        clearBtn.addActionListener(e -> clearSimulation());

        JButton exportBtn = GameTheme.createAccentButton("📁 Export to Excel");
        exportBtn.addActionListener(e -> exportToExcel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(clearBtn);
        buttonPanel.add(exportBtn);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createNavigationPanel() {
        JPanel nav = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        nav.setPreferredSize(new Dimension(180, 0));
        
        for (int i = 0; i < tabNames.length; i++) {
            nav.add(createNavButton(i));
            nav.add(Box.createVerticalStrut(8));
        }
        
        nav.add(Box.createVerticalGlue());
        
        // Status indicator
        JPanel statusPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        statusPanel.setMaximumSize(new Dimension(160, 80));
        
        JLabel statusIcon = new JLabel("💡");
        statusIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        JLabel statusText = new JLabel("<html><b>Pro Tip</b><br><span style='color:#94a3b8'>Run multiple simulations!</span></html>");
        statusText.setFont(GameTheme.FONT_SMALL);
        statusText.setForeground(GameTheme.TEXT_PRIMARY);
        
        statusPanel.add(statusIcon, BorderLayout.WEST);
        statusPanel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
        statusPanel.add(statusText, BorderLayout.CENTER);
        
        nav.add(statusPanel);
        
        return nav;
    }
    
    private JPanel createNavButton(int index) {
        JPanel button = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (selectedTab == index) {
                    // Selected state
                    GradientPaint gradient = new GradientPaint(
                        0, 0, GameTheme.PRIMARY,
                        getWidth(), 0, GameTheme.PRIMARY.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else if (getMousePosition() != null) {
                    // Hover state
                    g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                
                g2d.dispose();
            }
        };
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setMaximumSize(new Dimension(160, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(tabIcons[index]);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        
        JLabel textLabel = new JLabel(tabNames[index]);
        textLabel.setFont(GameTheme.FONT_BUTTON);
        textLabel.setForeground(GameTheme.TEXT_PRIMARY);
        
        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        content.setOpaque(false);
        content.add(iconLabel);
        content.add(textLabel);
        
        button.add(content, BorderLayout.CENTER);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTab(index);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void selectTab(int index) {
        selectedTab = index;
        String[] cardNames = {"guide", "simulation", "results", "analytics"};
        cardLayout.show(contentPanel, cardNames[index]);
        navPanel.repaint();
    }

    private void runSimulation(double[] iatRandoms, double[] serviceRandoms) {
        // Show loading state
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                double[] interArrivalTimes = QueueSimulator.generateInterArrivalTimes(iatRandoms);
                double[] serviceTimes = QueueSimulator.generateServiceTimes(serviceRandoms);
                lastResults = simulator.simulate(interArrivalTimes, serviceTimes);
                lastStatistics = simulator.computeStatistics(lastResults);
                return null;
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                tableOutputPanel.displayResults(lastResults);
                statisticsOutputPanel.displayStatistics(lastStatistics);
                
                // Show success dialog with game-like styling
                showSuccessDialog();
            }
        };
        worker.execute();
    }
    
    private void showSuccessDialog() {
        JDialog dialog = new JDialog(this, "Simulation Complete", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(GameTheme.PRIMARY);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 20, 20));
                g2d.dispose();
            }
        };
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Success icon
        JLabel icon = new JLabel("🎉", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        // Title
        JLabel title = new JLabel("Simulation Complete!", SwingConstants.CENTER);
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.SUCCESS);
        
        // Message
        JLabel message = new JLabel("<html><center>100 customers have been processed.<br>Check the Results and Analytics tabs!</center></html>", SwingConstants.CENTER);
        message.setFont(GameTheme.FONT_BODY);
        message.setForeground(GameTheme.TEXT_SECONDARY);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton resultsBtn = GameTheme.createPrimaryButton("📊 View Results");
        resultsBtn.addActionListener(e -> {
            dialog.dispose();
            selectTab(2);
        });
        
        JButton analyticsBtn = GameTheme.createSecondaryButton("📈 View Analytics");
        analyticsBtn.addActionListener(e -> {
            dialog.dispose();
            selectTab(3);
        });
        
        buttonPanel.add(resultsBtn);
        buttonPanel.add(analyticsBtn);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(icon);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(message);
        
        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(content);
        dialog.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);
    }

    private void clearSimulation() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Clear all input data and simulation results?\nThis cannot be undone.",
                "Clear Simulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        lastResults = null;
        lastStatistics = null;
        inputPanel.clear();
        tableOutputPanel.clear();
        statisticsOutputPanel.clear();
        selectTab(1);

        GameTheme.showMessage(this, "Cleared",
                "Simulation reset. You can start a new run from the Simulation tab.",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToExcel() {
        if (lastResults == null || lastStatistics == null) {
            JDialog dialog = new JDialog(this, "No Results", true);
            dialog.setUndecorated(true);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(this);
            
            JPanel content = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(GameTheme.BACKGROUND_CARD);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    g2d.setColor(GameTheme.ACCENT);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 20, 20));
                    g2d.dispose();
                }
            };
            content.setOpaque(false);
            content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            
            JLabel icon = new JLabel("⚠️", SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            
            JLabel message = new JLabel("<html><center>Run a simulation first<br>before exporting!</center></html>", SwingConstants.CENTER);
            message.setFont(GameTheme.FONT_BODY);
            message.setForeground(GameTheme.TEXT_PRIMARY);
            
            JButton okBtn = GameTheme.createAccentButton("Got it!");
            okBtn.addActionListener(e -> dialog.dispose());
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.add(icon);
            centerPanel.add(Box.createVerticalStrut(15));
            centerPanel.add(message);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setOpaque(false);
            btnPanel.add(okBtn);
            
            content.add(centerPanel, BorderLayout.CENTER);
            content.add(btnPanel, BorderLayout.SOUTH);
            
            dialog.setContentPane(content);
            dialog.setBackground(new Color(0, 0, 0, 0));
            dialog.setVisible(true);
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
            GameTheme.showMessage(this, "Export Complete",
                    "Results exported successfully to:\n" + path,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            GameTheme.showMessage(this, "Export Error",
                    "Error exporting to Excel:\n" + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
