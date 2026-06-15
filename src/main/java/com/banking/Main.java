package com.banking;

import com.banking.gui.MainFrame;

import javax.swing.*;

/**
 * Entry point for the Banking Queue Simulation application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            MainFrame frame = new MainFrame();
            frame.setSize(1100, 750);
            frame.setVisible(true);
        });
    }
}
