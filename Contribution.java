package com.microlending.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Contribution {
    private int id;
    private int circleId;
    private int memberId;
    private String memberName; // convenience field for display in tables
    private BigDecimal amount;
    private String monthYear; // format YYYY-MM
    private Date paidDate;
    private String status; // PENDING / PAID

    public Contribution() {}

    public Contribution(int id, int circleId, int memberId, String memberName,
                         BigDecimal amount, String monthYear, Date paidDate, String status) {
        this.id = id;
        this.circleId = circleId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.amount = amount;
        this.monthYear = monthYear;
        this.paidDate = paidDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCircleId() { return circleId; }
    public void setCircleId(int circleId) { this.circleId = circleId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    public Date getPaidDate() { return paidDate; }
    public void setPaidDate(Date paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
