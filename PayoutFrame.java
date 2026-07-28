package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.dao.MemberDAO;
import com.microlending.dao.PayoutDAO;
import com.microlending.model.Circle;
import com.microlending.model.Member;
import com.microlending.model.Payout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

public class PayoutFrame extends JFrame {

    private final DashboardFrame parent;
    private JComboBox<Circle> circleCombo;
    private JLabel nextMemberLabel;
    private DefaultTableModel tableModel;
    private JTable payoutTable;

    public PayoutFrame(DashboardFrame parent) {
        this.parent = parent;

        setTitle("Payout Schedule");
        setSize(600, 480);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(buildFormPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(), BorderLayout.CENTER);

        add(mainPanel);

        loadCircleOptions();
        circleCombo.addActionListener(e -> {
            updateNextPayoutMember();
            loadPayoutHistory();
        });
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Generate Payout"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Circle:"), gbc);
        circleCombo = new JComboBox<>();
        gbc.gridx = 1;
        form.add(circleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Next in line:"), gbc);
        nextMemberLabel = new JLabel("-");
        nextMemberLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 1;
        form.add(nextMemberLabel, gbc);

        JButton generateBtn = new JButton("Generate Payout for Next Member");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(generateBtn, gbc);

        generateBtn.addActionListener(e -> generatePayout());

        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payout History"));

        tableModel = new DefaultTableModel(new String[]{"ID", "Member", "Amount", "Payout Date", "Month"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        payoutTable = new JTable(tableModel);
        panel.add(new JScrollPane(payoutTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadCircleOptions() {
        try {
            CircleDAO circleDAO = new CircleDAO();
            List<Circle> circles = circleDAO.getAllCircles();
            circleCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(circles)));
            if (!circles.isEmpty()) {
                updateNextPayoutMember();
                loadPayoutHistory();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load circles.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNextPayoutMember() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) return;
        try {
            MemberDAO memberDAO = new MemberDAO();
            Member next = memberDAO.getNextPayoutMember(selectedCircle.getId());
            nextMemberLabel.setText(next != null ? next.getName() : "All members have received a payout");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not determine next payout member.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePayout() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) {
            JOptionPane.showMessageDialog(this, "Please select a circle first.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            MemberDAO memberDAO = new MemberDAO();
            Member next = memberDAO.getNextPayoutMember(selectedCircle.getId());
            if (next == null) {
                JOptionPane.showMessageDialog(this, "All members in this circle have already received a payout.",
                        "No Eligible Member", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<Member> allMembers = memberDAO.getMembersByCircle(selectedCircle.getId());
            java.math.BigDecimal totalAmount = selectedCircle.getMonthlyAmount()
                    .multiply(java.math.BigDecimal.valueOf(allMembers.size()));

            Payout payout = new Payout();
            payout.setCircleId(selectedCircle.getId());
            payout.setMemberId(next.getId());
            payout.setAmount(totalAmount);
            payout.setPayoutDate(Date.valueOf(LocalDate.now()));
            payout.setMonthYear(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            PayoutDAO payoutDAO = new PayoutDAO();
            if (payoutDAO.recordPayout(payout)) {
                memberDAO.markPayoutReceived(next.getId());
                JOptionPane.showMessageDialog(this,
                        "Payout of \u20B9" + totalAmount.toPlainString() + " generated for " + next.getName() + ".");
                updateNextPayoutMember();
                loadPayoutHistory();
                parent.refreshStats();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not generate payout.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPayoutHistory() {
        Circle selectedCircle = (Circle) circleCombo.getSelectedItem();
        if (selectedCircle == null) return;

        tableModel.setRowCount(0);
        try {
            PayoutDAO payoutDAO = new PayoutDAO();
            List<Payout> payouts = payoutDAO.getPayoutsByCircle(selectedCircle.getId());
            for (Payout p : payouts) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getMemberName(), "\u20B9" + p.getAmount().toPlainString(),
                        p.getPayoutDate(), p.getMonthYear()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load payout history.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
