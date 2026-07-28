package com.microlending.dao;

import com.microlending.DBConnection;
import com.microlending.model.Contribution;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContributionDAO {

    public boolean addContribution(Contribution c) throws SQLException {
        String sql = "INSERT INTO contributions (circle_id, member_id, amount, month_year, paid_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getCircleId());
            ps.setInt(2, c.getMemberId());
            ps.setBigDecimal(3, c.getAmount());
            ps.setString(4, c.getMonthYear());
            ps.setDate(5, c.getPaidDate());
            ps.setString(6, c.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Contribution> getContributionsByCircle(int circleId) throws SQLException {
        List<Contribution> list = new ArrayList<>();
        String sql = "SELECT c.*, m.name AS member_name FROM contributions c " +
                "JOIN members m ON c.member_id = m.id " +
                "WHERE c.circle_id = ? ORDER BY c.month_year DESC, m.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean markAsPaid(int contributionId) throws SQLException {
        String sql = "UPDATE contributions SET status = 'PAID', paid_date = CURDATE() WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contributionId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getPendingCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM contributions WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /** Sum of all PAID contributions for the current calendar month (across all circles). */
    public BigDecimal getCurrentMonthCollection(String monthYear) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM contributions WHERE month_year = ? AND status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, monthYear);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private Contribution mapRow(ResultSet rs) throws SQLException {
        return new Contribution(
                rs.getInt("id"),
                rs.getInt("circle_id"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getBigDecimal("amount"),
                rs.getString("month_year"),
                rs.getDate("paid_date"),
                rs.getString("status")
        );
    }
}
