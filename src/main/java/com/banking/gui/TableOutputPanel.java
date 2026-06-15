package com.banking.gui;

import com.banking.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Game-styled output panel displaying customer simulation results in a table.
 */
public class TableOutputPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private JLabel recordCountLabel;

    public TableOutputPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title section
        JPanel titlePanel = createTitleSection();

        // Table columns
        String[] columns = {
                "#", "IAT", "Arrival", "Service", "Start", "Wait", "Departure", "Time in System"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (isRowSelected(row)) {
                    c.setBackground(GameTheme.PRIMARY);
                    c.setForeground(GameTheme.TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? GameTheme.BACKGROUND_CARD : GameTheme.BACKGROUND_ELEVATED);
                    c.setForeground(GameTheme.TEXT_PRIMARY);
                }
                
                // Highlight wait time column
                if (column == 5 && !isRowSelected(row)) {
                    try {
                        double wait = Double.parseDouble(getValueAt(row, column).toString());
                        if (wait > 0) {
                            c.setForeground(GameTheme.ACCENT);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                
                return c;
            }
        };
        
        styleTable();

        // Card container for table
        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 20, 20));
                
                // Card
                g2d.setColor(GameTheme.BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                
                g2d.dispose();
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(GameTheme.BORDER_COLOR, 1, true));
        scrollPane.getVerticalScrollBar().setBackground(GameTheme.BACKGROUND_CARD);
        scrollPane.getHorizontalScrollBar().setBackground(GameTheme.BACKGROUND_CARD);
        
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }
    
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        
        // Title with icon
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);
        
        JLabel icon = new JLabel("📊");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel title = new JLabel("Simulation Results");
        title.setFont(GameTheme.FONT_HEADER);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        
        titleRow.add(icon);
        titleRow.add(title);
        
        // Record count badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgePanel.setOpaque(false);
        
        recordCountLabel = new JLabel("0 customers") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GameTheme.PRIMARY);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        recordCountLabel.setFont(GameTheme.FONT_BUTTON);
        recordCountLabel.setForeground(GameTheme.TEXT_PRIMARY);
        recordCountLabel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        badgePanel.add(recordCountLabel);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        legendPanel.add(createLegendItem("🟡", "Waited", GameTheme.ACCENT));
        legendPanel.add(createLegendItem("🟢", "No Wait", GameTheme.SUCCESS));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleRow, BorderLayout.WEST);
        topPanel.add(badgePanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLegendItem(String icon, String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);
        
        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(GameTheme.FONT_SMALL);
        
        JLabel label = new JLabel(text);
        label.setFont(GameTheme.FONT_SMALL);
        label.setForeground(GameTheme.TEXT_SECONDARY);
        
        item.add(dot);
        item.add(label);
        
        return item;
    }
    
    private void styleTable() {
        table.setOpaque(false);
        table.setBackground(GameTheme.BACKGROUND_CARD);
        table.setForeground(GameTheme.TEXT_PRIMARY);
        table.setFont(GameTheme.FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(GameTheme.PRIMARY);
        table.setSelectionForeground(GameTheme.TEXT_PRIMARY);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(GameTheme.BACKGROUND_ELEVATED);
        header.setForeground(GameTheme.TEXT_PRIMARY);
        header.setFont(GameTheme.FONT_BUTTON);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GameTheme.PRIMARY));
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(GameTheme.BACKGROUND_ELEVATED);
                label.setForeground(GameTheme.TEXT_PRIMARY);
                label.setFont(GameTheme.FONT_BUTTON);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return label;
            }
        });
        
        // Center all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // #
        table.getColumnModel().getColumn(0).setMaxWidth(80);
    }

    public void displayResults(List<Customer> customers) {
        tableModel.setRowCount(0);
        
        for (Customer c : customers) {
            String waitValue = format(c.getWaitingTime());
            
            tableModel.addRow(new Object[]{
                    c.getCustomerNumber(),
                    format(c.getInterArrivalTime()),
                    format(c.getArrivalTime()),
                    format(c.getServiceTime()),
                    format(c.getServiceStartTime()),
                    waitValue,
                    format(c.getDepartureTime()),
                    format(c.getTimeInSystem())
            });
        }
        
        recordCountLabel.setText(customers.size() + " customers");
        recordCountLabel.repaint();
    }

    public void clear() {
        tableModel.setRowCount(0);
        recordCountLabel.setText("0 customers");
        recordCountLabel.repaint();
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }
}
