package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.model.Circle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CircleFrame extends JFrame {

    private final DashboardFrame parent;
    private JTextField nameField;
    private JTextField amountField;
    private DefaultTableModel tableModel;
    private JTable circleTable;

    public CircleFrame(DashboardFrame parent) {
        this.parent = parent;

        setTitle("Manage Circles");
        setSize(560, 480);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(buildFormPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(), BorderLayout.CENTER);

        add(mainPanel);
        loadCircles();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Create New Circle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Circle Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Monthly Amount (\u20B9):"), gbc);
        amountField = new JTextField(15);
        gbc.gridx = 1;
        form.add(amountField, gbc);

        JButton createBtn = new JButton("Create Circle");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(createBtn, gbc);

        createBtn.addActionListener(e -> createCircle());

        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Existing Circles"));

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Monthly Amount", "Start Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        circleTable = new JTable(tableModel);
        panel.add(new JScrollPane(circleTable), BorderLayout.CENTER);

        return panel;
    }

    private void createCircle() {
        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();

        if (name.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monthly amount must be a valid number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Circle circle = new Circle();
        circle.setName(name);
        circle.setMonthlyAmount(amount);
        circle.setStartDate(Date.valueOf(LocalDate.now()));
        circle.setStatus("ACTIVE");

        try {
            CircleDAO circleDAO = new CircleDAO();
            if (circleDAO.createCircle(circle)) {
                JOptionPane.showMessageDialog(this, "Circle created successfully.");
                nameField.setText("");
                amountField.setText("");
                loadCircles();
                parent.refreshStats();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not create circle.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCircles() {
        tableModel.setRowCount(0);
        try {
            CircleDAO circleDAO = new CircleDAO();
            List<Circle> circles = circleDAO.getAllCircles();
            for (Circle c : circles) {
                tableModel.addRow(new Object[]{
                        c.getId(), c.getName(), "\u20B9" + c.getMonthlyAmount().toPlainString(),
                        c.getStartDate(), c.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load circles.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
