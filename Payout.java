package com.microlending.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Payout {
    private int id;
    private int circleId;
    private int memberId;
    private String memberName;
    private BigDecimal amount;
    private Date payoutDate;
    private String monthYear;

    public Payout() {}

    public Payout(int id, int circleId, int memberId, String memberName,
                  BigDecimal amount, Date payoutDate, String monthYear) {
        this.id = id;
        this.circleId = circleId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.amount = amount;
        this.payoutDate = payoutDate;
        this.monthYear = monthYear;
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

    public Date getPayoutDate() { return payoutDate; }
    public void setPayoutDate(Date payoutDate) { this.payoutDate = payoutDate; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
}
