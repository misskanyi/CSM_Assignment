package com.banking.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Game-inspired theme for the Banking Queue Simulation.
 * Provides modern colors, styled components, and visual effects.
 */
public class GameTheme {

    // Color Palette - Modern Dark Theme with Vibrant Accents
    public static final Color BACKGROUND_DARK = new Color(15, 23, 42);
    public static final Color BACKGROUND_CARD = new Color(30, 41, 59);
    public static final Color BACKGROUND_ELEVATED = new Color(51, 65, 85);
    
    public static final Color PRIMARY = new Color(99, 102, 241);      // Indigo
    public static final Color PRIMARY_HOVER = new Color(129, 140, 248);
    public static final Color SECONDARY = new Color(16, 185, 129);    // Emerald
    public static final Color SECONDARY_HOVER = new Color(52, 211, 153);
    public static final Color ACCENT = new Color(251, 191, 36);       // Amber
    public static final Color DANGER = new Color(239, 68, 68);        // Red
    public static final Color SUCCESS = new Color(34, 197, 94);       // Green
    
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    
    public static final Color BORDER_COLOR = new Color(71, 85, 105);
    public static final Color GLOW_COLOR = new Color(99, 102, 241, 50);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Apply the game theme to the entire application.
     */
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        UIManager.put("Panel.background", BACKGROUND_DARK);
        UIManager.put("OptionPane.background", BACKGROUND_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", PRIMARY);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", BACKGROUND_ELEVATED);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("TextArea.background", BACKGROUND_ELEVATED);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground", TEXT_PRIMARY);
        UIManager.put("ScrollPane.background", BACKGROUND_DARK);
        UIManager.put("Viewport.background", BACKGROUND_DARK);
        UIManager.put("Table.background", BACKGROUND_CARD);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TableHeader.background", BACKGROUND_ELEVATED);
        UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.background", BACKGROUND_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected", BACKGROUND_CARD);
        UIManager.put("ComboBox.background", BACKGROUND_ELEVATED);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("List.background", BACKGROUND_CARD);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("FileChooser.background", BACKGROUND_CARD);
        UIManager.put("FileChooser.foreground", TEXT_PRIMARY);
    }

    /**
     * Creates a styled game button with hover effects.
     */
    public static JButton createGameButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            private boolean pressed = false;
            
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setForeground(TEXT_PRIMARY);
                setFont(FONT_BUTTON);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                    
                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw glow effect when hovering
                if (hover && !pressed) {
                    g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 40));
                    g2d.fill(new RoundRectangle2D.Float(-4, -4, width + 8, height + 8, 20, 20));
                }
                
                // Button background
                Color bgColor = pressed ? baseColor.darker() : (hover ? baseColor.brighter() : baseColor);
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 12, 12));
                
                // Top highlight for 3D effect
                if (!pressed) {
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, width, height / 2, 12, 12));
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(d.width + 40, 140), Math.max(d.height + 16, 44));
            }
        };
        
        return button;
    }

    /**
     * Creates a primary action button (indigo).
     */
    public static JButton createPrimaryButton(String text) {
        return createGameButton(text, PRIMARY);
    }

    /**
     * Creates a secondary action button (emerald).
     */
    public static JButton createSecondaryButton(String text) {
        return createGameButton(text, SECONDARY);
    }

    /**
     * Creates an accent button (amber).
     */
    public static JButton createAccentButton(String text) {
        return createGameButton(text, ACCENT);
    }

    /**
     * Creates a card-style panel with rounded corners and shadow effect.
     */
    public static JPanel createCard() {
        return new JPanel() {
            {
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                
                // Card background
                g2d.setColor(BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
                
                // Border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 16, 16));
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
    }

    /**
     * Creates a stat card for displaying metrics.
     */
    public static JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(BACKGROUND_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                
                // Left accent bar
                g2d.setColor(accentColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight(), 4, 4));
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(FONT_SMALL);
        labelComponent.setForeground(TEXT_SECONDARY);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(FONT_HEADER);
        valueComponent.setForeground(TEXT_PRIMARY);
        
        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setOpaque(false);
        textPanel.add(labelComponent, BorderLayout.NORTH);
        textPanel.add(valueComponent, BorderLayout.CENTER);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }

    /**
     * Creates a styled text area with game theme.
     */
    public static JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        area.setOpaque(false);
        area.setFont(FONT_MONO);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(PRIMARY);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }

    /**
     * Creates a styled scroll pane with custom scrollbar.
     */
    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        
        // Style scrollbars
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND_CARD);
        scrollPane.getHorizontalScrollBar().setBackground(BACKGROUND_CARD);
        
        return scrollPane;
    }

    /**
     * Creates a title label with game styling.
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Creates a header label.
     */
    public static JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADER);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Creates a section label with icon.
     */
    public static JPanel createSectionHeader(String icon, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(FONT_SUBHEADER);
        textLabel.setForeground(TEXT_PRIMARY);
        
        panel.add(iconLabel);
        panel.add(textLabel);
        
        return panel;
    }

    /**
     * Creates a gradient background panel.
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_DARK,
                    0, getHeight(), new Color(30, 27, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 2));
                for (int i = 0; i < getWidth(); i += 50) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
                
                g2d.dispose();
            }
        };
    }

    /**
     * Creates a progress bar with game styling.
     */
    public static JProgressBar createProgressBar() {
        JProgressBar bar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Background
                g2d.setColor(BACKGROUND_ELEVATED);
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, height, height));
                
                // Progress
                int progressWidth = (int) ((width - 4) * (getValue() / 100.0));
                if (progressWidth > 0) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY,
                        progressWidth, 0, SECONDARY
                    );
                    g2d.setPaint(gradient);
                    g2d.fill(new RoundRectangle2D.Float(2, 2, progressWidth, height - 4, height - 4, height - 4));
                }
                
                g2d.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(200, 12));
        return bar;
    }

    /**
     * Custom modern scrollbar UI.
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = BACKGROUND_ELEVATED;
            this.trackColor = BACKGROUND_CARD;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(PRIMARY.darker());
            g2d.fill(new RoundRectangle2D.Float(
                thumbBounds.x + 2, thumbBounds.y + 2,
                thumbBounds.width - 4, thumbBounds.height - 4,
                8, 8
            ));
            g2d.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(BACKGROUND_CARD);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    /**
     * Creates an animated loading indicator.
     */
    public static JPanel createLoadingIndicator(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        JProgressBar progress = createProgressBar();
        progress.setIndeterminate(true);
        
        JLabel label = new JLabel(message);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_SECONDARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(progress, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Shows a styled message dialog.
     */
    public static void showMessage(Component parent, String title, String message, int messageType) {
        UIManager.put("OptionPane.background", BACKGROUND_CARD);
        UIManager.put("Panel.background", BACKGROUND_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        
        String icon = switch (messageType) {
            case JOptionPane.INFORMATION_MESSAGE -> "✅ ";
            case JOptionPane.WARNING_MESSAGE -> "⚠️ ";
            case JOptionPane.ERROR_MESSAGE -> "❌ ";
            default -> "ℹ️ ";
        };
        
        JOptionPane.showMessageDialog(parent, message, icon + title, messageType);
    }
}
