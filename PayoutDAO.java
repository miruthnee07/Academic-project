package com.microlending.dao;

import com.microlending.DBConnection;
import com.microlending.model.Payout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PayoutDAO {

    public boolean recordPayout(Payout p) throws SQLException {
        String sql = "INSERT INTO payouts (circle_id, member_id, amount, payout_date, month_year) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getCircleId());
            ps.setInt(2, p.getMemberId());
            ps.setBigDecimal(3, p.getAmount());
            ps.setDate(4, p.getPayoutDate());
            ps.setString(5, p.getMonthYear());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Payout> getPayoutsByCircle(int circleId) throws SQLException {
        List<Payout> list = new ArrayList<>();
        String sql = "SELECT p.*, m.name AS member_name FROM payouts p " +
                "JOIN members m ON p.member_id = m.id " +
                "WHERE p.circle_id = ? ORDER BY p.payout_date DESC";
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

    /** Fetches the most recently recorded payout across all circles, for the dashboard "Next Payout" style field. */
    public Payout getMostRecentPayout() throws SQLException {
        String sql = "SELECT p.*, m.name AS member_name FROM payouts p " +
                "JOIN members m ON p.member_id = m.id " +
                "ORDER BY p.payout_date DESC, p.id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private Payout mapRow(ResultSet rs) throws SQLException {
        return new Payout(
                rs.getInt("id"),
                rs.getInt("circle_id"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getBigDecimal("amount"),
                rs.getDate("payout_date"),
                rs.getString("month_year")
        );
    }
}
