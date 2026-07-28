package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.dao.ContributionDAO;
import com.microlending.dao.MemberDAO;
import com.microlending.model.Circle;
import com.microlending.model.Contribution;
import com.microlending.model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

public class ContributionFrame extends JFrame {

    private final DashboardFrame parent;
    private JComboBox<Circle> circleCombo;
    private JComboBox<Member> memberCombo;
    private JTextField monthYearField;
    private DefaultTableModel tableModel;
    private JTable contributionTable;

    public ContributionFrame(DashboardFrame parent) {
        this.parent = parent;

        setTitle("Monthly Contributions");
        setSize(650, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(buildFormPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(), BorderLayout.CENTER);
        mainPanel.add(buildActionPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        loadCircleOptions();
        circleCombo.addActionListener(e -> {
            loadMemberOptions();
            loadContributions();
        });
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Record Contribution"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Circle:"), gbc);
        circleCombo = new JComboBox<>();
        gbc.gridx = 1;
        form.add(circleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Member:"), gbc);
        memberCombo = new JComboBox<>();
        gbc.gridx = 1;
        form.add(memberCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Month (YYYY-MM):"), gbc);
        monthYearField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")), 15);
        gbc.gridx = 1;
        form.add(monthYearField, gbc);

        JButton recordBtn = new JButton("Record as Pending");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(recordBtn, gbc);

        recordBtn.addActionListener(e -> recordContribution());

        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Contribution History"));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Member", "Amount", "Month", "Paid Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        contributionTable = new JTable(tableModel);
        panel.add(new JScrollPane(contributionTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel();
        JButton markPaidBtn = new JButton("Mark Selected as Paid");
        markPaidBtn.addActionListener(e -> markSelectedAsPaid());
        panel.add(markPaidBtn);
        return panel;
    }

    private void loadCircleOptions() {
        try {
            CircleDAO circleDAO = new CircleDAO();
            List<Circle> circles = circleDAO.getAllCircles();
            circleCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(circles)));
            if (!circles.isEmpty()) {
                loadMemberOptions();
                loadContributions();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load circles.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMemberOptions() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) return;
        try {
            MemberDAO memberDAO = new MemberDAO();
            List<Member> members = memberDAO.getMembersByCircle(selectedCircle.getId());
            memberCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(members)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load members.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recordContribution() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        Member selectedMember = (Member) memberCombo.getSelectedItem();
        String monthYear = monthYearField.getText().trim();

        if (selectedCircle == null || selectedMember == null || monthYear.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a circle, member, and month.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contribution contribution = new Contribution();
        contribution.setCircleId(selectedCircle.getId());
        contribution.setMemberId(selectedMember.getId());
        contribution.setAmount(selectedCircle.getMonthlyAmount());
        contribution.setMonthYear(monthYear);
        contribution.setPaidDate(null);
        contribution.setStatus("PENDING");

        try {
            ContributionDAO contributionDAO = new ContributionDAO();
            if (contributionDAO.addContribution(contribution)) {
                JOptionPane.showMessageDialog(this, "Contribution recorded as pending.");
                loadContributions();
                parent.refreshStats();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not record contribution.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markSelectedAsPaid() {
        int selectedRow = contributionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contribution row first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int contributionId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            ContributionDAO contributionDAO = new ContributionDAO();
            if (contributionDAO.markAsPaid(contributionId)) {
                loadContributions();
                parent.refreshStats();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not update contribution.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadContributions() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) return;

        tableModel.setRowCount(0);
        try {
            ContributionDAO contributionDAO = new ContributionDAO();
            List<Contribution> contributions = contributionDAO.getContributionsByCircle(selectedCircle.getId());
            for (Contribution c : contributions) {
                tableModel.addRow(new Object[]{
                        c.getId(), c.getMemberName(), "\u20B9" + c.getAmount().toPlainString(),
                        c.getMonthYear(), c.getPaidDate() != null ? c.getPaidDate() : "-", c.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load contributions.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
