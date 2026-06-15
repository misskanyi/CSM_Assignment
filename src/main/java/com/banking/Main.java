package com.banking;

import com.banking.gui.GameTheme;
import com.banking.gui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point for the Banking Queue Simulation application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Apply game theme
            GameTheme.applyTheme();
            
            // Enable anti-aliasing for text
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            // Create and show splash screen briefly
            JWindow splash = createSplashScreen();
            splash.setVisible(true);
            
            // Create main frame while splash is showing
            MainFrame frame = new MainFrame();
            frame.setSize(1200, 800);
            
            // Hide splash and show main frame
            Timer timer = new Timer(1500, e -> {
                splash.dispose();
                frame.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    private static JWindow createSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(450, 300);
        splash.setLocationRelativeTo(null);
        
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, GameTheme.BACKGROUND_DARK,
                    0, getHeight(), new Color(30, 27, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Border
                g2d.setColor(GameTheme.PRIMARY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                
                g2d.dispose();
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Icon
        JLabel icon = new JLabel("🏦", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        
        // Title
        JLabel title = new JLabel("Queue Simulator", SwingConstants.CENTER);
        title.setFont(GameTheme.FONT_TITLE);
        title.setForeground(GameTheme.TEXT_PRIMARY);
        
        // Subtitle
        JLabel subtitle = new JLabel("Banking System Simulation", SwingConstants.CENTER);
        subtitle.setFont(GameTheme.FONT_BODY);
        subtitle.setForeground(GameTheme.TEXT_SECONDARY);
        
        // Loading indicator
        JProgressBar progress = GameTheme.createProgressBar();
        progress.setIndeterminate(true);
        
        JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
        loading.setFont(GameTheme.FONT_SMALL);
        loading.setForeground(GameTheme.TEXT_MUTED);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(icon);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(progress);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(loading);
        
        content.add(centerPanel, BorderLayout.CENTER);
        
        splash.setContentPane(content);
        splash.setBackground(new Color(0, 0, 0, 0));
        
        return splash;
    }
}
