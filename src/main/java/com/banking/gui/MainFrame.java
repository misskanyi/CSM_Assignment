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

public class MainFrame extends JFrame {

    private final ConfigurationPanel configurationPanel;
    private final InputPanel inputPanel;
    private final TableOutputPanel tableOutputPanel;
    private final StatisticsOutputPanel statisticsOutputPanel;
    private final QueueSimulator simulator = new QueueSimulator();

    private List<Customer> lastResults;
    private QueueStatistics lastStatistics;
    
    private JPanel navPanel;
    private int selectedTab = 0;
    private final String[] tabIcons = {"📖", "⚙️", "🎮", "📊", "📈"};
    private final String[] tabNames = {"Guide", "Configuration", "Simulation", "Results", "Analytics"};
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        super("Banking Queue Simulator");
        GameTheme.applyTheme();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);
        
        JPanel mainContainer = GameTheme.createGradientPanel();
        mainContainer.setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        navPanel = createNavigationPanel();
        
        configurationPanel = new ConfigurationPanel();
        inputPanel = new InputPanel();
        tableOutputPanel = new TableOutputPanel();
        statisticsOutputPanel = new StatisticsOutputPanel();
        InstructionsPanel instructionsPanel = new InstructionsPanel();

        inputPanel.setSimulationListener(this::runSimulation);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(instructionsPanel, "guide");
        contentPanel.add(configurationPanel, "configuration");
        contentPanel.add(inputPanel, "simulation");
        contentPanel.add(tableOutputPanel, "results");
        contentPanel.add(statisticsOutputPanel, "analytics");
        
        JPanel paddedContent = new JPanel(new BorderLayout());
        paddedContent.setOpaque(false);
        paddedContent.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        paddedContent.add(contentPanel, BorderLayout.CENTER);
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(navPanel, BorderLayout.WEST);
        mainContainer.add(paddedContent, BorderLayout.CENTER);
        
        setContentPane(mainContainer);
        selectTab(0);
    }

    public int getConfiguredCustomerCount() {
        try {
            return configurationPanel.getCustomerCount();
        } catch (Exception e) {
            return 100;
        }
    }
    
    private void selectTab(int index) {
        selectedTab = index;
        String[] cardNames = {"guide", "configuration", "simulation", "results", "analytics"};
        cardLayout.show(contentPanel, cardNames[index]);
        navPanel.repaint();
    }

    private void runSimulation(double[] iatRandoms, double[] serviceRandoms) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            simulator.setCustomerCount(configurationPanel.getCustomerCount());
            simulator.setIatMin(configurationPanel.getArrivalMin());
            simulator.setIatMax(configurationPanel.getArrivalMax());
            simulator.setServiceMin(configurationPanel.getServiceMin());
            simulator.setServiceMax(configurationPanel.getServiceMax());
        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            GameTheme.showMessage(this, "Configuration Error",
                    "Please double check your parameters are numeric variables.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                double[] interArrivalTimes = simulator.generateInterArrivalTimes(iatRandoms);
                double[] serviceTimes = simulator.generateServiceTimes(serviceRandoms);
                lastResults = simulator.simulate(interArrivalTimes, serviceTimes);
                lastStatistics = simulator.computeStatistics(lastResults);
                return null;
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                tableOutputPanel.displayResults(lastResults);
                statisticsOutputPanel.displayStatistics(lastStatistics);
                showSuccessDialog();
            }
        };
        worker.execute();
    }

    private void clearSimulation() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Reset all configuration variables, fields and simulation history?\nThis action is final.",
                "Clear Simulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        lastResults = null;
        lastStatistics = null;
        configurationPanel.resetToDefaults();
        inputPanel.clear();
        tableOutputPanel.clear();
        statisticsOutputPanel.clear();
        selectTab(1);

        GameTheme.showMessage(this, "Cleared",
                "Simulation reset. You can start a new run from the Configuration tab.",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, GameTheme.BACKGROUND_CARD,
                    getWidth(), 0, new Color(30, 27, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(GameTheme.PRIMARY);
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        
        JLabel icon = new JLabel("🏦");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel title = new JLabel("Queue Simulator");
        title.setFont(GameTheme.FONT_TITLE);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        
        titlePanel.add(icon);
        titlePanel.add(title);
        
        JButton exportBtn = GameTheme.createAccentButton("📁 Export to Excel");
        exportBtn.addActionListener(e -> exportToExcel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
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
        nav.setPreferredSize(new Dimension(220, 0));
        
        for (int i = 0; i < tabNames.length; i++) {
            nav.add(createNavButton(i));
            nav.add(Box.createVerticalStrut(8));
        }
        
        nav.add(Box.createVerticalGlue());
        return nav;
    }
    
    private JPanel createNavButton(int index) {
        JPanel button = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (selectedTab == index) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, GameTheme.PRIMARY,
                        getWidth(), 0, GameTheme.PRIMARY.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else if (getMousePosition() != null) {
                    g2d.setColor(GameTheme.BACKGROUND_ELEVATED);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                
                g2d.dispose();
            }
        };
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setMaximumSize(new Dimension(200, 50));
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

    private void exportToExcel() {
        if (lastResults == null || lastStatistics == null) {
            GameTheme.showMessage(this, "No Results", "Run a simulation first before exporting!", JOptionPane.WARNING_MESSAGE);
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

    private void showSuccessDialog() {
        GameTheme.showMessage(this, "Simulation Complete!", 
                "The customers have been processed. Check the Results and Analytics tabs!", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}