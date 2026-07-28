package com.microlending.ui;

import com.microlending.dao.CircleDAO;
import com.microlending.dao.ContributionDAO;
import com.microlending.dao.MemberDAO;
import com.microlending.dao.PayoutDAO;
import com.microlending.model.Payout;
import com.microlending.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardFrame extends JFrame {

    private final User loggedInUser;

    private JLabel totalCirclesValue;
    private JLabel totalMembersValue;
    private JLabel monthlyCollectionValue;
    private JLabel nextPayoutValue;
    private JLabel pendingPaymentsValue;

    public DashboardFrame(User user) {
        this.loggedInUser = user;

        setTitle("Micro Lending Circle Tracker - Dashboard");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel header = new JLabel("Micro Lending Circle Tracker", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(header, BorderLayout.NORTH);

        mainPanel.add(buildStatsPanel(), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        refreshStats();
    }

    private JPanel buildStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Overview"));

        totalCirclesValue = new JLabel("0");
        totalMembersValue = new JLabel("0");
        monthlyCollectionValue = new JLabel("\u20B90");
        nextPayoutValue = new JLabel("-");
        pendingPaymentsValue = new JLabel("0");

        addStatRow(statsPanel, "Total Circles:", totalCirclesValue);
        addStatRow(statsPanel, "Total Members:", totalMembersValue);
        addStatRow(statsPanel, "Monthly Collection:", monthlyCollectionValue);
        addStatRow(statsPanel, "Next Payout:", nextPayoutValue);
        addStatRow(statsPanel, "Pending Payments:", pendingPaymentsValue);

        return statsPanel;
    }

    private void addStatRow(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(label);
        panel.add(valueLabel);
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 6, 6));

        JButton createCircleBtn = new JButton("Create Circle");
        JButton membersBtn = new JButton("Members");
        JButton contributionsBtn = new JButton("Contributions");
        JButton payoutsBtn = new JButton("Payouts");
        JButton reportsBtn = new JButton("Reports");
        JButton logoutBtn = new JButton("Logout");

        createCircleBtn.addActionListener(e -> {
            new CircleFrame(this).setVisible(true);
        });
        membersBtn.addActionListener(e -> {
            new MemberFrame(this).setVisible(true);
        });
        contributionsBtn.addActionListener(e -> {
            new ContributionFrame(this).setVisible(true);
        });
        payoutsBtn.addActionListener(e -> {
            new PayoutFrame(this).setVisible(true);
        });
        reportsBtn.addActionListener(e -> {
            new ReportFrame(this).setVisible(true);
        });
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        buttonPanel.add(createCircleBtn);
        buttonPanel.add(membersBtn);
        buttonPanel.add(contributionsBtn);
        buttonPanel.add(payoutsBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(logoutBtn);

        return buttonPanel;
    }

    /** Reloads all dashboard stats from the database. Call this after any change elsewhere in the app. */
    public void refreshStats() {
        try {
            CircleDAO circleDAO = new CircleDAO();
            MemberDAO memberDAO = new MemberDAO();
            ContributionDAO contributionDAO = new ContributionDAO();
            PayoutDAO payoutDAO = new PayoutDAO();

            totalCirclesValue.setText(String.valueOf(circleDAO.getTotalCircleCount()));
            totalMembersValue.setText(String.valueOf(memberDAO.getTotalMemberCount()));

            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal collection = contributionDAO.getCurrentMonthCollection(currentMonth);
            monthlyCollectionValue.setText("\u20B9" + collection.toPlainString());

            Payout recentPayout = payoutDAO.getMostRecentPayout();
            nextPayoutValue.setText(recentPayout != null ? recentPayout.getMemberName() : "-");

            pendingPaymentsValue.setText(String.valueOf(contributionDAO.getPendingCount()));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load dashboard stats.\n\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
