package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.dao.ContributionDAO;
import com.microlending.dao.MemberDAO;
import com.microlending.dao.PayoutDAO;
import com.microlending.model.Circle;
import com.microlending.model.Contribution;
import com.microlending.model.Member;
import com.microlending.model.Payout;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class ReportFrame extends JFrame {

    private JComboBox<Circle> circleCombo;
    private JTextArea reportArea;

    public ReportFrame(DashboardFrame parent) {
        setTitle("Reports");
        setSize(560, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Circle:"));
        circleCombo = new JComboBox<>();
        topPanel.add(circleCombo);
        JButton generateBtn = new JButton("Generate Report");
        topPanel.add(generateBtn);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        add(mainPanel);

        loadCircleOptions();
        generateBtn.addActionListener(e -> generateReport());
    }

    private void loadCircleOptions() {
        try {
            CircleDAO circleDAO = new CircleDAO();
            List<Circle> circles = circleDAO.getAllCircles();
            circleCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(circles)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load circles.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReport() {
        Circle circle = (Circle) circleCombo.getSelectedItem();
        if (circle == null) {
            JOptionPane.showMessageDialog(this, "Please select a circle first.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append(" CIRCLE REPORT: ").append(circle.getName()).append("\n");
        sb.append("============================================\n\n");
        sb.append("Monthly Amount : \u20B9").append(circle.getMonthlyAmount().toPlainString()).append("\n");
        sb.append("Start Date     : ").append(circle.getStartDate()).append("\n");
        sb.append("Status         : ").append(circle.getStatus()).append("\n\n");

        try {
            MemberDAO memberDAO = new MemberDAO();
            ContributionDAO contributionDAO = new ContributionDAO();
            PayoutDAO payoutDAO = new PayoutDAO();

            List<Member> members = memberDAO.getMembersByCircle(circle.getId());
            sb.append("--- MEMBERS (").append(members.size()).append(") ---\n");
            for (Member m : members) {
                sb.append(String.format("  #%d  %-20s  Order: %-3d  Payout received: %s%n",
                        m.getId(), m.getName(), m.getPayoutOrder(), m.isHasReceivedPayout() ? "Yes" : "No"));
            }

            sb.append("\n--- CONTRIBUTIONS ---\n");
            List<Contribution> contributions = contributionDAO.getContributionsByCircle(circle.getId());
            BigDecimal totalPaid = BigDecimal.ZERO;
            BigDecimal totalPending = BigDecimal.ZERO;
            for (Contribution c : contributions) {
                sb.append(String.format("  %-20s  %-7s  \u20B9%-10s  %s%n",
                        c.getMemberName(), c.getMonthYear(), c.getAmount().toPlainString(), c.getStatus()));
                if ("PAID".equals(c.getStatus())) {
                    totalPaid = totalPaid.add(c.getAmount());
                } else {
                    totalPending = totalPending.add(c.getAmount());
                }
            }
            sb.append(String.format("%n  Total Paid   : \u20B9%s%n", totalPaid.toPlainString()));
            sb.append(String.format("  Total Pending: \u20B9%s%n", totalPending.toPlainString()));

            sb.append("\n--- PAYOUT HISTORY ---\n");
            List<Payout> payouts = payoutDAO.getPayoutsByCircle(circle.getId());
            for (Payout p : payouts) {
                sb.append(String.format("  %-20s  \u20B9%-10s  %s  (%s)%n",
                        p.getMemberName(), p.getAmount().toPlainString(), p.getPayoutDate(), p.getMonthYear()));
            }
            if (payouts.isEmpty()) {
                sb.append("  No payouts generated yet.\n");
            }

        } catch (SQLException ex) {
            sb.append("\nError loading report data: ").append(ex.getMessage());
        }

        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
    }
}
