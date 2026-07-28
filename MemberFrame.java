package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.dao.MemberDAO;
import com.microlending.model.Circle;
import com.microlending.model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

public class MemberFrame extends JFrame {

    private final DashboardFrame parent;
    private JComboBox<Circle> circleCombo;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField payoutOrderField;
    private DefaultTableModel tableModel;
    private JTable memberTable;

    public MemberFrame(DashboardFrame parent) {
        this.parent = parent;

        setTitle("Manage Members");
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(buildFormPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(), BorderLayout.CENTER);

        add(mainPanel);

        loadCircleOptions();
        circleCombo.addActionListener(e -> loadMembers());
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add Member"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Circle:"), gbc);
        circleCombo = new JComboBox<>();
        gbc.gridx = 1;
        form.add(circleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Member Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(15);
        gbc.gridx = 1;
        form.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Payout Order:"), gbc);
        payoutOrderField = new JTextField(15);
        gbc.gridx = 1;
        form.add(payoutOrderField, gbc);

        JButton addBtn = new JButton("Add Member");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(addBtn, gbc);

        addBtn.addActionListener(e -> addMember());

        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Members in Selected Circle"));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Join Date", "Payout Order", "Received Payout"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        memberTable = new JTable(tableModel);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadCircleOptions() {
        try {
            CircleDAO circleDAO = new CircleDAO();
            List<Circle> circles = circleDAO.getAllCircles();
            circleCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(circles)));
            if (!circles.isEmpty()) {
                loadMembers();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load circles.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMember() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) {
            JOptionPane.showMessageDialog(this, "Please create a circle first.",
                    "No Circle Available", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String orderText = payoutOrderField.getText().trim();

        if (name.isEmpty() || orderText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in name and payout order.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int payoutOrder;
        try {
            payoutOrder = Integer.parseInt(orderText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Payout order must be a whole number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Member member = new Member();
        member.setCircleId(selectedCircle.getId());
        member.setName(name);
        member.setPhone(phone);
        member.setJoinDate(Date.valueOf(LocalDate.now()));
        member.setPayoutOrder(payoutOrder);
        member.setHasReceivedPayout(false);

        try {
            MemberDAO memberDAO = new MemberDAO();
            if (memberDAO.addMember(member)) {
                JOptionPane.showMessageDialog(this, "Member added successfully.");
                nameField.setText("");
                phoneField.setText("");
                payoutOrderField.setText("");
                loadMembers();
                parent.refreshStats();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not add member.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMembers() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) return;

        tableModel.setRowCount(0);
        try {
            MemberDAO memberDAO = new MemberDAO();
            List<Member> members = memberDAO.getMembersByCircle(selectedCircle.getId());
            for (Member m : members) {
                tableModel.addRow(new Object[]{
                        m.getId(), m.getName(), m.getPhone(), m.getJoinDate(),
                        m.getPayoutOrder(), m.isHasReceivedPayout() ? "Yes" : "No"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load members.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
