package com.microlending;

import com.microlending.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use the system look and feel so the app matches the native OS style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to default Swing look and feel
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
