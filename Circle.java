package com.microlending.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Circle {
    private int id;
    private String name;
    private BigDecimal monthlyAmount;
    private Date startDate;
    private String status;

    public Circle() {}

    public Circle(int id, String name, BigDecimal monthlyAmount, Date startDate, String status) {
        this.id = id;
        this.name = name;
        this.monthlyAmount = monthlyAmount;
        this.startDate = startDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return name; // used directly in JComboBox / JList display
    }
}
