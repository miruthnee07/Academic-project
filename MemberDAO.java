package com.microlending.dao;

import com.microlending.DBConnection;
import com.microlending.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (circle_id, name, phone, join_date, payout_order, has_received_payout) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, member.getCircleId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getPhone());
            ps.setDate(4, member.getJoinDate());
            ps.setInt(5, member.getPayoutOrder());
            ps.setBoolean(6, member.isHasReceivedPayout());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) member.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<Member> getMembersByCircle(int circleId) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE circle_id = ? ORDER BY payout_order ASC, id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRow(rs));
                }
            }
        }
        return members;
    }

    public int getTotalMemberCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /** Returns the next member (in payout_order) within a circle who hasn't received a payout yet. */
    public Member getNextPayoutMember(int circleId) throws SQLException {
        String sql = "SELECT * FROM members WHERE circle_id = ? AND has_received_payout = FALSE " +
                "ORDER BY payout_order ASC, id ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, circleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean markPayoutReceived(int memberId) throws SQLException {
        String sql = "UPDATE members SET has_received_payout = TRUE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id"),
                rs.getInt("circle_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getDate("join_date"),
                rs.getInt("payout_order"),
                rs.getBoolean("has_received_payout")
        );
    }
}
