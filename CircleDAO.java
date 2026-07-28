package com.microlending.dao;

import com.microlending.DBConnection;
import com.microlending.model.Circle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CircleDAO {

    public boolean createCircle(Circle circle) throws SQLException {
        String sql = "INSERT INTO circles (name, monthly_amount, start_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, circle.getName());
            ps.setBigDecimal(2, circle.getMonthlyAmount());
            ps.setDate(3, circle.getStartDate());
            ps.setString(4, circle.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        circle.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Circle> getAllCircles() throws SQLException {
        List<Circle> circles = new ArrayList<>();
        String sql = "SELECT * FROM circles ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                circles.add(mapRow(rs));
            }
        }
        return circles;
    }

    public Circle getCircleById(int id) throws SQLException {
        String sql = "SELECT * FROM circles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public int getTotalCircleCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM circles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public boolean updateCircle(Circle circle) throws SQLException {
        String sql = "UPDATE circles SET name = ?, monthly_amount = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, circle.getName());
            ps.setBigDecimal(2, circle.getMonthlyAmount());
            ps.setString(3, circle.getStatus());
            ps.setInt(4, circle.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCircle(int id) throws SQLException {
        String sql = "DELETE FROM circles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Circle mapRow(ResultSet rs) throws SQLException {
        return new Circle(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("monthly_amount"),
                rs.getDate("start_date"),
                rs.getString("status")
        );
    }
}
